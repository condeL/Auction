package Seller;

import Server.Login_Interface;
import Server.Seller_Interface;

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

public class SellerClient {

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


            //Seller_Interface seller = (Seller_Interface) Naming.lookup("SELL");
            Seller_Interface seller = (Seller_Interface) registry.lookup("SELL");

            if (!seller.login(name)) {
                System.out.println("Login failed\n");
            } else {
                System.out.println("Seller client started...\n");


                boolean cont = true;

                while (cont) {
                    System.out.println("What do you want to do?\n(sell-list-winners-stop-exit)");
                    switch (input.next()) {
                        case "sell":

                            try {
                                System.out.println("Please enter the name of the item:");
                                String item = input.next();
                                System.out.println("Please enter the starting price:");
                                float starting = input.nextFloat();
                                System.out.println("Please enter the reserve price:");
                                float reserve = input.nextFloat();
                                System.out.println("Enter the deadline (dd/MM/20yy hh:mm):");
                                input.nextLine();
                                String deadline = input.nextLine();

                                int result_code = seller.createAuction(item, starting, reserve, name, phone, deadline);

                                switch (result_code) {
                                    case Seller_Interface.OK:
                                        System.out.println("Auction successfully added\n");
                                        System.out.println(seller.listItems());
                                        break;
                                    case Seller_Interface.INVALID:
                                        System.out.println("Error: Invalid starting numbers\n");
                                        break;
                                    case Seller_Interface.DATEERROR:
                                        System.out.println("Error: Wrong deadline format\n");
                                        break;
                                }
                            } catch (InputMismatchException e) {
                                System.out.println("Error: Please enter a number\n");
                            }
                            break;

                        case "list":
                            System.out.println(seller.listItems());
                            break;

                        case "winners":
                            System.out.println(seller.listWinners());
                            break;

                        case "stop":
                            System.out.println("Enter the id of the auction you want to stop:");
                            int id = input.nextInt();

                            int result_code = seller.stopAuction(id, name, phone);

                            switch (result_code) {
                                case Seller_Interface.OK:
                                    System.out.println("Auction successfully stopped\n");
                                    System.out.println(seller.getWinner(id));
                                    break;
                                case Seller_Interface.RESERVENOTMET:
                                    System.out.println("Reserve price not met. Do you still want to proceed? (Y for yes - anything else for no)");
                                    String choice = input.next();
                                    if (choice.equalsIgnoreCase("Y")) {
                                        seller.cancelAuction(id);
                                        System.out.println("Auction successfully stopped\n");
                                    } else {
                                        System.out.println("Operation aborted\n");
                                    }
                                    break;

                                case Seller_Interface.INVALID:
                                    System.out.println("Error: Invalid credentials\n");
                                    break;
                                case Seller_Interface.DATEERROR:
                                    System.out.println("Error: Deadline not reached\n");
                                    break;
                            }
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
