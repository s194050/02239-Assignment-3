package com.App;
import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Scanner;
import java.util.UUID;
import org.json.simple.parser.ParseException;

public class Client
{
    public static void main( String[] args ) throws MalformedURLException, RemoteException, NotBoundException, InterruptedException, ParseException {
        String[] printers = {"Printer1", "Printer2", "Printer3", "Printer4", "Printer5"}; // List of printers
        ClientToPrinter client1 = (ClientToPrinter) Naming.lookup("rmi://localhost:1099/ClientToPrinter"); // Connect to server
        boolean run = true; // Used to keep the GUI running
        boolean loggedIn = false; // Keeps track of whether the user is logged in or not
        boolean ACLPolicySelected = false; // Keeps track of whether the user has selected an ACL policy or not
        UUID uniqueUserToken = null; // Used to store the UUID of the user
        Scanner scanner = new Scanner(System.in);
        String accessControl = ""; // Used to store the name of the access control policy
        // Commonly used variables
        int selection;
        int job = 0;
        String username = "";
        String password = "";
        String printer = "";
        String parameter = "";
        String output = "";
        String serverStatus = "";
        String temp_username = "";
        String temp_password = "";
        String temp_role = "";

        for (int i = 0; i < printers.length; i++) { // Add printers
            client1.addPrinter(printers[i]);
        }

        while (run) { // Run the main print functions of the server
            try {

                while (!ACLPolicySelected) {
                    // handle access control policy selection
                    System.out.println("Please select an option for: \n 1: Access Control List \n 2: Role Based Access Control");
                    selection = Integer.parseInt(scanner.next() + scanner.nextLine());
                    switch (selection) {
                        case 1:
                            System.out.println(client1.setACLPolicy("ACL"));
                            ACLPolicySelected = true;
                            break;
                        case 2:
                            System.out.println(client1.setACLPolicy("RBAC"));
                            ACLPolicySelected = true;
                            break;
                        default:
                            System.out.println("Invalid selection");
                            break;
                    }
                }

                while (!loggedIn) {
                    // Handle the login process
                
                    System.out.println("Welcome to the print server \n 1: Login \n 2: Exit");
                    selection = Integer.parseInt(scanner.next() + scanner.nextLine()); // Get the user input

                    switch(selection){ // Handle the selection
                        case 1:
                            System.out.println("Enter username");
                            username = scanner.next() + scanner.nextLine();
                            System.out.println("Enter password");
                            password = scanner.next() + scanner.nextLine();
                            String outputOfLogin = client1.login(username, password);
                            if (outputOfLogin.equals("Login successful" + "\n")) { // If the login was successful, allow access to the printserver
                                System.out.println(outputOfLogin);
                                loggedIn = true;
                                uniqueUserToken = client1.getUniqueUserIdentifier(); // Assign token to the user
                            } // Otherwise break, and allow the user to try again
                            else {
                                System.out.println(outputOfLogin);
                            }
                            break;
                        case 2:
                            // Hard exit the program
                            run = false;
                            System.out.println("Thanks for using the print server");
                            System.exit(0);
                    }
                }
                if (run == false) { // If the user has exited the program, break the loop
                    break;
                }
                accessControl = client1.getAccessControl(); // Get the access control policy

                System.out.print("Server Options: \n \t\t 1: Start Server \t\t\t 2: Stop Server \t\t\t 3: Restart Server \n" +
                        "Printer Functions: \n \t\t 4: Print file \n \t\t 5: Print the job queue of a specific printer \n" +
                        "\t\t 6: Move a job on a specfic printer to the top of the queue\n" +
                        "\t\t 7: Get status of a printer\n Config Options: \n \t\t 8: Read a config parameter\n" +
                        "\t\t 9: Set a config parameter \n10: Exit Server GUI \n");

                if (username.equals("root")) { // If the user is root, allow them to create users
                    System.out.println("11: Create a user\n");
                }
                
                if (client1.isTheUserAdmin(username)) { // If the user is admin, allow them to create users
                    System.out.println("\nAccess Control Options:\n \t \t 12: Add a user to the access control\n \t\t" +
                        " 13: Remove a user from the access control\n \t \t 14: Change a user's role in the access control");
                }

                selection = Integer.parseInt(scanner.next() + scanner.nextLine()); // Get the user input

                switch (selection) { // Handle the selection
                    case 1:
                        serverStatus = client1.Start(uniqueUserToken);
                        if (serverStatus.equals("Server is starting")) { // Check if the server is already running
                            System.out.println(serverStatus); // Start the server
                            break;
                        } else {
                            System.out.println(serverStatus); // Otherwise, print the error message
                            break;
                        }

                    case 2:
                        serverStatus = client1.Stop(uniqueUserToken);
                        if (serverStatus.equals("Stopping the server")) { // Check if the server is not running
                            System.out.println(serverStatus); // Stop the server
                            break;
                        } else {
                            System.out.println(serverStatus); // Otherwise, print the error message
                            break;
                        }

                    case 3:
                        serverStatus = client1.Restart(uniqueUserToken);
                        if (serverStatus.equals("Server restarted")) {
                            System.out.println("Restarting server\n");
                            System.out.println(serverStatus); // Restart the server
                            break;
                        } else {
                            System.out.println(serverStatus); // Otherwise, print the error message
                            break;
                        }

                    case 4: 
                        getAvailablePrinters(client1, scanner); // Get available printers
                        System.out.println("Enter the name of the printer you want to print on: ");

                        printer = scanner.next() + scanner.nextLine();
                        if (client1.getPrinter(printer) == null) { // If the printer doesn't exist, break
                            System.out.println("Printer does not exist, try again\n");
                            break;
                        }

                        System.out.println("Enter the name of the file you want to print: ");

                        String filename = scanner.next() + scanner.nextLine(); 

                        output = client1.print(filename, printer, uniqueUserToken); // Print the file
                        if(output.equals("Session Invalid")){ // If the session is invalid, break
                            System.out.println("Session expired, please login again\n");
                            loggedIn = false;
                            break;
                        }else{
                            System.out.println(output);
                            break;
                        }
                        

                    case 5:
                        getAvailablePrinters(client1, scanner); // Get available printers
                        System.out.println("Enter the name of the printer you want to see the job queue of: ");
                        printer = scanner.next() + scanner.nextLine();

                        if (client1.getPrinter(printer) == null) { // Check if the printer exists
                            System.out.println("Printer does not exist, try again\n");
                            break;
                        }

                        output = client1.queue(printer, uniqueUserToken); // Get the job queue
                        if(output.equals("Session Invalid")){ // If the session is invalid, break
                            System.out.println("Session expired, please login again\n");
                            loggedIn = false;
                            break;
                        }else{
                            System.out.println(output);
                            break;
                        }
                        
                    case 6:
                        getAvailablePrinters(client1, scanner); // Get available printers
                        System.out.println("Enter the name of the printer you want to change the job queue of: ");
                        printer = scanner.next() + scanner.nextLine();
                        if (client1.getPrinter(printer) == null) { // Check if the printer exists
                            System.out.println("Printer does not exist, try again\n");
                            break;
                        }

                        getAvailableJobs(client1, scanner, printer, uniqueUserToken); // Get available jobs
                        System.out.println("Enter the job number you want to move to the top of the queue: ");
                        try {
                            job = Integer.parseInt(scanner.next() + scanner.nextLine());
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid input, try again\n"); // If not an integer allow user to try again
                            break;
                        }

                        if (client1.getJobID(job, printer) == -1) { // Check if the job exists
                            System.out.println("Job does not exist, try again\n");
                            break;
                        }

                        output = client1.topQueue(printer, job, uniqueUserToken); // Move the job to the top of the queue
                        if(output.equals("Session Invalid")){ // If the session is invalid, break
                            System.out.println("Session expired, please login again\n");
                            loggedIn = false;
                            break;
                        }else{
                            System.out.println(output);
                            break;
                        }
                       
                    case 7:
                        getAvailablePrinters(client1, scanner);
                        System.out.println("Enter the name of the printer you want to check status of: ");
                        printer = scanner.next() + scanner.nextLine();
                        output = client1.status(printer, uniqueUserToken); // Get the status of the printer
                        if(output.equals("Session Invalid")){ // If the session is invalid, break
                            System.out.println("Session expired, please login again\n");
                            loggedIn = false;
                            break;
                        }else{
                            System.out.println(output);
                            break;
                        }
                        
                    case 8:
                        getAvailableParameters(client1, scanner);
                        System.out.println("Enter the name of the config parameter you want to read: ");
                        parameter = scanner.next() + scanner.nextLine();

                        output = client1.readConfig(parameter, uniqueUserToken); // Read the config parameter
                        if(output.equals("Session Invalid")){ // If the session is invalid, break
                            System.out.println("Session expired, please login again\n");
                            loggedIn = false;
                            break;
                        }else{
                            System.out.println(output);
                            break;
                        }
                       
                    case 9:
                        getAvailableParameters(client1, scanner);
                        System.out.println("Enter the name of the config parameter you want to set: \n " +
                                "If you want to add a new parameter, enter the name of the parameter you want to add");
                        parameter = scanner.next() + scanner.nextLine();
                        System.out.println("Enter the value you want to set the config parameter to: ");

                        String value = scanner.next() + scanner.nextLine();
                        output = client1.setConfig(parameter, value, uniqueUserToken); // Set the config parameter
                        if(output.equals("Session Invalid")){ // If the session is invalid, break
                            System.out.println("Session expired, please login again\n");
                            loggedIn = false;
                            break;
                        }else{
                            System.out.println(output);
                            break;
                        }
                        
                    case 10:
                            run = false;
                            System.out.println(client1.logout(uniqueUserToken)); // Logout
                            System.out.println("Thanks for using the print server");
                            System.out.println("Exiting...");
                            System.exit(0);
                    case 11:
                        if (!username.equals("root")) {
                            // this error message can give information to a hacker
                            // System.out.println("You do not have permission to use this command");
                            break;
                        }
                        System.out.println("Enter username");
                        temp_username = scanner.next() + scanner.nextLine();
                        System.out.println("Enter password");
                        temp_password = scanner.next() + scanner.nextLine();
                        System.out.println(client1.createUser(temp_username, temp_password, uniqueUserToken)); // Create user
                        break;
                    case 12:
                        accessControl = client1.getAccessControl(); // Update the chosen access control
                        if (!client1.isTheUserAdmin(username)) { // Check if the user is an admin
                            System.out.println("Invalid selection\n");
                            break;
                        }
                        if(accessControl.equals("RBAC")){ // If the access control is RBAC
                            System.out.println("Enter the username of the user you want to add to the access control");
                            temp_username = scanner.next() + scanner.nextLine();
                            System.out.println("Enter the name of the role you want to add the user to");
                            temp_role = scanner.next() + scanner.nextLine();
                            System.out.println(client1.addUserToAccessControl(temp_username, temp_role, uniqueUserToken)); // Add user to access control
                            break;
                        }else if(accessControl.equals("ACL")){ // If the access control is ACL
                            System.out.println("Enter the username of the user you want to add to the access control");
                            temp_username = scanner.next() + scanner.nextLine();
                            System.out.println("Enter the operations you want to add the user to (seperate using ; and dont add spaces)");
                            String temp_operations = scanner.next() + scanner.nextLine();
                            System.out.println(client1.addUserToAccessControl(temp_username, temp_operations, uniqueUserToken)); // Add user to access control
                            break;
                        }else{
                            System.out.println("Unexpected Error\n");
                            break;
                        }
                    case 13:
                        accessControl = client1.getAccessControl(); // Update the chosen access control
                        if (!client1.isTheUserAdmin(username)) { // Check if the user is an admin
                            System.out.println("Invalid selection\n");
                            break;
                        }
                        if(accessControl.equals("RBAC")){ //If the access control is RBAC
                            System.out.println("Enter the username of the user you want to delete from the access control");
                            temp_username = scanner.next() + scanner.nextLine();
                            System.out.println(client1.deleteUserFromAccessControl(temp_username, uniqueUserToken)); // Delete user
                            break;
                        }else if(accessControl.equals("ACL")){ // If the access control is ACL
                            System.out.println("Enter the username of the user you want to remove from to the access control");
                            temp_username = scanner.next() + scanner.nextLine();
                            System.out.println(client1.deleteUserFromAccessControl(temp_username, uniqueUserToken)); // Delete user
                             break;
                        }else {
                            System.out.println("Unexpected Error\n");
                            break;
                        }
                    case 14:
                        accessControl = client1.getAccessControl(); // Update the chosen access control
                        if (!client1.isTheUserAdmin(username)) { // Check if the user is an admin
                            System.out.println("Invalid selection\n");
                            break;
                        }
                        if(accessControl.equals("RBAC")){ // If the access control is RBAC
                            System.out.println("Enter the username of the user you want to change the role of ");
                            temp_username = scanner.next() + scanner.nextLine();
                            System.out.println("Enter the name of the role you want to change the users permissions to");
                            temp_role = scanner.next() + scanner.nextLine();
                            System.out.println(client1.changeRolePermission(temp_username, temp_role, uniqueUserToken)); // Delete user
                            break;
                        }else if(accessControl.equals("ACL")){ // If the access control is ACL
                            System.out.println("Do you want to remove or add a function to the user? (remove/add)");
                            String choice = scanner.next() + scanner.nextLine();
                            if(choice.equals("remove")){ // If the user wants to remove a function
                                System.out.println("Enter the username of the user you want to remove a function from");
                                temp_username = scanner.next() + scanner.nextLine();
                                System.out.println("Enter the function you want to remove from the user");
                                String temp_function = scanner.next() + scanner.nextLine();
                                System.out.println(client1.removeUserFunctionFromACL(temp_username, temp_function, uniqueUserToken));
                                break;
                            }else if(choice.equals("add")){ // If the user wants to add a function
                                System.out.println("Enter the username of the user you want to add a function to");
                                temp_username = scanner.next() + scanner.nextLine();
                                System.out.println("Enter the function you want to add to the user");
                                String temp_function = scanner.next() + scanner.nextLine();
                                System.out.println(client1.addUserFunctionInACL(temp_username, temp_function, uniqueUserToken));
                                break;
                            }else{
                                System.out.println("Invalid selection\n");
                                break;
                            }
                        }else{
                            System.out.println("Unexpected Error\n");
                            break;
                        }
                    default:
                        System.out.println("Invalid selection\n");
                        break;
                }
                }catch (NumberFormatException | IOException e){
                    System.out.println("Invalid input, try again\n");
                }
        }
    }


