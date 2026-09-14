/*
Dian le Roux (25147065)
Marko de Swardt (24658562)
Jay Macaskill (25198387)

COS 226 (Concurrent Systems) Assignment 1
An assignment exploring TTAS, MCS and CLH locks
Last Updated: 14 September 2026
*/

import java.util.concurrent.atomic.*;

public interface Lock 
{
    void lock();
    void unlock();
}

// ==== MCS LOCK ==== //

/*
This is a queue-based lock
We have list of threads waiting to enter critical section

Each thread that wants to enter the CS joins the queue and waits for the thread in front of it to unlock

Use atomic instructions to update the queue

Each thread waits/spins on its own locked field
*/

class Node
{
    volatile boolean locked = false; // whether this node is locked
    volatile Node next = null; // the next node in the list
}

/*
Flaws:
    Not reentrant (issues arise when a thread holding the lock calls lock again, it waits for itself)
    Thread can call unlock without having called lock before and will spin forever in the while (n.next == null) { }
    Busy-waiting witout yielding
    No interrupt support
    Poor liveness under contention, does not operate well under misuse
*/
class MCS implements Lock
{
    AtomicReference<Node> tail = new AtomicReference<>(null); // the tail of the queue
    ThreadLocal<Node> id = ThreadLocal.withInitial(Node::new);
    // this is the unique node for each thread, initially it is a default Node

    @Override
    public void lock()
    {
        Node n = id.get(); // gives us this node
        n.locked = false; // ensure that it indeed reset (not waiting yet)
        n.next = null; // for now there is no value after it

        Node m = tail.getAndSet(n); // let the tail be this node
        // m represents the value that is currently the tail, the value previous in line to this node

        if (m != null) // if there IS a thread that will go before this one
        {
            n.locked = true; // lock this one (it is waiting for that thread)
            m.next = n; // that thread now knows that this one will go after it
            while (n.locked) { } // while this thread is locked (waiting)
        }
    }

    @Override
    public void unlock()
    {
        Node n = id.get(); // get this node
        if (n.next == null) // if there is no one next in line
        {
            if (tail.compareAndSet(n, null)) // and if we are the tail, remove us from the queue and stop (queue empty)
                return;
            while (n.next == null) { } // if there is no one after us, wait for there to be someone after us
        }
        n.next.locked = false; // then let the next person take their turn and stop their wait
    }
}