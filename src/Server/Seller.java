package Server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Seller extends UnicastRemoteObject implements Seller_Interface{

    AuctionsServer auctionsServer;

    public Seller(AuctionsServer auctionsServer) throws RemoteException {
        super();
        this.auctionsServer = auctionsServer;
    }

    @Override
    public boolean login(String username){
        System.out.println("New user login:" + username);
        System.out.println("Success!");
        return true;
    }

    @Override
    public int createAuction(String name, float starting, float reserve, String seller_name, String seller_phone, String deadline){

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/20yy hh:mm");

        if(starting < 0 || reserve < 0)
            return INVALID;
        try {
            Date time = sdf.parse(deadline);
            auctionsServer.getAuctions().add(new Auction(name, starting, reserve, seller_name,seller_phone, time));
            return OK;
        } catch (ParseException e) {
            System.out.println("Wrong date format");
            return DATEERROR;
        }
    }

    @Override
    public int stopAuction(int id, String seller_name, String seller_phone) {

        Auction auction = auctionsServer.getAuctions().get(id);
        String name = auction.getSeller_name();
        String phone = auction.getSeller_phone();

        Date deadline = auction.getTime();
        Date currentTime = new Date();


        if (seller_name.equals(name) && seller_phone.equals(phone)) {
            if (deadline.compareTo(currentTime) <= 0) {
                if(auction.checkReserve()){
                    auction.stopAuction();
                    return OK;
                } else
                    return RESERVENOTMET;
            } else
                return DATEERROR;
        }else
            return INVALID;
    }

    @Override
    public void cancelAuction(int id){
        Auction auction = auctionsServer.getAuctions().get(id);
        auction.stopAuction();
        auction.setCanceled();
    }

    @Override
    public String getWinner(int id) {
        return auctionsServer.displayWinner(id);
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
