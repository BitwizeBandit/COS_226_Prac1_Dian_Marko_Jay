// Dian le Roux (25147065)
// Marko de Swardt (24658562)
// Jay Macaskill (25198387)

// COS 226 (Concurrent Systems) Practical 3
// Last Updated: 6 September 2026

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public class TTASLock implements SimpleLock
{

    private final AtomicBoolean locked = new AtomicBoolean(false) ;

    // Counts only the ACTUAL calls of testAndSet() (the atomic RMW calls)
    // the number we compare against TASLock's count to demonstrate the reduction in operations
    private final AtomicLong testAndSetCount = new AtomicLong(0) ;

    private boolean testAndSet()
    {
        return locked.getAndSet(true) ;
    }

    public void lock()
    {
        while (true)
        {
            // While the lock is held, this loopmjust keeps rereading the locally cached 
            // value and generates no bus traffic at all.
            while (locked.get())
            {
                // busy wait locally until the lock LOOKS free
            }

            testAndSetCount.incrementAndGet(); // count this genuine atomic attempt
            if (!testAndSet())
            {
                return; // we won the race and now hold the lock
            }
        }
    }

    public void unlock()
    {
        locked.set(false);
    }

    @Override
    public long getTestAndSetCount()
    {
        return testAndSetCount.get();
    }

    @Override
    public void resetTestAndSetCount()
    {
        testAndSetCount.set(0);
    }

}
