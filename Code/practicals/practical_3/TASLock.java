// Dian le Roux (25147065)
// Marko de Swardt (24658562)
// Jay Macaskill (25198387)

// COS 226 (Concurrent Systems) Practical 3
// A practical exploring test-and-set locks
// Last Updated: 6 September 2026

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public class TASLock implements SimpleLock
{

    private final AtomicBoolean locked = new AtomicBoolean(false);
    private final AtomicLong testAndSetCount = new AtomicLong(0);

    /* Do not modify this method */
    private boolean testAndSet() 
    {
        return locked.getAndSet(true); // method essentially duplicates getAndSet, just a wrapper
    }

    // getAndSet may be conceptually modelled as follows
    /*
        private boolean getAndSet(flag)
        {
            boolean prev = L; // L is conceptually the value of locked
            L = flag;
            return L;
            // if the lock was in use previously, this returns true
            // if it was unused, this returns false
            // in either case, when we use getAndSet(true), interest is put forward
        }
    */


    // Every iteration of this loop performs one atomic RMW operation,
    // regardless of whether the lock is actually free. 
    // Under contention this is exactly what floods the bus with invalidations
    @Override
    public void lock() 
    {
        while(true){

            testAndSetCount.incrementAndGet(); // counting this invoc of testAndSet()

            if(!testAndSet())
            {
                return; // false was returned, then lock was free and we now hold it
            }
        }
    }

    @Override
    public void unlock() 
    {
        locked.set(false); // release for other threads
    }

    @Override
    public long getTestAndSetCount()
    {
        return testAndSetCount.get();
    }

    @Override
    public void resetTestAndSetCount()
    {
        testAndSetCount.set(0); // in case we need to reset the count
    }
 
    
}
