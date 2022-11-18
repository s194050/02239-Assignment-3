package com.App;
import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import org.json.simple.parser.ParseException;
import java.util.UUID;


public interface ClientToPrinter extends Remote{ // Client to Printer interface
    public String print(String filename, String printer, UUID userToken) throws RemoteException, IOException, ParseException;
    public String queue(String printer, UUID userToken) throws RemoteException, IOException, ParseException;
    public String topQueue(String printer, int job, UUID userToken) throws RemoteException, IOException, ParseException;
    public String Start(UUID userToken) throws RemoteException, IOException, ParseException;
    public String Stop(UUID userToken) throws RemoteException, IOException, ParseException;
    public String Restart(UUID userToken) throws RemoteException, InterruptedException, IOException, ParseException;
    public String status(String printer, UUID userToken) throws RemoteException, IOException, ParseException;
    public String readConfig(String parameter, UUID userToken) throws RemoteException, IOException, ParseException;
    public String setConfig(String parameter, String value, UUID userToken) throws RemoteException, IOException, ParseException;
    public String getParameters() throws RemoteException;
    public void addPrinter(String printerName) throws RemoteException;
    public String getPrinter(String printerName) throws RemoteException;
    public String getPrinters() throws RemoteException;
    public int getJobID(int jobNumber, String printerName) throws RemoteException;
    public String createUser(String username, String password, UUID userToken) throws RemoteException;
    public String login(String username, String password) throws RemoteException;
    public UUID getUniqueUserIdentifier() throws RemoteException;
    public String logout(UUID userToken) throws RemoteException;
}


