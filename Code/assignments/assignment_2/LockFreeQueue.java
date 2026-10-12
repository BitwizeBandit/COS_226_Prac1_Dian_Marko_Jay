/*
Dian le Roux (25147065)
Marko de Swardt (24658562)
Jay Macaskill (25198387)

COS 226 (Concurrent Systems) Assignment 2
An assignment exploring Concurrent Queues and Producer-Consumer Systems
Last Updated: 12 October 2026

LockFreeQueue.java
*/

import java.util.concurrent.atomic.AtomicReference;

public class LockFreeQueue<T> implements ConcurrentQueue<T>
{
    private class Node
    {
        final T value;
        final AtomicReference<Node> next = new AtomicReference<>(null);

        Node (T value)
        {
            this.value = value;
        }
    }

    private final AtomicReference<Node> head;
    private final AtomicReference<Node> tail;

    public LockFreeQueue()
    {
        Node n = new Node(null);
        head = new AtomicReference<>(n);
        tail = new AtomicReference<>(n);
    }

    @Override
    public void enq(T item)
    {
        if (item == null) {
            throw new NullPointerException("null items are not allowed");
        }

        Node node = new Node(item);
        
        while (true)
        {
            Node last = tail.get();
            Node next = last.next.get();

            if (last == tail.get())
            {
                if (next == null) // end of list
                {
                    if(last.next.compareAndSet(null, node)) // append
                    {
                        tail.compareAndSet(last, node);
                        return;
                    }
                }
                else {
                    tail.compareAndSet(last, next);
                }
            }
        }
    }

    @Override
    public T deq()
    {
        while(true)
        {
            Node first = head.get();
            Node last = tail.get();
            Node next = first.next.get();

            if (first == head.get())
            {
                if (first == last)
                {
                    if (next == null) {
                        return null;
                    }
                    else {
                        tail.compareAndSet(last, next);
                    }
                }
                else
                {
                    T temp = next.value;
                    if (head.compareAndSet(first, next)) 
                    {
                        return temp;
                    }
                }
            }
        }
    }
}