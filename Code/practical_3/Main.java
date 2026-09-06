// Dian le Roux (25147065)
// Marko de Swardt (24658562)
// Jay Macaskill (25198387)
 
// COS 226 (Concurrent Systems) Practical 3
// A practical exploring test-and-set locks
// Last Updated: 6 September 2026
 
// ------------------------ our  Contention Tests XD -----------------
// Runs the shared counter workload with Both our TASLock and TTASLock over
// an increasing number of threads (2, 4, 8, 16, 32) each config
// gets repeated 5 times, and prints the avg execution time and the
// avg number of testAndSet() invocs for each config

import java.util.function.Supplier;
 
public class Main
{
 
    private static final int[] THREAD_COUNTS = {2, 4, 8, 16, 32};
    private static final int INCREMENTS_PER_THREAD = 500_000;
    private static final int RUNS_PER_CONFIG = 5;
 
    // Shared counter incremented by every thread inside the critical section. 
    // volatile is enough here (not a replacement for the lock) purely so that the final read in the main thread is
    // guaranteed to see the last writer's value after join()
    private static volatile int counter;
 
    public static void main(String[] args) throws InterruptedException
    {
        System.out.println("COS 226 Practical 3 - TAS vs TTAS Contention Experiment");
        System.out.println("Increments per thread: " + INCREMENTS_PER_THREAD);
        System.out.println("Runs per configuration: " + RUNS_PER_CONFIG);
        System.out.println();
 
        // -------- Our Single run demo
        runSingleDemo(new TASLock(), "TASLock", 2);
        System.out.println();
 
        // ------ Full contention experiment for Task 3 
        runExperiment("TASLock", TASLock::new);
        runExperiment("TTASLock", TTASLock::new);
    }
 
    // A single, simple run: mirroring what the provided skeleton originally did,
    // just with the missing lock initialisation filled in
    private static void runSingleDemo(SimpleLock lock, String name, int numThreads) throws InterruptedException
    {
        counter = 0;
        Thread[] threads = new Thread[numThreads];
        long startTime = System.nanoTime();
 
        for (int i = 0; i < numThreads; i++)
        {
            threads[i] = new Thread(() -> {
                for (int j = 0; j < INCREMENTS_PER_THREAD; j++)
                {
                    lock.lock();
                    counter++;
                    lock.unlock();
                }
            });
            threads[i].start();
        }
 
        for (Thread thread : threads)
        {
            thread.join();
        }
 
        long endTime = System.nanoTime();
 
        System.out.println("--- Single demo run: " + name + " (" + numThreads + " threads) ---");
        System.out.println("Expected counter: " + ((long) numThreads * INCREMENTS_PER_THREAD));
        System.out.println("Actual counter:   " + counter);
        System.out.println("Execution time:   " + (endTime - startTime) / 1_000_000 + " ms");
        System.out.println("testAndSet() calls: " + lock.getTestAndSetCount());
    }
 
    // Runs the full 2/4/8/16/32-thread x 5-repetition sweep for one lock
    // implementation and prints an averaged results table
    private static void runExperiment(String name, Supplier<SimpleLock> lockFactory) throws InterruptedException
    {
        System.out.println("=== " + name + " ===");
        System.out.printf("%-10s %-20s %-25s%n", "Threads", "Avg Time (ms)", "Avg testAndSet() calls");
 
        for (int numThreads : THREAD_COUNTS)
        {
            double totalTimeMs = 0;
            double totalCalls = 0;
 
            for (int run = 0; run < RUNS_PER_CONFIG; run++)
            {
                SimpleLock lock = lockFactory.get();
                counter = 0;
 
                Thread[] threads = new Thread[numThreads];
                long startTime = System.nanoTime();
 
                for (int i = 0; i < numThreads; i++)
                {
                    threads[i] = new Thread(() -> {
                        for (int j = 0; j < INCREMENTS_PER_THREAD; j++)
                        {
                            lock.lock();
                            counter++;
                            lock.unlock();
                        }
                    });

                    threads[i].start();
                }
 
                for (Thread thread : threads)
                {
                    thread.join();
                }
 
                long endTime = System.nanoTime();
 
                long expected = (long) numThreads * INCREMENTS_PER_THREAD;
                
                if (counter != expected)
                {
                    // If this ever prints, mutual exclusion has a bug, so it should never happen for a correct lock
                    System.out.println("WARNING: expected " + expected + " but got " + counter + " (mutual exclusion may be broken!)");
                }
 
                totalTimeMs += (endTime - startTime) / 1_000_000.0;
                totalCalls += lock.getTestAndSetCount();
            }
 
            double avgTimeMs = totalTimeMs / RUNS_PER_CONFIG;
            double avgCalls = totalCalls / RUNS_PER_CONFIG;
 
            System.out.printf("%-10d %-20.2f %-25.0f%n", numThreads, avgTimeMs, avgCalls);
        }
 
        System.out.println();
    }
}
 