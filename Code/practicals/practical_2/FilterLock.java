public class FilterLock implements Lock 
{
    // why we use 'volatile': volatile forces every read to go to main memory and every write to be flushed immediately, 
    // so threads actually see each others updates in real time

    private final int n; // total num of threads using this lock, loops use it to check how many threads/levels needs to be checked
    private final VolatileInt[] level; // waiting rooms, threads must traverse it before getting a lock
                                      // if more than 1 thread tries to enter a level, then atleast 1 thread is blocked 
    private final VolatileInt[] victim; // records which thread most recently declared itself willing to wait at level L

    public FilterLock(int n) // constructor
    {
        this.n = n; // num of levels

        level = new VolatileInt[n]; // Each thread must pass through n−1 levels of “exclusion” to enter its CS
        victim = new VolatileInt[n]; // 

        // level[i] will track how far thread i has climbed, 0 means its not trying, higest level i wanna reach
        // victim[L] will track which thread most recently offered to "lose" at level L, so its excludede from level L

        // every thread starts at level 0
        for (int i = 0; i < n; i++) 
        {
            level[i] = new VolatileInt(0);
        }
        
       // every levels victim initialised, will get overwritten firt time any thread reaches the level
        for (int L = 0; L < n; L++) 
        {
            victim[L] = new VolatileInt(0);
        }

    }

    private boolean existsSameOrHigher(int me, int L) // garuntees at most one thread reaches the last level (Mutual Exclusion)
    {
        for (int k = 0; k < n; k++) 
        {
            if (k != me && level[k].value >= L) 
            {
                return true; //returns true if there is a lock higher than the current one
            }
        }
        return false; // if no other thread is in level L or higer
    }

    @Override
    public void lock(int threadId) 
    {
        for (int j = 1; j < n; j++) 
        {
            level[threadId].value = j; // threadId tries to get in level j
            victim[j].value = threadId; // becomes victim, sacrifices self

                                                    // victim makes at least one thread always gets through each level(deadlock freedom)
            while (existsSameOrHigher(threadId, j) && victim[j].value == threadId) 
            {
                // waits while lokced
            }

           // either because its not competing with another thread, or another thread overwrote victim[j] after it
        }
    }

    @Override
    public void unlock(int threadId) 
    {
        level[threadId].value = 0; //changes val to become unlocked, and releases the thread from every level it occupied
    }
}