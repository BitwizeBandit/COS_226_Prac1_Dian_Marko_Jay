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
        // TODO
        return false;
    }

    public boolean remove(int value) 
    {
        // TODO
        return false;
    }

    public boolean contains(int value) 
    {
        // TODO
        return false;
    }
}