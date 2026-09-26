public class Main 
{

    private static final int[] THREAD_COUNTS = { 2, 4, 8, 16 };
    private static final int OPERATIONS_PER_THREAD = 1000;

    // Repeat each configuration and average, to smooth out scheduling noise
    private static final int TRIALS = 5;

    public static void main(String[] args) throws InterruptedException 
    {
        System.out.println("Threads\tCoarse-Grained Time (ms)\tFine-Grained Time (ms)");

        for (int numberOfThreads : THREAD_COUNTS) 
        {
            double coarseAvg = runCoarseTrials(numberOfThreads);
            double fineAvg = runFineTrials(numberOfThreads);

            System.out.printf("%d\t\t%.3f\t\t\t%.3f%n", numberOfThreads, coarseAvg, fineAvg);
        }
    }

    private static double runCoarseTrials(int numberOfThreads) throws InterruptedException 
    {
        long total = 0;

        for (int t = 0; t < TRIALS; t++) 
        {
            CoarseList list = new CoarseList();
            total += runCoarseWorkload(list, numberOfThreads);
        }

        return (total / (double) TRIALS) / 1_000_000.0; // ns -> ms
    }

    private static double runFineTrials(int numberOfThreads) throws InterruptedException 
    {
        long total = 0;

        for (int t = 0; t < TRIALS; t++) 
        {
            FineList list = new FineList();
            total += runFineWorkload(list, numberOfThreads);
        }

        return (total / (double) TRIALS) / 1_000_000.0; // ns -> ms
    }

    private static long runCoarseWorkload(CoarseList list, int numberOfThreads)
            throws InterruptedException 
    {
        Thread[] threads = new Thread[numberOfThreads];

        long startTime = System.nanoTime();

        for (int i = 0; i < numberOfThreads; i++) 
        {
            final int threadID = i;

            threads[i] = new Thread(() -> {
                for (int j = 0; j < OPERATIONS_PER_THREAD; j++) 
                {
                    int value = (threadID * 1000) + (j % 1000);

                    if (j % 3 == 0) 
                    {
                        list.add(value);
                    } 
                    else if (j % 3 == 1) 
                    {
                        list.contains(value);
                    } 
                    else 
                    {
                        list.remove(value);
                    }
                }
            });

            threads[i].start();
        }

        for (Thread thread : threads) 
        {
            thread.join();
        }

        return System.nanoTime() - startTime;
    }

    private static long runFineWorkload(FineList list, int numberOfThreads)
            throws InterruptedException 
    {
        Thread[] threads = new Thread[numberOfThreads];

        long startTime = System.nanoTime();

        for (int i = 0; i < numberOfThreads; i++) 
        {
            final int threadID = i;

            threads[i] = new Thread(() -> {
                for (int j = 0; j < OPERATIONS_PER_THREAD; j++) 
                {
                    int value = (threadID * 1000) + (j % 1000);

                    if (j % 3 == 0) 
                    {
                        list.add(value);
                    } 
                    else if (j % 3 == 1) 
                    {
                        list.contains(value);
                    } 
                    else 
                    {
                        list.remove(value);
                    }
                }
            });

            threads[i].start();
        }

        for (Thread thread : threads) 
        {
            thread.join();
        }

        return System.nanoTime() - startTime;
    }
}