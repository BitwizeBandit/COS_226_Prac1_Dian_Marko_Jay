/*
Dian le Roux (25147065)
Marko de Swardt (24658562)
Jay Macaskill (25198387)

COS 226 (Concurrent Systems) Assignment 1
An assignment exploring TTAS, MCS and CLH locks
Last Updated: 18 September 2026

Lock.java
*/

import java.util.concurrent.atomic.*;

class TTAS implements Lock
{
    AtomicBoolean state = new AtomicBoolean(false); //false = free, true = held

    @Override
    public void lock()
    {
        while (true)
        {
            while (state.get())
            {
               //lurking, spin whole time
            }

            if (!state.getAndSet(true)) // pouncing, try acquire
                return; //gets lock
            //not acquired, keep spinning
        }
    }

    @Override
    public void unlock()
    {
        state.set(false);
    }
}