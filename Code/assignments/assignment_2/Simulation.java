/*
COS 226 (Concurrent Systems) Assignment 2
Concurrent Queues and Producer-Consumer Systems

Simulation.java
*/

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.concurrent.atomic.AtomicLong;

/*
Runs one complete producer-consumer execution against a chosen queue and
returns the measurements. Used by both Main (single run, printed report) and
ExperimentRunner (the full Task 3 table).
*/
public final class Simulation
{
    private Simulation() { }

    public static final class Result
    {
        public String queueName;
        public int producers;
        public int consumers;
        public int totalJobs;
        public long produced;
        public long processed;
        public int jobsNotProcessedExactlyOnce; // lost or duplicated jobs
        public int fifoViolations;
        public double executionMs;
        public double throughputJobsPerSec;
        public int[] processedPerConsumer;

        public boolean correct()
        {
            return produced == totalJobs
                && processed == totalJobs
                && jobsNotProcessedExactlyOnce == 0
                && fifoViolations == 0;
        }
    }

    public static ConcurrentQueue<Job> createQueue(String type, int capacity)
    {
        switch (type.toUpperCase())
        {
            case "BOUNDED":
            case "BOUNDEDQUEUE":
                return new BoundedQueue<>(capacity);
            case "LOCKFREE":
            case "LOCK-FREE":
            case "LOCKFREEQUEUE":
                return new LockFreeQueue<>();
            default:
                throw new IllegalArgumentException(
                    "Unknown queue type: " + type + " (expected BOUNDED or LOCKFREE)");
        }
    }

    public static Result run(String queueType, int numberOfProducers,
                             int numberOfConsumers, int totalJobs, int capacity)
        throws InterruptedException
    {
        ConcurrentQueue<Job> queue = createQueue(queueType, capacity);

        AtomicLong totalProduced = new AtomicLong(0);
        AtomicInteger tickets = new AtomicInteger(0);
        AtomicIntegerArray processedCount = new AtomicIntegerArray(totalJobs);

        // Split the work between producers. If totalJobs does not divide
        // evenly, the first (totalJobs % producers) producers make one extra.
        Thread[] producerThreads = new Thread[numberOfProducers];
        int base = totalJobs / numberOfProducers;
        int extra = totalJobs % numberOfProducers;
        int nextId = 0;
        for (int p = 0; p < numberOfProducers; p++)
        {
            int count = base + (p < extra ? 1 : 0);
            producerThreads[p] = new Thread(
                new Producer(p, nextId, count, queue, totalProduced), "producer-" + p);
            nextId += count;
        }

        Consumer[] consumers = new Consumer[numberOfConsumers];
        Thread[] consumerThreads = new Thread[numberOfConsumers];
        for (int c = 0; c < numberOfConsumers; c++)
        {
            consumers[c] = new Consumer(c, queue, totalJobs, numberOfProducers,
                                        tickets, processedCount);
            consumerThreads[c] = new Thread(consumers[c], "consumer-" + c);
        }

        long start = System.nanoTime();
        for (Thread t : producerThreads) t.start();
        for (Thread t : consumerThreads) t.start();
        for (Thread t : producerThreads) t.join();
        for (Thread t : consumerThreads) t.join();
        long end = System.nanoTime();

        Result r = new Result();
        r.queueName = queue.getClass().getSimpleName();
        r.producers = numberOfProducers;
        r.consumers = numberOfConsumers;
        r.totalJobs = totalJobs;
        r.produced = totalProduced.get();
        r.processedPerConsumer = new int[numberOfConsumers];
        for (int c = 0; c < numberOfConsumers; c++)
        {
            r.processedPerConsumer[c] = consumers[c].getProcessed();
            r.processed += consumers[c].getProcessed();
            r.fifoViolations += consumers[c].getOrderViolations();
        }
        for (int id = 0; id < totalJobs; id++)
            if (processedCount.get(id) != 1)
                r.jobsNotProcessedExactlyOnce++;

        r.executionMs = (end - start) / 1_000_000.0;
        r.throughputJobsPerSec = totalJobs / ((end - start) / 1_000_000_000.0);
        return r;
    }
}