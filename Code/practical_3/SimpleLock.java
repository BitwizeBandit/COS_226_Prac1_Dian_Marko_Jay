// Dian le Roux (25147065)
// Marko de Swardt (24658562)
// Jay Macaskill (25198387)
 
// COS 226 (Concurrent Systems) Practical 3
// Common interface implemented by both TASLock and TTASLock so that
// Main.java can run our contention experiments generically over both
// lock implementations, and so both locks shows a way to count how
// many times the atomic testAndSet() operation was actually invoked/executed

// Note for Jay & Marko: we need this functionality for Task 3s "average number of testAndSet() invocations"

public interface SimpleLock 
{
 
    void lock();
 
    void unlock();
 
    // Returns how many times testAndSet() has been invoked since the
    // lock was created, or since the last resetTestAndSetCount() call
    long getTestAndSetCount();
 
    // Resetting the invocation counter to 0 before a new fresh experiment run
    void resetTestAndSetCount();
}