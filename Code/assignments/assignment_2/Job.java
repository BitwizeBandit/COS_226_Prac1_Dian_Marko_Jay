/*
COS 226 (Concurrent Systems) Assignment 2
Concurrent Queues and Producer-Consumer Systems
 
Job.java
*/
 
/*
A single unit of work passed from a producer to a consumer.
Immutable, so it can be shared safely between threads without locking.

    id: globally unique across all producers (0 .. totalJobs-1)
    producerId: which producer made it
    sequence: the job's position in its producer's output (0, 1, 2, ...),
               used to check that the queue keeps FIFO order per producer
*/
public final class Job
{
    public final int id;
    public final int producerId;
    public final int sequence;
 
    public Job(int id, int producerId, int sequence)
    {
        this.id = id;
        this.producerId = producerId;
        this.sequence = sequence;
    }
 
    @Override
    public String toString()
    {
        return "Job#" + id + "(producer " + producerId + ", seq " + sequence + ")";
    }
}