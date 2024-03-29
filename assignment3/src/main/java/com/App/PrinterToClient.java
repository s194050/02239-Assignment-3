package com.App;

import java.io.*;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import org.json.simple.parser.ParseException;

import com.Domain.Parameter;
import com.Domain.Printer;
import com.AccessControl.AccessControl;

public class PrinterToClient extends UnicastRemoteObject implements ClientToPrinter { // Printer to Client interface
    private static final long serialVersionUID = 1L; // Serial version UID
    private List<Printer> printers = new ArrayList<>(); // List of printers
    private List<Parameter> parameters = new ArrayList<>();
    private UUID uniqueUserIdentifier; // Unique user identifier
    private boolean serverStatus = false; // Server status

    AccessControl accessControl = AccessControl.runACL("RBAC"); // Run the access control and initiate to RBAC
    String policy = "RBAC"; // Set the policy to RBAC

    // This will change from the Client if the user decides

    public PrinterToClient(String name) throws RemoteException {
        super(); // Call to UnicastRemoteObject constructor
    }
    
    public String print(String filename, String printer, UUID userToken) throws IOException, ParseException { // Print a file
        if (SessionAuth.validateSession(userToken)) {
            if(!serverStatus){
                return "Server is not running";
            }
            String username = SessionAuth.getUsernameFromToken(userToken);
            if (accessControl.validateUserPermissions(username, "print")) {
                System.out.println(username + ": Tried to access print and was accepted");

                for (Printer specficPrinter : printers) { // Loop through printers
                    if (specficPrinter.getPrinterName().equals(printer)) {
                        specficPrinter.setStatus("Busy"); // Set printer status to busy
                        return specficPrinter.addToqueue(filename);
                    }
                }
                return null; // Printer not found
            } else {
                System.out.println(username + ": Tried to access print and was denied");
                return "You dont have access to this operation";
            }

        } else {
            return "Session Invalid";
        }
    }

    public String queue(String printer, UUID userToken) throws IOException, ParseException { // Get queue for a printer
        if (SessionAuth.validateSession(userToken)) {
            if(!serverStatus) {
                return "Server is not running";
            }
            String username = SessionAuth.getUsernameFromToken(userToken);
            if (accessControl.validateUserPermissions(username, "queue")) {
                System.out.println(username + ": Tried to access queue and was accepted");


                for (Printer specficPrinter : printers) { // Loop through printers
                    if (specficPrinter.getPrinterName().equals(printer)) {
                        return specficPrinter.queue();
                    }
                }
                return null; // Printer not found

            } else {
                System.out.println(username + ": Tried to access queue and was denied");
                return "You dont have access to this operation";
            }
        } else {
            return "Session Invalid";
        }
    }

    public String topQueue(String printer, int job, UUID userToken) throws IOException, ParseException { //job = job number in queue to be moved to top of queue
        if (SessionAuth.validateSession(userToken)) {
            if(!serverStatus){
                return "Server is not running";
            }

            String username = SessionAuth.getUsernameFromToken(userToken);
            if (accessControl.validateUserPermissions(username, "topQueue")) {
                System.out.println(username + ": Tried to access topQueue and was accepted");


                for (Printer specficPrinter : printers) { // Loop through printers
                    if (specficPrinter.getPrinterName().equals(printer)) {
                        return specficPrinter.topQueue(job);
                    }
                }
                return null; // Printer not found

            } else {
                System.out.println(username + " tried to access topQueue and was denied");
                return "You dont have access to this operation";
            }
        } else {
            return "Session Invalid";
        }
    }

    public String Start(UUID userToken) throws IOException, ParseException { //  start the print server
        if (SessionAuth.validateSession(userToken)) {
            if(serverStatus){
                return "Server is already running";
            }
            String username = SessionAuth.getUsernameFromToken(userToken);
            if (accessControl.validateUserPermissions(username, "Start")) {
                System.out.println(username + ": Tried to access Start and was accepted");

                serverStatus = true;
                return "Server is starting";

            } else {
                System.out.println(username + ": Tried to access Start and was denied");
                return "You dont have access to this operation";
            }
        } else {
            return "Session Invalid";
        }
    }


    public String Stop(UUID userToken) throws IOException, ParseException { // stop the print server
        if (SessionAuth.validateSession(userToken)) {
            if(!serverStatus){
                return "Server is already stopped";
            }
            String username = SessionAuth.getUsernameFromToken(userToken);
            if (accessControl.validateUserPermissions(username, "Stop")) {
                System.out.println(username + ": Tried to access Stop and was accepted");
                serverStatus = false;
                return "Stopping the server";
            } else {
                System.out.println(username + ": Tried to access Stop and was denied");
                return "You dont have access to this operation";
            }
        } else {
            return "Session Invalid";
        }
    }

