public class FilterLock implements Lock 
{

    private final int n;
    private final VolatileInt[] level;
    private final VolatileInt[] victim;

    public FilterLock(int n) 
    {

    }

    @Override
    public void lock(int threadId) 
    {

    }

    @Override
    public void unlock(int threadId) 
    {
        
    }
}