import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class CoarseList 
{

    private final Node head;
    private final Node tail;

    private final Lock lock = new ReentrantLock();

    public CoarseList() 
    {
        head = new Node(Integer.MIN_VALUE);
        tail = new Node(Integer.MAX_VALUE);

        head.next = tail;
    }

    public boolean add(int value) 
    {
        // Acquiring the global lock before touching any part of the list
        // Nothing below this line can run concurrently with any other add/remove/contains call
        lock.lock();

        try
        {
            // Stepping the list starting from the head node, 
            // keeping track of the node just before "curr" (pred), so that if we
            // need to insert a new node we already know where it goes
            Node pred = head;
            Node curr = pred.next;

            // Advancing while curr's value is still smaller than the value im inserting
            while(curr.value < value)
            {
                pred = curr;
                curr = curr.next;
            }

            // dupes not allowed, do nothing and report fail
            if (curr.value == value) 
            { 
                return false;
            } 
            else{

                // curr.value > value: value belongs between Pred and Curr
                // now insert a new node in between them

                Node node = new Node(value);
                node.next = curr; // New node points forward to Curr

                pred.next = node; // Pred now points to the New node
                
                return true;
            }
        }
        finally {

            // always release lock, so list doesnt permanently locks
            lock.unlock();
        }


    }

    // remove a node if its IN the list, and returning true if it was removed 
    // and false if it wasnt in list
    public boolean remove(int value) 
    {
        lock.lock();

        try {

            Node pred = head;
            Node curr = pred.next;

            // traverse, after loop we will hopefully be at valid pos
            while(curr.value < value)
            {
                pred = curr;
                curr = curr.next;
            }

            if(curr.value == value)// if found
            {
                pred.next = curr.next;
                return true;
            }
            else{ // when not present
                return false;
            }
        }
        finally{

            lock.unlock();
        }
    }

    // check if a value is present in a list, returning true if value is present
    public boolean contains(int value) 
    {
        lock.lock();

        // Read only traveling, but still needs the lock because another thread could update
        // next pointers while we travel the nodes, which is unsafe to observe concurrently

        try{
       
            Node curr = head;

            // travel
            while(curr.value < value)
            {
                curr = curr.next;
            }

            // check if node is in list
            if(curr.value == value)
            {
                return true;
            }
            else{
                return false;
            }
        }
        finally{

            lock.unlock();
        }


    }
}