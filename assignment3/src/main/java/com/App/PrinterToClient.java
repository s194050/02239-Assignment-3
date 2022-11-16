package com.App;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import com.Domain.Parameter;
import com.Domain.Printer;

public class PrinterToClient extends UnicastRemoteObject implements ClientToPrinter { // Printer to Client interface
    private static final long serialVersionUID = 1L; // Serial version UID
    private List<Printer> printers = new ArrayList<>(); // List of printers
    private List<Parameter> parameters = new ArrayList<>();
    private UUID uniqueUserIdentifier; // Unique user identifier


    public PrinterToClient(String name) throws RemoteException {
        super(); // Call to UnicastRemoteObject constructor
    }
    
    public String print(String filename, String printer, UUID userToken){ // Print a file
        if(SessionAuth.validateSession(userToken)){
            for (Printer specficPrinter : printers) { // Loop through printers
                if (specficPrinter.getPrinterName().equals(printer)) {
                    specficPrinter.setStatus("Busy"); // Set printer status to busy
                    return specficPrinter.addToqueue(filename);
                }
            }
            return null; // Printer not found
        }else{
            return "Session Invalid";
        }
    }

    public String queue(String printer, UUID userToken) { // Get queue for a printer
        if(SessionAuth.validateSession(userToken)){
            for (Printer specficPrinter : printers) { // Loop through printers
                if (specficPrinter.getPrinterName().equals(printer)) {
                    return specficPrinter.queue();
                }
            }
            return null; // Printer not found
        }else{
            return "Session Invalid";
        }
    }

    public String topQueue(String printer, int job, UUID userToken) { //job = job number in queue to be moved to top of queue 
        if(SessionAuth.validateSession(userToken)){
            for (Printer specficPrinter : printers) { // Loop through printers
                if (specficPrinter.getPrinterName().equals(printer)) {
                    return specficPrinter.topQueue(job);
                }
            }
            return null; // Printer not found
        }else{
            return "Session Invalid";
        }
    }

    public String Start(UUID userToken) { //  start the print server
        if(SessionAuth.validateSession(userToken)){
            return "Server is starting";
        }else{
            return "Session Invalid";
        }
    }


    public String Stop(UUID userToken) { // stop the print server
        if(SessionAuth.validateSession(userToken)){
            return "Stopping the server";
        }else{
            return "Session Invalid";
        }
    }

