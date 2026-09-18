/*
Dian le Roux (25147065)
Marko de Swardt (24658562)
Jay Macaskill (25198387)

COS 226 (Concurrent Systems) Assignment 1
An assignment exploring TTAS, MCS and CLH locks
Last Updated: 18 September 2026

Main.java
*/

public class Main 
{

    public static void main(String[] args) throws InterruptedException 
    {
        int numberOfThreads = 16; /*Change -- 2, 4, 8, 16*/
        int iterations = 200;

        /*Add your lock here --  TTAS, CLH, MCS*/
        Lock lockCLH = new CLH();
        Lock lockMCS = new MCS();
        Lock lockTTAS = new TTAS();

        // ------------------ MCS
        System.out.println("=== MCS ===");
        Auction auctionMCS = new Auction(AuctionUtils.generateItemName());
        Runner runnerMCS = new Runner(numberOfThreads,iterations, auctionMCS, lockMCS);
        runnerMCS.run();

        // ------------------ CLH
        System.out.println("=== CLH ===");
        Auction auctionCLH = new Auction(AuctionUtils.generateItemName());
        Runner runnerCLH = new Runner(numberOfThreads,iterations, auctionCLH, lockCLH);
        runnerCLH.run();

        //TTAS
        System.out.println("=== TTAS ===");
        Auction auctionTTAS = new Auction(AuctionUtils.generateItemName());
        Runner runnerTTAS = new Runner(numberOfThreads, iterations, auctionTTAS, lockTTAS);
        runnerTTAS.run();
    }
}