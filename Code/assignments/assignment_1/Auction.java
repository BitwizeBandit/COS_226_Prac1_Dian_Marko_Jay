public class Auction 
{

    private final String itemName;
    private volatile double highestBid;
    private volatile int highestBidder;

    public Auction(String itemName) 
    {
        this.itemName = itemName;
        this.highestBid = 0.0;
        this.highestBidder = -1;
    }

    public String getItemName() 
    {
        return itemName;
    }

    public double getHighestBid() 
    {
        return highestBid;
    }

    public int getHighestBidder() 
    {
        return highestBidder;
    }

    public void placeBid(int bidderId, double amount) 
    {

        if(amount > highestBid) 
        {
            highestBid = amount;
            highestBidder = bidderId;
        }
    }
}