    public String Restart(UUID userToken) throws InterruptedException { // restart the print server
        if(SessionAuth.validateSession(userToken)){
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    System.out.println("...");
                }
            }, 0, 1000);

            Thread.sleep(5000);
            timer.cancel();

            return "Server restarted";
        }else{
            return "Session Invalid";
        }
    }


    public String status(String printer, UUID userToken) { // status of the printer
        if(SessionAuth.validateSession(userToken)){
            for (Printer printer_element : printers) {
                if (Objects.equals(printer, printer_element.getPrinterName())) {
                    return "Status of " + printer + " : " + "\n"
                            + printer_element.getStatus() + "\n";

                }
            }
            return "Status of " + printer + " : " + "\n"
                    + printer + " does not exist." + "\n"
                    + "Please try again. ";
        }else{
            return "Session Invalid";
         }
    }

    public String readConfig(String parameter, UUID userToken) { // read the configuration file
        if(SessionAuth.validateSession(userToken)){
            for(Parameter param : parameters) {
                if(param.getParameterName().equals(parameter)) {
                    return "Value of parameter: " + parameter + " is: " + param.getParameterValue();
                }
            }
            return null;
        }else{
            return "Session Invalid";
        }
        
    }


    public String setConfig(String parameter, String value, UUID userToken) { // set a configuration parameter
        if(SessionAuth.validateSession(userToken)){
            for(Parameter param : parameters) {
                if(param.getParameterName().equals(parameter)) {
                    param.setParameterValue(value);
                    return "Parameter " + parameter + " set to " + value;
                }
            }
            parameters.add(new Parameter(parameter, value));
            return "Parameter " + parameter + " added with value " + value;
        }else{
            return "Session Invalid";
        }
    }


    public String getParameters() { // get all parameters
        String allParameters = "";
        for(Parameter param : parameters) {
            allParameters += param.getParameterName() + " ";
        }
        return allParameters;
    }


    public void addPrinter(String printerName) { // Add a printer
        printers.add(new Printer(printerName));
    }


    public String getPrinter(String printerName) { // Get a printer
        for (Printer printer : printers) {
            if (printer.getPrinterName().equals(printerName)) {
                return printer.getPrinterName();
            }
        }
        return null;
    }


    public String getPrinters() { // Get all printers
        StringBuilder printerNames = new StringBuilder();
        for (Printer printer : printers) {
            printerNames.append(printer.getPrinterName()).append(" ");
        }
        return printerNames.toString();
    }


    public int getJobID(int jobNumber, String printerName) { // Get job ID
        for (Printer printer : printers) {
            if (printer.getPrinterName().equals(printerName)) {
                return printer.getJobNumber(jobNumber);
            }
        }
        return -1; // Printer is empty of jobs
    }


    public String createUser(String username, String password, UUID userToken) throws RemoteException{
        if(SessionAuth.validateSession(userToken)){ // Only allow a user to be created if session is valid
            
        try{
            // Create file if it dosen't exist. Boolean in FileWriter makes sure we append to file and don't overwrite.
            File file = new File("password.txt");
            file.createNewFile();
            boolean name_exist = false;
            Scanner myReader = new Scanner(file);

            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                String name = data.substring(0, data.indexOf(':'));
                if (username.equals(name)){
                    name_exist = true;
                }
            }
            myReader.close();

            if (!name_exist){
                FileWriter fstream = new FileWriter(file, true);
                BufferedWriter out = new BufferedWriter(fstream);
                // Write to file and make a new line.
                try {
                    String hashtext = StrongSecuredPassword.generateStorngPasswordHash(password);
                    // save the username and the hash
                    out.write(username + ":" + hashtext);
                    out.newLine();
                    //Close the output stream
                    out.close();
                    return "Account with Username: " + username + " created successfully." + "\n";
                }
                // For specifying wrong message digest algorithms
                catch (NoSuchAlgorithmException e) {
                    out.close();
                    throw new RuntimeException(e);
                }
            }

            }catch (Exception e){//Catch exception if any
              System.err.println("Error: " + e.getMessage());
            }

            return "Account with Username: " + username + " already exists." + "\n";
        }else{
            return "Session Invalid";
        }
    }


    public String login(String username, String password) throws RemoteException{
        boolean accepted = false;
        if(SessionAuth.validateUser(username)){ // Check whether user is already logged in by looking for its username in the session list
            return "User already logged in\n";
        }
        try {
            // Setup read file.
            File myObj = new File("password.txt");
            Scanner myReader = new Scanner(myObj);

            // Read through every line.
            while (myReader.hasNextLine()) {
              String data = myReader.nextLine();

              String name = data.substring(0, data.indexOf(':'));
              // Check if username is the one we are looking for
              if(username.equals(name)){
                String pw = data.substring(data.indexOf(':') + 1);
                // Check that the password matches.
                try {
                    accepted = StrongSecuredPassword.validatePassword(password, pw);
                } 
                catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
                    accepted = false;
                }
                break;
              }
            }
            myReader.close();
          } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
          }
        if(accepted){
            uniqueUserIdentifier = SessionAuth.createSession(username);
            return "Login successful" + "\n";
        }else{
            return "Login failed, try again" + "\n";
        }
    }

    public UUID getUniqueUserIdentifier() { // Assign the user a token indicating that they are logged in
        return uniqueUserIdentifier;
    }

    public String logout(UUID userToken) { // Remove the users token from the list of valid tokens
        if(SessionAuth.removeSession(userToken)){
            return "Logout successful" + "\n";
        }else{
            return "Logout failed, try again" + "\n";
        }

    }
}
    

