package Server;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class AuctionsServer {
    ArrayList<Auction> auctions;
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/20yy hh:mm");

    AuctionsServer(){
        auctions = new ArrayList<>();

    }

    public synchronized String listActiveAuctions(){
        StringBuilder sb = new StringBuilder();

        int size = auctions.size();
        for (int i=0; i<size;i++) {
            Auction auction = auctions.get(i);
            if(auction.isActive())
                sb.append("ID:"+ i +" | Item:" + auction.getName() + " | Current bid:" + auction.getCurrent_bid() + " | Seller name:" + auction.getSeller_name() + " | Current bidder name:" + auction.getBidder_name() + " | Deadline:" + auction.getTime() + "\n");
        }
        return sb.toString();
    }

    public String listWinners(){
        StringBuilder sb = new StringBuilder();
        int size = auctions.size();
        for (int i=0; i<size;i++) {
            Auction auction = auctions.get(i);
            if(!auction.isActive())
                if(auction.isCanceled())
                    sb.append("ID:"+ i +" | Item:" + auction.getName() + " | Winning bid:" + "RESERVE PRICE NOT MET" + " | Seller name:" + auction.getSeller_name() + " | Winner name:" + "CANCELED" + "\n");
                else
                    sb.append("ID:"+ i +" | Item:" + auction.getName() + " | Winning bid:" + auction.getCurrent_bid() + " | Seller name:" + auction.getSeller_name() + " | Winner name:" + auction.getBidder_name() + "\n");
        }
        return sb.toString();
    }

    public String displayWinner(int id){
        StringBuilder sb = new StringBuilder();
        Auction auction = auctions.get(id);

        sb.append("ID:"+ id +" | Item:" + auction.getName() + " | Winning bid:" + auction.getCurrent_bid() + " | Seller name:" + auction.getSeller_name() + " | Winner name:" + auction.getBidder_name() + "\n");
        return sb.toString();
    }

    public Auction getAuction(int id){
        return auctions.get(id);
    }
    public ArrayList<Auction> getAuctions() {
        return auctions;
    }
}
