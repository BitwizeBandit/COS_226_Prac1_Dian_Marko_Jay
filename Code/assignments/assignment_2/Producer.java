/*
COS 226 (Concurrent Systems) Assignment 2
Concurrent Queues and Producer-Consumer Systems

Producer.java
*/

import java.util.concurrent.atomic.AtomicLong;

/*
Generates its assigned number of jobs and enqueues each with enq().
Works with any ConcurrentQueue.

Job ids are unique across producers: producer p owns the id range
[firstId, firstId + jobsToProduce).
*/
public class Producer implements Runnable
{
    private final int producerId;
    private final int firstId;
    private final int jobsToProduce;
    private final ConcurrentQueue<Job> queue;
    private final AtomicLong totalProduced;

    public Producer(int producerId, int firstId, int jobsToProduce,
                    ConcurrentQueue<Job> queue, AtomicLong totalProduced)
    {
        this.producerId = producerId;
        this.firstId = firstId;
        this.jobsToProduce = jobsToProduce;
        this.queue = queue;
        this.totalProduced = totalProduced;
    }

    @Override
    public void run()
    {
        for (int i = 0; i < jobsToProduce; i++)
        {
            queue.enq(new Job(firstId + i, producerId, i));
            totalProduced.incrementAndGet();
        }
    }
}