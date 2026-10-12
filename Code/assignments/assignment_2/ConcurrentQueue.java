/*
COS 226 (Concurrent Systems) Assignment 2
Concurrent Queues and Producer-Consumer Systems

ConcurrentQueue.java
*/

/*
Common interface for both queue implementations, so the same Producer and
Consumer classes can be used with either one.

deq() contract:
    - BoundedQueue: blocks until an item is available, never returns null.
    - LockFreeQueue: never blocks. Returns null if the queue is empty at the
      moment of the call (the spec allows this, the consumer must retry).
Null items are therefore illegal for both queues.
*/
public interface ConcurrentQueue<T>
{
    void enq(T item);
    T deq();
}