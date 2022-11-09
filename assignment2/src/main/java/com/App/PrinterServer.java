package com.App;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class PrinterServer { // Server for handling printer requests

    public static void main(String[] args) throws RemoteException {
        Registry registry = LocateRegistry.createRegistry(1099);
        registry.rebind("ClientToPrinter", new PrinterToClient("Server"));
        System.out.println("Server is running");
    }    
}
