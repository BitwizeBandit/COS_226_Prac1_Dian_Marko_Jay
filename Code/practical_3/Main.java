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

public class Main 
{

    private static final int NUMBER_OF_THREADS = 2;
    private static final int INCREMENTS_PER_THREAD = 1000000;
    private static int counter = 0;

    public static void main(String[] args) throws InterruptedException 
    {

        TASLock lock = ;/*Your lock implementation here (You may also swap out the TAS lock for your optimised lock here)*/
        Thread[] threads = new Thread[NUMBER_OF_THREADS];
        long startTime = System.nanoTime();

        for(int i = 0; i < NUMBER_OF_THREADS; i++) 
        {

            threads[i] = new Thread(() -> {

                for(int j = 0; j < INCREMENTS_PER_THREAD; j++) 
                {
                    lock.lock();
                    counter++;
                    lock.unlock();
                }
            });

            threads[i].start();
        }

        for(Thread thread : threads) 
        {
            thread.join();
        }

        long endTime = System.nanoTime();

        System.out.println("Expected counter: " + (NUMBER_OF_THREADS * INCREMENTS_PER_THREAD));
        System.out.println("Actual counter: " + counter);
        System.out.println("Execution time: " + (endTime - startTime) / 1000000 + " ms");
    }
}