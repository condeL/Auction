package Server;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Seller_Interface extends Remote {

    int OK = 0;
    int INVALID = 1;
    int DATEERROR = 2;
    int RESERVENOTMET = 3;


    boolean login(String username) throws RemoteException;

    int createAuction(String name, float starting, float reserve, String seller_name, String seller_phone, String deadline) throws RemoteException;

    int stopAuction(int id, String seller_name, String seller_phone) throws RemoteException;

    void cancelAuction(int id) throws RemoteException;

    String getWinner(int id)  throws RemoteException;

    String listItems() throws RemoteException;

    String listWinners() throws RemoteException;
}
