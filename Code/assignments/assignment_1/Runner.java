/*
Dian le Roux (25147065)
Marko de Swardt (24658562)
Jay Macaskill (25198387)

COS 226 (Concurrent Systems) Assignment 1
An assignment exploring TTAS, MCS and CLH locks
Last Updated: 15 September 2026

Runner.java
*/

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.ThreadLocalRandom;
/*Optional Helper Runner Class*/
public class Runner 
{

    public final int numberOfThreads;
    public final int iterations;
    public final Auction auction;
    public final Lock lock;

    private final AtomicLong totalWaitingTime = new AtomicLong(0);
    private final AtomicLong totalBidsPlaced = new AtomicLong(0);
    private AtomicInteger[] bidsWonPerBidder;

    public Runner(int numberOfThreads,int iterations,Auction auction,Lock lock) 
    {
        this.numberOfThreads = numberOfThreads;
        this.iterations = iterations;
        this.auction = auction;
        this.lock = lock;
    }

    public void run() throws InterruptedException 
    {
        Thread[] threads = new Thread[numberOfThreads];
        bidsWonPerBidder = new AtomicInteger[numberOfThreads]; // here we initialise an array for each thread

        for(int i = 0; i < numberOfThreads; i++) 
        {
            bidsWonPerBidder[i] = new AtomicInteger(0);

            final int bidderId = i;

            threads[i] = new Thread(() -> {
                bidder(bidderId);
            });
        }

        long startTime = System.nanoTime();

        for(Thread thread : threads) 
        {
            thread.start();
        }

        for(Thread thread : threads) 
        {
            thread.join();
        }

        long endTime = System.nanoTime();

        reportResults(endTime - startTime);
    }

    // here we have a bidder that locks and then enters to bid, bidders (threads) bid one at a time
    /*Defines the behaviour of an individual bidder. Note you have to decide how to incorporate your lock.*/
    public void bidder(int bidderId) 
    {
        for (int i = 0; i < iterations; i ++) // how many iterations we have of the bidding
        {

            double currentHighest = auction.getHighestBid();  // read it without the lock, thats why highestBid(in Aucion.java) must be volatile
            double increment = 1 + ThreadLocalRandom.current().nextInt(1, 10);
            double newBid = currentHighest + increment; // here we place a bid using the random increment

            long waitStart = System.nanoTime();
            lock.lock(); // ignore the error here, it's pulling from a different folder
            long acquired = System.nanoTime();
            totalWaitingTime.addAndGet(acquired - waitStart); // how long the thread waited before it was able to get the lock
            // additional measurement (can get average wait time, shows contention cost)

            try
            {
                
                auction.placeBid(bidderId, newBid);
                totalBidsPlaced.incrementAndGet();

                if (auction.getHighestBidder() == bidderId){ // double checking that it did succeed

                    bidsWonPerBidder[bidderId].incrementAndGet();
                }
            }
            finally
            {
                lock.unlock(); // same error as above, should be fine
            }
        }
    }

    /*Optional Helper: Records and reports the results of the experiment.*/
    public void reportResults(long executionTime) 
    {
        System.out.println("🏺 Item: " + auction.getItemName());
        System.out.println("🧵 Threads: " + numberOfThreads + ", Iterations/thread: " + iterations);
        System.out.println("⏱️  Total execution time (ms): " + executionTime / 1_000_000.0);
        System.out.println("📈 Total bids placed: " + totalBidsPlaced.get());
        System.out.println("🏆 Final highest bid: " + auction.getHighestBid() + " by bidder " + auction.getHighestBidder());
        System.out.println("⏳ Avg wait time per lock acquisition (ns): " + (totalWaitingTime.get() / (double) (numberOfThreads * iterations)));

        for (int i = 0; i < numberOfThreads; i++)
            System.out.println("   💰 Bidder " + i + " won " + bidsWonPerBidder[i].get() + " bids");
    }
}