package Server;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class Login extends UnicastRemoteObject implements Login_Interface {

    boolean reading = false; //to check if the server is currently reading a file
    FileInputStream input; // the file to copy

    public Login() throws RemoteException {
        super();
    }

    @Override
    public byte[] downloadCertificate(byte[] output){
        if(!isReading()) {
            try {
                input = new FileInputStream("auction_certificate.der");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            System.out.println("certificate transfer started...");
        }

        byte[] buffer = new byte[1024];
        try {
            reading = (input.read(buffer, 0, buffer.length)) > 0; //if there is still something to read in the file, return true


        if(isReading())
            return buffer;
        else{
            System.out.println("Transfer done!");
            input.close();
        }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean isReading() {
        return reading;
    }
}
