// Dian le Roux (25147065)
// Marko de Swardt (24658562)
// Jay Macaskill (25198387)

// COS 226 (Concurrent Systems) Practical 3
// Last Updated: 6 September 2026

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public class TTASLock {
    private final AtomicBoolean locked = new AtomicBoolean(false);
    
    private final AtomicLong testAndSetCount = new AtomicLong(0);

    private boolean testAndSet()
    {
        
    }

    public void lock()
    {
        
    }

    public void unlock()
    {
        
    }

    @Override
    public long getTestAndSetCount()
    {
        
    }

    @Override
    public void resetTestAndSetCount()
    {

    }
}
