package com.AccessControl;
import java.io.*;
import java.util.Arrays;

import org.json.simple.parser.ParseException;

public class AccessControlList extends AccessControl {

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
            } else {
                status = false;
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
        boolean status = false;
        String lines;
        String fileIn = "accessControlLists.txt";
        BufferedReader reader = new BufferedReader(new FileReader(fileIn));
        BufferedWriter writer = new BufferedWriter(new FileWriter(fileIn, true));

        String operationsArray[] = operations.split(";");
        System.out.println(Arrays.toString(operationsArray));

        while ((lines = reader.readLine()) != null) {
            String splitString[] = lines.split(":");
            if (splitString[0].equals(username)) {
                // user with such username exists
                System.out.println("Such user already exists");
//                status = false;
                break;
            }}
            System.out.println("Such user doesnt exist");
            writer.newLine();//it already appends at the end
            writer.write(username + ":");
            for (String operation:
                    operationsArray) {
                writer.write(operation + ";");
            }
            status = true;


        reader.close();
        writer.close();
        return status ? "true" : "false";
    }

    @Override
    public String deleteUser(String username) throws IOException, FileNotFoundException, ParseException {
        if (!userAlreadyInAccessControl(username)) {
            return "true";
        }

        boolean status;
        String lines;
        File inputFile = new File("accessControlLists.txt");
        File tempFile = new File("myTempFile.txt");

        BufferedReader reader = new BufferedReader(new FileReader(inputFile));
        BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

        while ((lines = reader.readLine()) != null) {
            String splitString[] = lines.split(":");
            if (splitString[0].equals(username)) {
                continue;
            }

            writer.write(lines + System.getProperty("line.separator"));
        }
        reader.close();
        writer.close();
        inputFile.delete();
        status = tempFile.renameTo(inputFile);
        return status ? "true" : "false";
    }

    @Override
    public String changeUserRole(String username, String newRole)
            throws IOException, FileNotFoundException, ParseException {
        return "Cannot change role when using ACLs";
    }

    @Override
    public String addUserFunction(String username, String function)
            throws IOException, FileNotFoundException, ParseException {
        
        if (!userAlreadyInAccessControl(username)) {
            System.out.println("Such user doesnt exist");
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

