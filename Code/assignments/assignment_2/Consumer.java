/*
COS 226 (Concurrent Systems) Assignment 2
Concurrent Queues and Producer-Consumer Systems

Consumer.java
*/

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;

/*
Removes jobs with deq() and records each one as processed.
Works with any ConcurrentQueue.

How consumers know when to stop: the total workload is known up front. Before
each deq(), a consumer claims one "ticket" from a shared counter. If all
tickets are taken, the consumer is finished. Because exactly totalJobs tickets
exist and exactly totalJobs jobs are produced:
    - every produced job is eventually consumed, and
    - no consumer is ever left blocked inside a blocking deq() with no job
      coming (which would hang the BoundedQueue).

Empty queue handling: BoundedQueue.deq() blocks until a job arrives.
LockFreeQueue.deq() returns null when empty, so the consumer retries
(yielding the CPU between attempts) until it gets a real job.

Processing a job = removing it and recording it in the shared processedCount
array. A job counted exactly once means no job was lost or duplicated.
*/
public class Consumer implements Runnable
{
    private final int consumerId;
    private final ConcurrentQueue<Job> queue;
    private final int totalJobs;
    private final AtomicInteger tickets;             // shared by all consumers
    private final AtomicIntegerArray processedCount; // indexed by job id, shared

    private final int[] lastSequenceSeen;            // private, per producer
    private int processed = 0;
    private int orderViolations = 0;

    public Consumer(int consumerId, ConcurrentQueue<Job> queue, int totalJobs,
                    int numberOfProducers, AtomicInteger tickets,
                    AtomicIntegerArray processedCount)
    {
        this.consumerId = consumerId;
        this.queue = queue;
        this.totalJobs = totalJobs;
        this.tickets = tickets;
        this.processedCount = processedCount;
        this.lastSequenceSeen = new int[numberOfProducers];
        java.util.Arrays.fill(lastSequenceSeen, -1);
    }

    @Override
    public void run()
    {
        while (tickets.getAndIncrement() < totalJobs)
        {
            Job job = queue.deq();
            while (job == null) // only the lock-free queue can return null
            {
                Thread.yield();
                job = queue.deq();
            }

            processedCount.incrementAndGet(job.id); // "process" the job
            processed++;

            // FIFO check: jobs from the same producer must reach one
            // consumer in the order they were produced.
            if (job.sequence <= lastSequenceSeen[job.producerId])
                orderViolations++;
            lastSequenceSeen[job.producerId] = job.sequence;
        }
    }

    public int getConsumerId() { return consumerId; }
    public int getProcessed() { return processed; }
    public int getOrderViolations() { return orderViolations; }
}