    public static void getAvailablePrinters(ClientToPrinter client, Scanner scanner) throws MalformedURLException, RemoteException, NotBoundException {
        // Function to output all available printers
        String available;
        String printers = "";
        System.out.println("Do you want a list of available printers? [y/n]");
        available = scanner.nextLine();
        if (available.equals("y")) {
            printers = client.getPrinters();
        }
        System.out.println(printers);
    }

    public static void getAvailableJobs(ClientToPrinter client, Scanner scanner, String printerName, UUID uniqueUserToken) throws MalformedURLException, RemoteException, NotBoundException, IOException, ParseException {
        // Function to output all available jobs
        String available;
        String jobs = "";
        System.out.println("Do you want a list of available jobs? [y/n]");
        available = scanner.nextLine();
        if (available.equals("y")) {
            jobs = client.queue(printerName, uniqueUserToken);
        }
        System.out.println(jobs);
    }

    public static void getAvailableParameters(ClientToPrinter client, Scanner scanner) throws MalformedURLException, RemoteException, NotBoundException {
        // Function to output all available parameters
        String available;
        String parameters = "";
        System.out.println("Do you want a list of available parameters? [y/n]");
        available = scanner.nextLine();
        if (available.equals("y")) {
            parameters = client.getParameters();
        }
        System.out.println(parameters);
    }
}