    public String Restart(UUID userToken) throws InterruptedException, IOException, ParseException { // restart the print server
        if (SessionAuth.validateSession(userToken)) {

            String username = SessionAuth.getUsernameFromToken(userToken);
            if (accessControl.validateUserPermissions(username, "Restart")) {
                System.out.println(username + ": Tried to access Restart and was accepted");

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

            } else {
                System.out.println(username + ": Tried to access Restart and was denied");
                return "You dont have access to this operation";
            }

        } else {
            return "Session Invalid";
        }
    }


    public String status(String printer, UUID userToken) throws IOException, ParseException { // status of the printer
        if (SessionAuth.validateSession(userToken)) {
            if(!serverStatus){
                return "Server is stopped";
            }
            String username = SessionAuth.getUsernameFromToken(userToken);
            if (accessControl.validateUserPermissions(username, "status")) {
                System.out.println(username + ": Tried to access status and was accepted");

                for (Printer printer_element : printers) {
                    if (Objects.equals(printer, printer_element.getPrinterName())) {
                        return "Status of " + printer + " : " + "\n"
                                + printer_element.getStatus() + "\n";

                    }
                }
                return "Status of " + printer + " : " + "\n"
                        + printer + " does not exist." + "\n"
                        + "Please try again. ";

            } else {
                System.out.println(username + ": Tried to access status and was denied");
                return "You dont have access to this operation";
            }
        } else {
            return "Session Invalid";
        }
    }

    public String readConfig(String parameter, UUID userToken) throws IOException, ParseException { // read the configuration file
        if (SessionAuth.validateSession(userToken)) {
            if(!serverStatus){
                return "Server is stopped";
            }
            String username = SessionAuth.getUsernameFromToken(userToken);
            if (accessControl.validateUserPermissions(username, "readConfig")) {
                System.out.println(username + ": Tried to access readConfig and was accepted");


                for (Parameter param : parameters) {
                    if (param.getParameterName().equals(parameter)) {
                        return "Value of parameter: " + parameter + " is: " + param.getParameterValue();
                    }
                }
                return null;

            } else {
                System.out.println(username + ": Tried to access readConfig and was denied");
                return "You dont have access to this operation";
            }
        } else {
            return "Session Invalid";
        }

    }


    public String setConfig(String parameter, String value, UUID userToken) throws IOException, ParseException { // set a configuration parameter
        if (SessionAuth.validateSession(userToken)) {
            if(!serverStatus){
                return "Server is stopped";
            }
            String username = SessionAuth.getUsernameFromToken(userToken);
            if (accessControl.validateUserPermissions(username, "setConfig")) {
                System.out.println(username + ": Tried to access setConfig and was accepted");


                for (Parameter param : parameters) {
                    if (param.getParameterName().equals(parameter)) {
                        param.setParameterValue(value);
                        return "Parameter " + parameter + " set to " + value;
                    }
                }
                parameters.add(new Parameter(parameter, value));
                return "Parameter " + parameter + " added with value " + value;

            } else {
                System.out.println(username + ": Tried to access setConfig and was denied");
                return "You dont have access to this operation";
            }
        } else {
            return "Session Invalid";
        }
    }


