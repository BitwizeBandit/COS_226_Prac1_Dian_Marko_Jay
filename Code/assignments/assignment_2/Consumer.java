/*
Dian le Roux (25147065)
Marko de Swardt (24658562)
Jay Macaskill (25198387)

Concurrent Queues and Producer-Consumer Systems
*/

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;

/*
Removes jobs with deq() and records each one as processed.
Works with any ConcurrentQueue

Because exactly totalJobs tickets exist and exactly totalJobs jobs are produced:
    every produced job is eventually consumed
    no consumer is ever left blocked inside a blocking deq() with no job
    coming

Processing a job = removing it and recording it in the shared processedCount array. 
a Job counted exactly once means no job was lost or duplcated
*/
public class Consumer implements Runnable
{
    private final int consumerId;
    private final ConcurrentQueue<Job> queue;
    private final int totalJobs;
    private final AtomicInteger tickets; // shared by all Consumers
    private final AtomicIntegerArray processedCount; // indexed by job id, also Shared

    private final int[] lastSequenceSeen; // private, per Producer
    private int processed = 0;
    private int orderViolations = 0;

    public Consumer(int consumerId, ConcurrentQueue<Job> queue, int totalJobs, int numberOfProducers,
                    AtomicInteger tickets, AtomicIntegerArray processedCount)
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
            while (job == null) // only the 'lock free' queue can return null
            {
                Thread.yield();
                job = queue.deq();
            }

            processedCount.incrementAndGet(job.id); // "process" the job
            processed++;

            // FIFO check: jobs from the same producer must reach one order they were produced
            if (job.sequence <= lastSequenceSeen[job.producerId])
            {
                orderViolations++;
            }

            lastSequenceSeen[job.producerId] = job.sequence;
        }
    }

    public int getConsumerId() 
    { 
        return consumerId; 
    }

    public int getProcessed() 
    { 
        return processed; 
    }

    public int getOrderViolations()
    { 
        return orderViolations; 
    }
}