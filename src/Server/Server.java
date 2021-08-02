package Server;

import javax.rmi.ssl.SslRMIClientSocketFactory;
import javax.rmi.ssl.SslRMIServerSocketFactory;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;


public class Server {

	public static void main(String[] args) throws RemoteException {

        System.out.println("Starting server...");

        try {
        AuctionsServer auctionsServer = new AuctionsServer();

        Login login = new Login();
		Buyer buyer = new Buyer(auctionsServer);
		Seller seller = new Seller(auctionsServer);


            //System.setProperty("java.rmi.server.hostname", "xx.xxx.xxx.xx");

            Registry login_registry = LocateRegistry.createRegistry(1098);
            Registry registry = LocateRegistry.createRegistry(1099, new SslRMIClientSocketFactory(), new SslRMIServerSocketFactory());


            login_registry.rebind("LOGIN", login);
            registry.rebind("BUY", buyer);
            registry.rebind("SELL", seller);

            System.out.println("Server Started...");

        } catch (RemoteException e){
            System.out.println("Server startup failed. " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

}
