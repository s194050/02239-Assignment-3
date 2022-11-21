package com.AccessControl;
import java.io.*;
import java.util.Arrays;

import org.json.simple.parser.ParseException;

public class AccessControlList extends AccessControl {

    // Maybe better if there was a file with all allowed operations instead of hardcoded here, and then read from that file
    private String validateOperations(String operations) {
        String[] operationsArray = operations.split(";");
        for (int operationsIndexArray = 0; operationsIndexArray < operationsArray.length; operationsIndexArray++) {
            if (operationsArray[operationsIndexArray].equals("print")
                    || operationsArray[operationsIndexArray].equals("queue")
                    || operationsArray[operationsIndexArray].equals("topQueue")
                    || operationsArray[operationsIndexArray].equals("Start")
                    || operationsArray[operationsIndexArray].equals("Stop")
                    || operationsArray[operationsIndexArray].equals("Restart")
                    || operationsArray[operationsIndexArray].equals("status")
                    || operationsArray[operationsIndexArray].equals("readConfig")
                    || operationsArray[operationsIndexArray].equals("setConfig")) {
                continue;
            } else {
                return "Invalid operation";
            }
        }
        return "true";
    }

    private String[] getUserFunctions (String username) throws IOException, FileNotFoundException, ParseException {
        String lines;
        String fileIn = "accessControlLists.txt";
        BufferedReader reader = new BufferedReader(new FileReader(fileIn));
        while ((lines = reader.readLine()) != null) {
            String splitString[] = lines.split(":");
            if (splitString[0].equals(username)) {
                String operationsLine = splitString[1];
                String operationsArray[] = operationsLine.split(";");
                reader.close();
                return operationsArray;
            }
        }
        reader.close();
        return null;
    }

    // means that the specific user has access to the specific function
    private boolean functionExists(String username, String function) throws IOException, FileNotFoundException, ParseException {
        boolean status = false;
        String lines;
        String fileIn = "accessControlLists.txt";
        BufferedReader reader = new BufferedReader(new FileReader(fileIn));
        while ((lines = reader.readLine()) != null) {
            String splitString[] = lines.split(":");
            if (splitString[0].equals(username)) {
                String operationsLine = splitString[1];
                String operationsArray[] = operationsLine.split(";");
                for (int operationsIndexArray = 0; operationsIndexArray < operationsArray.length; operationsIndexArray++) {
                    if (operationsArray[operationsIndexArray].equals(function)) {
                        System.out.println("Such function already exists");
                        status = true;
                        reader.close();
                        return status;
                    }
                }
            }
        }
        reader.close();
        return status;
    }
    
    // means that specific user already exists in the acl file
    private boolean userAlreadyInAccessControl(String username)
            throws IOException, FileNotFoundException, ParseException {
        boolean status = false;
        String lines;
        String fileIn = "accessControlLists.txt";
        BufferedReader reader =  new BufferedReader(new FileReader(fileIn));
        while ((lines = reader.readLine()) != null) {
            String splitString[] = lines.split(":");
            if (splitString[0].equals(username)) {
                status = true;
                break;
            }
        }
        reader.close();
        return status;
    }
    
    @Override
    public boolean isUserAdmin(String username) throws IOException, ParseException {
        boolean status = false;
        String lines;
        String fileIn = "accessControlLists.txt";
        BufferedReader reader =  new BufferedReader(new FileReader(fileIn));
        while ((lines = reader.readLine()) != null) {
            String splitString[] = lines.split(":");
            if (splitString[0].equals(username)) {
                String operationsLine = splitString[1];
                if (operationsLine.equals("print;queue;topQueue;Start;Stop;Restart;status;readConfig;setConfig;")) {
                    status = true;
                    break;
                } else {
                    status = false;
                    break;
                }
            }
        }
        reader.close();
        return status;
    }

    @Override
    public boolean validateUserPermissions(String username, String function) throws IOException, ParseException {
        boolean status = false;
        String lines;
        String fileIn = "accessControlLists.txt";
        BufferedReader reader = new BufferedReader(new FileReader(fileIn));
        while ((lines = reader.readLine()) != null) {
            String splitString[] = lines.split(":");
            if (splitString[0].equals(username)) {
                String operationsLine = splitString[1];
                String operationsArray[] = operationsLine.split(";");
                for (int operationsIndexArray = 0; operationsIndexArray < operationsArray.length; operationsIndexArray++) {
                    if (operationsArray[operationsIndexArray].equals(function)) {
                        status = true;
                        break;
                    } else {
                        status = false;
                    }
                }
            }
        }
        reader.close();
        return status;
    }

