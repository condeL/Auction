package Server;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Buyer_Interface extends Remote {

    int OK = 0;
    int LOWBID = 1;
    int NOTFOUND = 2;
    int INACTIVE = 3;

    boolean login(String username) throws RemoteException;

    int placeBid(int id, float amount, String bidder_name, String bidder_phone) throws RemoteException;

    String listItems() throws RemoteException;

    String listWinners() throws RemoteException;
}
