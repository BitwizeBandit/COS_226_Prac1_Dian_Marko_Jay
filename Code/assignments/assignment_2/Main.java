/*
Dian le Roux (25147065)
Marko de Swardt (24658562)
Jay Macaskill (25198387)

Concurrent Queues and Producer Consumer Systems

Usage:
    java Main [BOUNDED|LOCKFREE] [producers] [consumers] [totalJobs] [capacity]
Defaults:
    BOUNDED 4 4 12000 100
Examples:
    java Main LOCKFREE 6 2
    java Main BOUNDED 2 6 12000 100
*/

public class Main
{
    public static void main(String[] args) throws InterruptedException
    {
        String queueType = args.length > 0 ? args[0] : "BOUNDED";
        int producers = args.length > 1 ? Integer.parseInt(args[1]) : 4;
        int consumers = args.length > 2 ? Integer.parseInt(args[2]) : 4;
        int totalJobs = args.length > 3 ? Integer.parseInt(args[3]) : 12000;
        int capacity = args.length > 4 ? Integer.parseInt(args[4]) : 100;

        Simulation.Result r = Simulation.run(queueType, producers, consumers, totalJobs, capacity);

        System.out.println("Queue:                " + r.queueName
            + (r.queueName.equals("BoundedQueue") ? " (capacity " + capacity + ")" : " (unbounded)"));
        System.out.println("Producers/Consumers:  " + r.producers + " / " + r.consumers);
        System.out.println("Jobs produced:        " + r.produced);
        System.out.println("Jobs processed:       " + r.processed);
        System.out.println("Jobs not processed exactly once: " + r.jobsNotProcessedExactlyOnce);
        System.out.println("FIFO order violations:           " + r.fifoViolations);
        System.out.printf ("Total execution time: %.3f ms%n", r.executionMs);
        System.out.printf ("Throughput:           %.0f jobs/s%n", r.throughputJobsPerSec);

        for (int c = 0; c < r.consumers; c++)
            System.out.println("  Consumer " + c + " processed " + r.processedPerConsumer[c] + " jobs");
        
        System.out.println(r.correct()
            ? "RESULT: CORRECT (every produced job was processed exactly once)"
            : "RESULT: INCORRECT");
    }
}