    public String getParameters() { // get all parameters
        String allParameters = "";
        for (Parameter param : parameters) {
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


    public String createUser(String username, String password, UUID userToken) throws RemoteException {
        if (SessionAuth.validateSession(userToken)) { // Only allow a user to be created if session is valid
            if(!serverStatus){
                return "Server is stopped";
            }
            try {
                // Create file if it dosen't exist. Boolean in FileWriter makes sure we append to file and don't overwrite.
                File file = new File("password.txt");
                file.createNewFile();
                boolean name_exist = false;
                Scanner myReader = new Scanner(file);

                while (myReader.hasNextLine()) {
                    String data = myReader.nextLine();
                    String name = data.substring(0, data.indexOf(':'));
                    if (username.equals(name)) {
                        name_exist = true;
                    }
                }
                myReader.close();

                if (!name_exist) {
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

            } catch (Exception e) {//Catch exception if any
                System.err.println("Error: " + e.getMessage());
            }

            return "Account with Username: " + username + " already exists." + "\n";
        } else {
            return "Session Invalid";
        }
    }


    public String login(String username, String password) throws RemoteException {
        boolean accepted = false;
        if (SessionAuth.validateUser(username)) { // Check whether user is already logged in by looking for its username in the session list
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
                if (username.equals(name)) {
                    String pw = data.substring(data.indexOf(':') + 1);
                    // Check that the password matches.
                    try {
                        accepted = StrongSecuredPassword.validatePassword(password, pw);
                    } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
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
        if (accepted) {
            uniqueUserIdentifier = SessionAuth.createSession(username);
            return "Login successful" + "\n";
        } else {
            return "Login failed, try again" + "\n";
        }
    }

    public UUID getUniqueUserIdentifier() { // Assign the user a token indicating that they are logged in
        return uniqueUserIdentifier;
    }

    public String logout(UUID userToken) { // Remove the users token from the list of valid tokens
        if (SessionAuth.removeSession(userToken)) {
            return "Logout successful" + "\n";
        } else {
            return "Logout failed, try again" + "\n";
        }

    }

    public String addUserToAccessControl(String username, String role, UUID userToken) throws FileNotFoundException, IOException, ParseException {
        if (SessionAuth.validateSession(userToken)) {
            if (!serverStatus) {
                return "Server is stopped";
            }

            // check if username exists
            boolean name_exist = false;
            File myObj = new File("password.txt");
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                String name = data.substring(0, data.indexOf(':'));
                if (username.equals(name)) {
                    name_exist = true;
                }
            }
            myReader.close();
            // 
            if (!name_exist) {
                return "User " + username + " does not exist";
            }
            
            // if user exists
            String Status = accessControl.createUser(username, role);
            if (Status.equals("true")) {
                return "User " + username + " added to access control with role " + role;
            } else if(Status.equals("Role does not exist")){ 
                return "Role: " + role + " does not exist";
            } else if (Status.equals("User already in access control")) {
                return "User " + username + " already exists";
            } else if(Status.equals("false")){
                return "User " + username + " could not be added to access control";
            } else if(Status.equals("Invalid operations")){
                return "Invalid operations given";
            } else{
                return "Unexpected Error";
            }
        } else {
            return "Session Invalid";
        }
    }

    public String deleteUserFromAccessControl(String username, UUID userToken) throws FileNotFoundException, IOException, ParseException {
        if (SessionAuth.validateSession(userToken)) {
            if(!serverStatus){
                return "Server is stopped";
            }

            String Status = accessControl.deleteUser(username);
            if (Status.equals("true")) {
                return "User " + username + " deleted from access control";
            } else if (Status.equals("false")) {
                return "User " + username + " does not exist in access control";
            } else{
                return "Unexpected Error";
            }
        } else {
            return "Session Invalid";
        }
    }

    public String changeRolePermission(String username, String role, UUID userToken)
            throws FileNotFoundException, IOException, ParseException {
        if (SessionAuth.validateSession(userToken)) {
            if (!serverStatus) {
                return "Server is stopped";
            }

            String Status = accessControl.changeUserRole(username, role);
            if (Status.equals("true")) {
                return "User " + username + " changed to role " + role;
            } else if (Status.equals("Role does not exist")) {
                return "Role: " + role + " does not exist";
            } else if (Status.equals("false")) {
                return "Role " + role + " does not exist in access control";
            } else if (Status.equals("Cannot change role when using ACLs")) {
                return "Cannot change role when using ACLs";
            } else {
                return "Unexpected error";
            }
        } else {
            return "Session Invalid";
        }
    }
    
    public String addUserFunctionInACL(String username, String function, UUID userToken)
            throws FileNotFoundException, IOException, ParseException {
        if (SessionAuth.validateSession(userToken)) {
            if (!serverStatus) {
                return "Server is stopped";
            }
            String Status = accessControl.addUserFunction(username, function);
            if (Status.equals("true")) {
                return "Added this function to this user";
            } else if (Status.equals("false")) {
                return "Cannot add this function to the user";
            } else if (Status.equals("Cannot add functions when using RBAC")) {
                return "Cannot add functions when using RBAC";
            } else if (Status.equals("Already has permission")) {
                return username + " already has permission for this function";
            } else {
                return "Unexpected error";
            }
        } else {
            return "Session Invalid";
        }
    }
    
    public String removeUserFunctionFromACL(String username, String function, UUID userToken)
    throws FileNotFoundException, IOException, ParseException {
        if (SessionAuth.validateSession(userToken)) {
            if (!serverStatus) {
                return "Server is stopped";
            }
            String Status = accessControl.removeUserFunction(username, function);
            if (Status.equals("true")) {
                return "Removed this function from the user";
            } else if (Status.equals("false")) {
                return "Cannot remove this function from the user";
            } else if (Status.equals("Cannot remove functions when using RBAC")) {
                return "Cannot remove functions when using RBAC";
            } else if(Status.equals("Does not have permission")){
                return username + " does not have permission for this function";
            } else {
                return "Unexpected error";
            }
        } else {
            return "Session Invalid";
        }
    }

    public String setACLPolicy(String policyname){ // Set the access control policy to either RBAC or ACL
        if(policyname.equals("RBAC")){
            accessControl = AccessControl.runACL(policyname); // Set the policy to RBAC
            policy = "RBAC";
            return "Set policy to RBAC";
        }else if(policyname.equals("ACL")){
            accessControl = AccessControl.runACL(policyname); // Set the policy to ACL
            policy = "ACL";
            return "Set policy to ACL";
        }else{
            return "Policy does not exist";
        }
    }

    public String getAccessControl() { // Get the current access control policy
        return policy;
    }

    public boolean isTheUserAdmin(String username) throws ParseException, IOException{ // Check if the user is an admin
        return accessControl.isUserAdmin(username);
    }
}
