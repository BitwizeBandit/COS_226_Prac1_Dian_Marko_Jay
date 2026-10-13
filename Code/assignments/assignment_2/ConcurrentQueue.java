/*
Dian le Roux (25147065)
Marko de Swardt (24658562)
Jay Macaskill (25198387)

Concurrent Queues and Producer-Consumer Systems

Common interface for both queue impls, so the same Producer and
Consumer classes can be used with either one

deq() contract:
    BoundedQueue: blocks until an item is available, never returns null.
    LockFreeQueue: never blocks. Returns null if the queue is empty at the
    moment of the call

Null items are therefore not allowed for Both queues
*/
public interface ConcurrentQueue<T>
{
    void enq(T item);
    T deq();
}