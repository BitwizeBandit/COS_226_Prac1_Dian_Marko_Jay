/*
Dian le Roux (25147065)
Marko de Swardt (24658562)
Jay Macaskill (25198387)

COS 226 (Concurrent Systems) Assignment 1
An assignment exploring TTAS, MCS and CLH locks
Last Updated: 14 September 2026

Main.java
*/

public class Main 
{

    public static void main(String[] args) throws InterruptedException 
    {
        int numberOfThreads = 16; /*Change -- 2, 4, 8, 16*/
        int iterations = 200;

        Auction auction =new Auction(AuctionUtils.generateItemName());
        Lock lock = new MCS(); /*Add your lock here --  TTAS, CLH, MCS*/
        Runner runner = new Runner(numberOfThreads,iterations,auction,lock);
        runner.run();
    }
}