    @Override
    public String createUser(String username, String operations) throws IOException, FileNotFoundException, ParseException {
        if (userAlreadyInAccessControl(username)) {
            return "User already in access control";
        } else {
            String fileIn = "accessControlLists.txt";
            BufferedWriter writer = new BufferedWriter(new FileWriter(fileIn, true));
            // validate operations before writing in file
            if (validateOperations(operations).equals("true")) {
                writer.write(username + ":" + operations + ";");
                writer.newLine();
                writer.close();
                return "true";
            } else {
                writer.close();
                return "Invalid operations";
            }
        }
    }

    @Override
    public String deleteUser(String username) throws IOException, FileNotFoundException, ParseException {
        // If user doesn't exist in the acl file - then nothing to do
        if (!userAlreadyInAccessControl(username)) {
            return "false";
        } else {
            String lines;
            String fileIn = "accessControlLists.txt";
            String fileOut = "accessControlListsTemp.txt";
            BufferedReader reader = new BufferedReader(new FileReader(fileIn));
            BufferedWriter writer = new BufferedWriter(new FileWriter(fileOut));
            while ((lines = reader.readLine()) != null) {
                String splitString[] = lines.split(":");
                if (!splitString[0].equals(username)) {
                    writer.write(lines);
                    writer.newLine();
                }
            }
            reader.close();
            writer.close();
            File oldFile = new File(fileIn);
            oldFile.delete();
            File newFile = new File(fileOut);
            newFile.renameTo(oldFile);
            return "true";
        }
    }

    @Override
    public String changeUserRole(String username, String newRole)
            throws IOException, FileNotFoundException, ParseException {
        return "Cannot change role when using ACLs";
    }

    @Override
    public String addUserFunction(String username, String function)
            throws IOException, FileNotFoundException, ParseException {
        
        if (!userAlreadyInAccessControl(username)) { // if user already exists
            return "false";
        }
        // if user already has such function
        if (functionExists(username, function)) {
            System.out.println("Such function already exists");
            return "true";
        }

        boolean status = false;
        String lines;
        String fileIn = "accessControlLists.txt";
        BufferedReader reader = new BufferedReader(new FileReader(fileIn));
        BufferedWriter writer = new BufferedWriter(new FileWriter(fileIn, true));

        while ((lines = reader.readLine()) != null) {
            String splitString[] = lines.split(":");
            if (splitString[0].equals(username)) {
                String operationsLine = splitString[1];
                String operationsArray[] = operationsLine.split(";");
                for (int operationsIndexArray = 0; operationsIndexArray < operationsArray.length; operationsIndexArray++) {
                    if (operationsArray[operationsIndexArray].equals(function)) {
                        System.out.println("Such role already exists");
                        status = false;
                        break;
                    } else {
                        System.out.println("Such role doesnt exist");
                        writer.newLine();//it already appends at the end
                        writer.write(username + ":");
                        for (String operation : operationsArray) {
                            writer.write(operation + ";");
                        }
                        writer.write(function + ";");
                        status = true;
                    }
                }
            }
        }
        
        reader.close();
        writer.close();
        return status ? "true" : "false";
    }
    
    @Override
    public String removeUserFunction(String username, String function)
            throws IOException, FileNotFoundException, ParseException {
        // we can deleteUser

        if (!userAlreadyInAccessControl(username)) {
            System.out.println("Such user doesnt exist");
            return "false";
        }

        if (!functionExists(username, function)) {
            System.out.println("Such function doesnt exist");
            return "true";
        }

        if (deleteUser(username).equals("true")) {
            // and then create user with the same name but without the function
            String operationsArray[] = getUserFunctions(username);
            String newOperations[] = new String[operationsArray.length - 1];
            for (int operationsIndexArray = 0; operationsIndexArray < operationsArray.length; operationsIndexArray++) {
                if (operationsArray[operationsIndexArray].equals(function)) {
                    continue;
                } else {
                    newOperations[operationsIndexArray] = operationsArray[operationsIndexArray];
                }
            }
            String newOperationsString = String.join(";", newOperations);
            if (createUser(username, newOperationsString).equals("true")) {
                return "true";
            } else {
                return "false";
            }
        } else {
            return "false";
        }
    }

}

