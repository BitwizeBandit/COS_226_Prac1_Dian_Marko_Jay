/*
Dian le Roux (25147065)
Marko de Swardt (24658562)
Jay Macaskill (25198387)

Concurrent Queues and Producer Consumer Systems

ExperimentRunner.java

Task 3: runs both queues under the three required configurations
(4P/4C balanced, 6P/2C producer heavy, 2P/6C consumer-heavy) with the Same
total workload and the SAME bounded capacity, repeats each run, and prints a
table of averages.

    java ExperimentRunner [totalJobs] [capacity] [repeats]
*/

import java.util.Locale;

public class ExperimentRunner
{
    private static final String[] QUEUES = {"BOUNDED", "LOCKFREE"};
    private static final int[][] CONFIGS = {{4, 4}, {6, 2}, {2, 6}}; // {producers, consumers}
    private static final String[] CONFIG_NAMES = {"balanced", "producer-heavy", "consumer-heavy"};

    public static void main(String[] args) throws InterruptedException
    {
        int totalJobs = args.length > 0 ? Integer.parseInt(args[0]) : 12000;
        int capacity  = args.length > 1 ? Integer.parseInt(args[1]) : 100;
        int repeats   = args.length > 2 ? Integer.parseInt(args[2]) : 5;

        // Warming up so JIT compilation does not distort the first measured runs
        for (int i = 0; i < 3; i++)
            for (String q : QUEUES)
                Simulation.run(q, 4, 4, totalJobs, capacity);

        System.out.printf(Locale.US, "Total jobs: %d, bounded capacity: %d, repeats per cell: %d%n%n", totalJobs, capacity, repeats);

        System.out.printf(Locale.US, "%-10s %-15s %9s %9s %12s %12s %10s %8s%n", "Queue", "Workload", "Producers", "Consumers", "Avg time(ms)", "Throughput/s", "Min time", "Correct");

        for (int c = 0; c < CONFIGS.length; c++)
        {
            for (String q : QUEUES)
            {
                int p = CONFIGS[c][0];
                int k = CONFIGS[c][1];
                double sumMs = 0, sumTp = 0, minMs = Double.MAX_VALUE;
                boolean allCorrect = true;
                String name = q;

                for (int i = 0; i < repeats; i++)
                {
                    Simulation.Result r = Simulation.run(q, p, k, totalJobs, capacity);
                    sumMs += r.executionMs;
                    sumTp += r.throughputJobsPerSec;
                    minMs = Math.min(minMs, r.executionMs);
                    allCorrect &= r.correct();
                    name = r.queueName;
                }

                System.out.printf(Locale.US, "%-10s %-15s %9d %9d %12.2f %12.0f %10.2f %8s%n",
                    name.replace("Queue", ""), CONFIG_NAMES[c], p, k,
                    sumMs / repeats, sumTp / repeats, minMs, allCorrect ? "yes" : "NO");
            }
        }
    }
}