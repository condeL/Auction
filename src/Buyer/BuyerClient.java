package Buyer;

import Server.Buyer_Interface;
import Server.Login_Interface;

import javax.rmi.ssl.SslRMIClientSocketFactory;
import java.io.*;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.InputMismatchException;
import java.util.Scanner;


public class BuyerClient {

    public static void main(String[] args) throws RemoteException {

        /* The host address needs to be changed to the current server's local address */
        String host = "xx.xx.xx.xxx";

        Registry login_registry;
        Registry registry;

        Scanner input = new Scanner(System.in);

        System.out.println("Please enter your name:");
        String name = input.next();
        System.out.println("Please enter your phone number:");
        String phone = input.next();

        System.out.println("Connecting to server...\n");

        try {
            login_registry = LocateRegistry.getRegistry(host, 1098);
            Login_Interface login = (Login_Interface) login_registry.lookup("LOGIN");

            System.out.println("Checking certificate...\n");

            /*verifying certificate*/
            KeyStore truststore = null;
            try {
                truststore = KeyStore.getInstance("JKS");
                truststore.load(new FileInputStream("truststore.jks"), "testpass".toCharArray());
                if(truststore.isCertificateEntry("auction_trust")){
                    System.out.println("Certificate found!\n");
                }
                else{
                    throw new Exception("Certificate error");
                }
            } catch (Exception e) {
                System.out.println("Certificate not found: " + e.getMessage() +"\n");

                System.out.println("Receiving certificate from server...\n");

                FileOutputStream newFile = null;
                try {
                    newFile = new FileOutputStream("auction_certificate2.der");

                    byte[] bt = new byte[1024];

                    do {
                        bt = login.downloadCertificate(bt);
                        if (login.isReading())
                            newFile.write(bt, 0, bt.length);
                    } while (login.isReading());

                    newFile.close();
                    System.out.println("Certificate transfer finished!\n");
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

                try {

                    truststore = KeyStore.getInstance("JKS");
                    char[] password = "testpass".toCharArray();
                    truststore.load(null, password);

                    FileOutputStream fos = new FileOutputStream("truststore.jks");
                    /* Following code taken from https://stackoverflow.com/questions/18889058/programmatically-import-ca-trust-cert-into-existing-keystore-file-without-using
                     ******************************************************************************************/

                    CertificateFactory cf = CertificateFactory.getInstance("X.509");

                    FileInputStream fis = new FileInputStream("auction_certificate2.der");
                    DataInputStream dis = new DataInputStream(fis);
                    byte[] bytes = new byte[dis.available()];
                    dis.readFully(bytes);
                    ByteArrayInputStream bais = new ByteArrayInputStream(bytes);

                    InputStream certstream = bais;
                    Certificate certificate = cf.generateCertificate(certstream);

                    /*
                     * ********************************************************************************************/

                    truststore.setCertificateEntry("auction_trust", certificate);

                    truststore.store(fos, password);

                } catch (KeyStoreException | CertificateException | IOException | NoSuchAlgorithmException ex) {
                    ex.printStackTrace();
                }
            }

            System.setProperty("javax.net.ssl.trustStore", "truststore.jks");
            System.setProperty("javax.net.ssl.trustStorePassword", "testpass");
            System.setProperty("javax.net.ssl.trustStoreType","JKS");

            registry = LocateRegistry.getRegistry(host, 1099, new SslRMIClientSocketFactory());

            //Buyer_Interface buyer = (Buyer_Interface) Naming.lookup("BUY");
            Buyer_Interface buyer = (Buyer_Interface) registry.lookup("BUY");

            if (!buyer.login(name)) {
                System.out.println("Login failed.\n");
            } else {

                System.out.println("Buyer client started...\n");

                System.out.println("Hello, " + name + " here are the items for sale");
                System.out.println(buyer.listItems());

                boolean cont = true;

                while (cont) {
                    System.out.println("What do you want to do?\n(bid-list-winners-exit)");
                    switch (input.next()) {
                        case "bid":

                            try {
                                System.out.println("Enter the item id:");
                                int id = input.nextInt();
                                System.out.println("Please enter the amount you want to bid:");
                                float bid = input.nextFloat();


                                int result_code = buyer.placeBid(id, bid, name, phone);

                                switch (result_code) {
                                    case Buyer_Interface.OK:
                                        System.out.println("Bid successfully placed\n");
                                        System.out.println(buyer.listItems());
                                        break;
                                    case Buyer_Interface.LOWBID:
                                        System.out.println("Bid lower than current bid. Please try again.\n");
                                        break;
                                    case Buyer_Interface.INACTIVE:
                                        System.out.println("Auction has ended.\n");
                                        break;
                                    case Buyer_Interface.NOTFOUND:
                                        System.out.println("Auction not found. Please try again.\n");
                                        break;
                                }
                            } catch (InputMismatchException e) {
                                System.out.println("Error: Please enter a number\n");
                            }
                            break;

                        case "list":
                            System.out.println(buyer.listItems());
                            break;

                        case "winners":
                            System.out.println(buyer.listWinners());
                            break;
                        case "exit":
                            System.out.println("Goodbye.");
                            cont = false;
                            break;
                        default:
                            System.out.println("Wrong command. Please try again.\n");

                    }
                }
            }
        }catch (RemoteException | NotBoundException e){
            System.out.println("Connection to server failed: " + e.getMessage());
        }
    }
}
