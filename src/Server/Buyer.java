package Server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class Buyer extends UnicastRemoteObject implements Buyer_Interface{

    AuctionsServer auctionsServer;

    public Buyer(AuctionsServer auctionsServer) throws RemoteException {
        super();
        this.auctionsServer = auctionsServer;
    }

    @Override
    public boolean login(String username){
        System.out.println("\nNew user login:" + username);
        System.out.println("Success!");
        return true;
    }

    @Override
    public int placeBid(int id, float amount, String bidder_name, String bidder_phone){
        try {
            Auction auction = auctionsServer.getAuctions().get(id);

            if(auction.isActive()) {
                if (auction.updateBid(amount, bidder_name, bidder_phone))
                    return OK;
                else
                    return LOWBID;
            } else
                return INACTIVE;
        } catch (IndexOutOfBoundsException e){
            System.out.println("Error: auction id not found");
            return NOTFOUND;
        }

    }

    @Override
    public String listItems(){
        return auctionsServer.listActiveAuctions();
    }

    @Override
    public String listWinners(){
        return auctionsServer.listWinners();
    }
}
