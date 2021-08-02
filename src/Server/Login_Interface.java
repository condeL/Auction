package Server;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Login_Interface extends Remote {


    byte[] downloadCertificate(byte[] output) throws RemoteException;

    boolean isReading() throws RemoteException;
}
