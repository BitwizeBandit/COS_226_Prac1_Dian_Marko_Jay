/*
COS 226 (Concurrent Systems) Assignment 2
Concurrent Queues and Producer-Consumer Systems

BoundedQueue.java
*/

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/*
Bounded partial queue (Chapter 10, section 3 / textbook 10.3).

Singly linked list with a sentinel node. Two locks:
    enqLock guards the tail (enqueuers only touch the tail)
    deqLock guards the head (dequeuers only touch the head)
So an enq() and a deq() can run in parallel as long as the queue is neither
empty nor full.

Two conditions:
    notFullCondition  (on enqLock) enqueuers wait here while size == capacity
    notEmptyCondition (on deqLock) dequeuers wait here while size == 0

size is an AtomicInteger, the only variable both ends touch. The method that
changes the queue across the empty/non-empty (or full/non-full) boundary
wakes the threads waiting at the other end. To signal, it must first take
that end's lock, which is what prevents a lost wake-up: the waiter checks the
condition and calls await() while holding the same lock.
*/
public class BoundedQueue<T> implements ConcurrentQueue<T>
{
    private final ReentrantLock enqLock = new ReentrantLock();
    private final ReentrantLock deqLock = new ReentrantLock();
    private final Condition notFullCondition = enqLock.newCondition();
    private final Condition notEmptyCondition = deqLock.newCondition();

    private final AtomicInteger size = new AtomicInteger(0);
    private final int capacity;

    private volatile Node head; // always the sentinel
    private volatile Node tail; // last real node (or the sentinel if empty)

    private class Node
    {
        T value;
        volatile Node next = null;

        Node(T value)
        {
            this.value = value;
        }
    }

    public BoundedQueue(int capacity)
    {
        if (capacity <= 0)
            throw new IllegalArgumentException("capacity must be positive");
        this.capacity = capacity;
        head = new Node(null); // sentinel
        tail = head;
    }

    @Override
    public void enq(T item)
    {
        if (item == null)
            throw new NullPointerException("null items are not allowed");

        boolean mustWakeDequeuers = false;
        enqLock.lock();
        try
        {
            while (size.get() == capacity)
                notFullCondition.await(); // wait while full

            Node node = new Node(item);
            tail.next = node; // link new node (linearisation point)
            tail = node;      // swing tail
            if (size.getAndIncrement() == 0)
                mustWakeDequeuers = true; // queue went from empty to non-empty
        }
        catch (InterruptedException e)
        {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("interrupted while waiting in enq()", e);
        }
        finally
        {
            enqLock.unlock();
        }

        if (mustWakeDequeuers)
        {
            deqLock.lock();
            try
            {
                notEmptyCondition.signalAll();
            }
            finally
            {
                deqLock.unlock();
            }
        }
    }

    @Override
    public T deq()
    {
        T result;
        boolean mustWakeEnqueuers = false;
        deqLock.lock();
        try
        {
            while (size.get() == 0)
                notEmptyCondition.await(); // wait while empty

            result = head.next.value; // value of first real node
            head = head.next;         // that node becomes the new sentinel
            if (size.getAndDecrement() == capacity)
                mustWakeEnqueuers = true; // queue went from full to non-full
        }
        catch (InterruptedException e)
        {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("interrupted while waiting in deq()", e);
        }
        finally
        {
            deqLock.unlock();
        }

        if (mustWakeEnqueuers)
        {
            enqLock.lock();
            try
            {
                notFullCondition.signalAll();
            }
            finally
            {
                enqLock.unlock();
            }
        }
        return result;
    }
}