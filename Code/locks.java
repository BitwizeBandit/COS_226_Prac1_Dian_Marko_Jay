// Dian le Roux (25147065)
// Marko de Swardt (24658562)
// Jay Macaskill (25198387)

// COS 226 (Concurrent Systems) Practical 1
// A practical implementing LockOne, LockTwo and Peterson's Lock and some tests
// Last Updated: 28 July 2026

// ========================= our LOCK IMPLEMENTATIONS ============================ //

interface Lock
{
    void lock(int id);
    void unlock(int id);
}

class LockOne implements Lock
{
// LockOne algorithm works with flags only, while the other thread is still
// expressing interest, this thread will wait

    private boolean[] flag = new boolean[2];

    @Override
    public void lock(int id)
    {
        
        int j = 1 - id; // if the id is 0, other is 1, vice versa

        flag[id] = true; // 'id' wants to enter crit-section

        // if 'j'-thraed is also interested, 'id' thraed will wait until 'j' is not interested anymore
        // so 'id'-thread will wait in this 'while loop'

        while(flag[j]){
                // 'id'thread keeps checking
        }

        // 'id'thread will reach this point when 'flag[j]' becomes false
    }

// When no longer interested, this thread will just release interest
    @Override
    public void unlock(int id)
    {
        flag[id] = false; // not interested anymore, jive other thread a chance now
    }

} // This is not deadlock free in a concurrent environment, each thread could simultaneously
  // wait for the other

class LockTwo implements Lock
{
// LockTwo algorithm works with a "victim mentality" while this thread is the victim
// it will wait. When a thread locks, it elects itself as the victim immediately


    @Override
    public void lock(int id)
    {

    }

// Optionally can do validation on victim but it is usually left completely void
    @Override
    public void unlock(int id)
    {

    }

} // This is not deadlock-free in a sequential environment and can also have starvation if spinning occurs early

class PetersonsLock implements Lock
{
// Peterson's deadlock-free, starvation-free lock algorithm
    private volatile boolean[] interested = {false, false }; // Only two threads
    private volatile int victim; // These are both volatile so nothing is ever stale which helps in other places

    @Override
    public void lock(int id)
    {
        interested[id] = true; // Thread announces interest and defers
        victim = id;

        int other;
        if (id == 0)
            other = 1;
        else
            other = 0;

        while (interested[other] && victim == id) { /* Wait */}
            // This is why starvation isn't possible -- the thread always passes itself up
    } // If the other thread is interested and this thread is the victim it will wait

    @Override
    public void unlock(int id)
    {
        interested[id] = false;
    } // Simply stop expressing interest
}

// Each class has a similar structure:
// - Implements the Lock interface which has lock and unlock
// - Overrides the two functions with their own implementations

class shared_counter
{
    private int counter = 0;
    private int thread0_counter = 0;
    private int thread1_counter = 0;
    // Each thread and the whole class has a counter
    // so we can keep track of what individual threads are up to

    public void increment(int id)
    {
        counter ++;
        if (id == 0) {
            thread0_counter ++;
        }
        else thread1_counter ++;

    } // We can thus compare the values of the counters to check for
      // read/write errors

    // Some getters
    public int counter() { 
        return counter; 
    }

    public int thread0_counter() { 
        return thread0_counter; 
    }

    public int thread1_counter() { 
        return thread1_counter; 
    }
}

// this calss describes what the thread should do, imps a Runnable, itself is not a thread
// a 'Thread' obj will be created separately and be given a runnable to execute
class thread implements Runnable
{
    private int id;
    private Lock lock;
    private shared_counter counter;
    private int iterations;
    private volatile boolean running = true;

    public thread (int id, Lock lock, shared_counter counter, int iterations)
    {
        this.id = id;
        this.lock = lock;
        this.counter = counter;
        this.iterations = iterations;
    }

    @Override
    public void run()
    {

        for (int i = 0; i < iterations && running; i ++)
        {
            lock.lock(id); // Enter the critical section
            System.out.println("Thread " + id + " is in the critical section");
            
            try { counter.increment(id); } // actual shared data update
            finally
            { 
                System.out.println("Thread " + id + " exited the critical section");
                lock.unlock(id); // Exit the critical section
            }

            Thread.yield(); // Let the other thread run
        }

        int this_counter;
        if (id == 0) this_counter = counter.thread0_counter();
        else this_counter = counter.thread1_counter();

        System.out.println("Thread " + id + " completed " + iterations + " iterations");
        System.out.println("Thread " + id + "'s counter is now " + this_counter);
        System.out.println("The shared counter is " + counter.counter());
    }

    public void stop() { running = false; } // A method to let us stop running
}

// ========================= MAIN ============================ //

public class locks
{
    private static final int iterations = 50;
    public static void main(String[] args)
    throws InterruptedException // Since we work with threads there can be interrupts
    {
        test(new LockOne(), "LockOne"); // Should deadlock
        test (new LockTwo(), "LockTwo"); // One thread should starve OR deadlock depending on when spin occurs
        // So with LockTwo basically what happens is if threads lock one after the other, the last one to lock will spin indefinitely
        // since the first one doesn't revert back to set itself as the victim again
        test (new PetersonsLock(), "Peterson's Lock"); // Should work perfectly
    }

    private static void test(Lock lock, String lock_name)
    throws InterruptedException
    {
        System.out.println("\n=========");
        System.out.println("Testing: " + lock_name);
        System.out.println("=========\n");

        shared_counter counter = new shared_counter();
        thread thread0 = new thread(0, lock, counter, iterations);
        thread thread1 = new thread(1, lock, counter, iterations);

        Thread T0 = new Thread(thread0);
        Thread T1 = new Thread(thread1);

        T0.setDaemon(true);
        T1.setDaemon(true); // This lets the program end otherwise when deadlock occurs it won't exit

        long start = System.currentTimeMillis();

        T0.start();
        T1.start();
        T0.join(2000);
        T1.join(2000); // This is because if deadlock occurs and there's no join timeout the join hangs

        long end = System.currentTimeMillis();
        long duration = end - start;

        System.out.println("\n=========");
        System.out.println("RESULTS");
        System.out.println("=========\n");
        System.out.println("\tTime taken: " + duration + "ms");
        System.out.println("\tExpected count: " + (2*iterations));
        System.out.println("\tActual count: " + counter.counter());
        System.out.println("\tThread 0 increments: " + counter.thread0_counter());
        System.out.println("\tThread 1 increments: " + counter.thread1_counter());
    
        if (counter.counter() < 2*iterations)
        {
            int lost = 2*iterations - counter.counter();
            System.out.println("MISSING INCREMENTS: " + lost + " lost");
            if (lock_name.equals("LockOne") || duration > 2000) System.out.println("DEADLOCK OCCURRED");
            else System.out.println("MUTUAL EXCLUSION VIOLATED");
        }
        else if (counter.thread0_counter() == 0 || counter.thread1_counter() == 0)
        {
            System.out.println("One thread has zero increments");
            System.out.println("STARVATION OCCURRED");
        }
        else System.out.println("Both threads successful!");
    }
}