import java.util.concurrent.atomic.*;
// ==== CLH LOCK ==== //
// Queue based lock, but each thread spins on its Pred's node(until it releases), not its own

class CLH implements Lock
{

    static class CHLNode {
        volatile boolean locked = false; // true : still waiting/holding
    }

    AtomicReference<CLHNode> tail; //  point tail of the queue(point to last thread's node in queue)

    ThreadLocal<CLHNode> myNode = ThreadLocal.withInitial(CLHNode::new); // this thread's Currnt node

    ThreadLocal<CLHNode> myPred = ThreadLocal.withInitial(() -> null);  // this thread's Pred's node

    public CLH()
    {
        // sentinel node, locked = false, so the very first thread through doesn't spin at all
        tail = new AtomicReference<>(new CLHNode());
    }

    @Override
    public void lock()
    {
        CLHNode node = myNode.get();

        node.locked = true; // announce that I am (about to be) waiting
        CLHNode pred = tail.getAndSet(node); // atomically join the queue at the tail(insert in queue tail)
        myPred.set(pred);

        while (pred.locked) { } // spin locally on my Pred's node

    }

    @Override
    public void unlock() // only 1 waiting thread is waked up at a time, preventing the a 'release storm' like with TTAS
    {
        CLHNode node = myNode.get();

        node.locked = false; // notify anyone/threads what is spinning on MY node stop waiting

        myNode.set(myPred.get()); // recycle my Pred's node as my new node
    }
}