public class BakeryLock implements Lock 
{

    private final int n;
    private final VolatileBoolean[] flag;
    private final VolatileInt[] label;

    public BakeryLock(int n) // constructor
    {
        this.n = n;
        flag = new VolatileBoolean[n];
        label = new VolatileInt[n];

        //initialise the two arrays

        for (int i = 0; i < n; i ++)
        {
            flag[i] = new VolatileBoolean(false);
            label[i] = new VolatileInt(0);
        }
    }

    @Override
    public void lock(int threadId) 
    {
        int k = threadId; // just makes it easier to refer to

        flag[k].value = true; // announce interest
        int max = 0;

        for (VolatileInt i : label)
        {
            if (i.value > max) max = i.value;
        } label[k].value = max + 1; // assign this thread a ticket

        for (int i = 0; i < n; i ++)
        {
            if (i == k) continue;
            while (flag[i].value && earlier(label[i].value, i, label[k].value, k))
            { /* busy-wait while another flag is interested and definitely earlier */ }
        }
    }

    private boolean earlier(int l, int id, int l2, int id2)
    // use both the id and also the ticket to break any ties
    {
        return (l < l2) || (l == l2 && id < id2);
    }

    @Override
    public void unlock(int threadId) 
    {
        flag[threadId].value = false; // stop announcing interest
    }
}

// works with multiple threads because we have a ticketing system and an interest array
// an issue arises when we have greedy threads (that get their turn but hog it) or when we have too many
// threads and get an integer overflow because then a later thread may get an earlier ticket number or a number
// that does not represent its position properly