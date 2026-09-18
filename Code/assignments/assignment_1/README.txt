COS 226 - Concurrent Systems
Assignment 1: Concurrent Auction

- Dian le Roux (25147065)
- Marko de Swardt (24658562)
- Jay Macaskill (25198387)

=====================
FILES
=====================

Lock.java - Lock interface with lock() and unlock()
TTASLock.java - Test-Test-and-Set lock implementation
CLHLock.java - CLH queue-based lock implementation
MCSLock.java - MCS queue-based lock implementation
Auction.java - Shared auction state (contains item, highest bid, highest bidder)
AuctionUtils.java - Utility class for generating random auction item names
Runner.java - Creates/starts/joins bidder threads, runs the auction, reports results
Main.java - Entry point, runs TTAS, CLH and MCS back-to-back
Makefile - Build/run automation
concurrent_auction_results.pdf - Contains the experimental results for Task 3
README.txt - this files

=====================
HOW TO BUILD & RUN
=====================

Using the Makefile
    make - compiles all .java files
    make run - compiles and runs Main
    make clean - removes compiled .class files

Directly
    javac *.java
    java Main

To change the number of bidder threads, edit "numberOfThreads" at the top of Main.java
We tested 2, 4, 8 and 16 threads. Main.java runs all three locks for the thread count, each with an independent Auction instance

=====================
MEASUREMENTS
=====================

For each run of Runner, we report
- Total execution time
- Total number of bid attempts
- Final highest bid
- Bids won by each bidder
- Additional measurement
    - Average time (ns) spent waiting to acquire the lock
        - Isolates contention cost from work performed

=====================
AUCTION OVERVIEW
=====================

Each bidder thread repeats the following iterations times:
    1. Read the current highest bid from Auction, whereby highestBid and highestBidder are volatile
        Last committed value is read without needing a lock
    2. Compute a new bid greater than the current highest bid by random increment
    3. Acquire the lock and attempt to place the bid. placeBid applies only if the amount bidded is higher than the highest bid
       at the time it runs, if another thread raised a higher bid, the bid is rejected. This step is protected by the lock, there is no
       possibility of corrupted auction state or lost updates. Deciding what to bid is unlocked and does not affect correctness
    4. Release the lock
Bidders can lose, since not every attempt succeeds. Under higher contention more attempts are made based on a highest bid that is already stale
by the time the thread reaches the front of the lock, producing non-trivial bids won per bidder and a fairness comparison, rather than every bidder winning every attempt.

=====================
LOCKS USED
=====================

1. TTAS Lock: spins on a read of an AtomicBoolean before attempting test-and-set via getAndSet.
   Reduces contention on the atomic instruction. Lacks fairness as there is no queueing.
2. CLH Lock: queue-based lock where each thread spins on its predecessor's node. The first
   thread through never spins because a guard node is created. Nodes are recycled on unlock.
3. MCS Lock: queue-based lock where each thread spins on its own node. Each thread's node is stored per-thread
   using ThreadLocal so the same node is used between lock and unlock calls.

- None of these locks are re-entrant
- None detect misuse (such as calling unlock without a prior lock call, which may cause CLH/MCS to spin indefinitely or leave the lock in an inconsistent state)
- All locks use busy-waiting, which can cause poor performance under thread oversubscription (more threads than CPU cores)
- No interrupt support is provided, a spinning thread cannot be cancelled