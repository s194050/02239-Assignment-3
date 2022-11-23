package com.AccessControl;
import java.io.*;
import java.util.Arrays;

import org.json.simple.parser.ParseException;

public class AccessControlList extends AccessControl {

    // Maybe better if there was a file with all allowed functions instead of hardcoded here, and then read from that file
    private String validateFunctions(String functions) {
        String[] functionsArray = functions.split(";");
        for (int functionsIndexArray = 0; functionsIndexArray < functionsArray.length; functionsIndexArray++) {
            if (functionsArray[functionsIndexArray].equals("print")
                    || functionsArray[functionsIndexArray].equals("queue")
                    || functionsArray[functionsIndexArray].equals("topQueue")
                    || functionsArray[functionsIndexArray].equals("Start")
                    || functionsArray[functionsIndexArray].equals("Stop")
                    || functionsArray[functionsIndexArray].equals("Restart")
                    || functionsArray[functionsIndexArray].equals("status")
                    || functionsArray[functionsIndexArray].equals("readConfig")
                    || functionsArray[functionsIndexArray].equals("setConfig")) {
                continue;
            } else {
                return "Invalid function";
            }
        }
        return "true";
    }

    private String[] getFunctionUsers (String function) throws IOException, FileNotFoundException, ParseException {
        String lines;
        String fileIn = "accessControlLists.txt";
        BufferedReader reader = new BufferedReader(new FileReader(fileIn));
        while ((lines = reader.readLine()) != null) {
            String splitString[] = lines.split(":");
            if (splitString[0].equals(function)) {
                String usersLine = splitString[1];
                String usersArray[] = usersLine.split(";");
                reader.close();
                return usersArray;
            }
        }
        reader.close();
        return null;
    }
    
    // means that specific user already exists in the acl file
    private boolean userAlreadyInAccessControl(String username) throws IOException, FileNotFoundException, ParseException {
        boolean status = false;
        String lines;
        String fileIn = "accessControlLists.txt";
        BufferedReader reader =  new BufferedReader(new FileReader(fileIn));
        while ((lines = reader.readLine()) != null) {
            String splitString[] = lines.split(":");
            String usersLine = splitString[1];
            String usersArray[] = usersLine.split(";");
            for (int usersIndexArray = 0; usersIndexArray < usersArray.length; usersIndexArray++) {
                if (usersArray[usersIndexArray].equals(username)) {
                    status = true;
                    break;
                }
            }
        }
        reader.close();
        return status;
    }
    
    @Override
    public boolean isUserAdmin(String username) throws IOException, ParseException {
        String lines;
        String fileIn = "accessControlLists.txt";
        int adminFunctionsCount = 9; // 9 functions in total, admin only if all 9 functions are allowed
        BufferedReader reader =  new BufferedReader(new FileReader(fileIn));
        while ((lines = reader.readLine()) != null) {
            String splitString[] = lines.split(":");
            String usersLine = splitString[1];
            String usersArray[] = usersLine.split(";");
            for (int usersIndexArray = 0; usersIndexArray < usersArray.length; usersIndexArray++) {
                if (usersArray[usersIndexArray].equals(username)) {
                    adminFunctionsCount--;
                }
            }
        }
        reader.close();
        return adminFunctionsCount == 0; //is admin if all 9 functions are allowed
    }

    @Override
    public boolean validateUserPermissions(String username, String function) throws IOException, ParseException {
        if (validateFunctions(function).equals("Invalid function")) {
            return false;
        }
        String[] usersArray = getFunctionUsers(function);
        if (usersArray == null) {
            return false;
        }
        for (int usersIndexArray = 0; usersIndexArray < usersArray.length; usersIndexArray++) {
            if (usersArray[usersIndexArray].equals(username)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String createUser(String username, String functions) throws IOException, FileNotFoundException, ParseException {
        if (userAlreadyInAccessControl(username)) {
            return "User already in access control";
        }
        if (validateFunctions(functions).equals("Invalid function")) {
            return "Invalid operations";
        }

        String lines;
        String fileIn = "accessControlLists.txt";
        String fileOut = "accessControlListsTemp.txt";
        BufferedReader reader = new BufferedReader(new FileReader(fileIn));
        BufferedWriter writer = new BufferedWriter(new FileWriter(fileOut));

        while ((lines = reader.readLine()) != null) {
            String splitString[] = lines.split(":");
            String function = splitString[0];
            String usersLine = splitString[1];
            String usersArray[] = usersLine.split(";");

            // check if the user should have access to this specific function
            String[] functionsArray = functions.split(";");
            boolean flag = false;
            for (int functionsIndexArray = 0; functionsIndexArray < functionsArray.length; functionsIndexArray++) {
                if (function.equals(functionsArray[functionsIndexArray])) {
                    flag = true;
                    break;
                }
            }
            // if the function is in the functions string, add the user to the function
            if (flag) {
                usersArray = Arrays.copyOf(usersArray, usersArray.length + 1);
                usersArray[usersArray.length - 1] = username;
                String newUsersLine = String.join(";", usersArray);
                writer.write(function + ":" + newUsersLine + ";");
                writer.newLine();
            } else {
                writer.write(function + ":" + usersLine);
                writer.newLine();
            }
        }
        writer.close();
        reader.close();
        File oldFile = new File(fileIn);
        oldFile.delete();
        File newFile = new File(fileOut);
        newFile.renameTo(oldFile);
        return "true";
    }

    @Override
    public String deleteUser(String username) throws IOException, FileNotFoundException, ParseException {
        // If user doesn't exist in the acl file - then nothing to do
        if (!userAlreadyInAccessControl(username)) {
            return "false";
        }
        String lines;
        String fileIn = "accessControlLists.txt";
        String fileOut = "accessControlListsTemp.txt";
        BufferedReader reader = new BufferedReader(new FileReader(fileIn));
        BufferedWriter writer = new BufferedWriter(new FileWriter(fileOut));
        while ((lines = reader.readLine()) != null) {
            String splitString[] = lines.split(":");
            String usersLine = splitString[1];
            String usersArray[] = usersLine.split(";");
            String newUsersLine = "";
            for (int usersIndexArray = 0; usersIndexArray < usersArray.length; usersIndexArray++) {
                if (!usersArray[usersIndexArray].equals(username)) {
                    newUsersLine += usersArray[usersIndexArray] + ";";
                }
            }
            writer.write(splitString[0] + ":" + newUsersLine);
            writer.newLine();
        }
        reader.close();
        writer.close();
        File oldFile = new File(fileIn);
        oldFile.delete();
        File newFile = new File(fileOut);
        newFile.renameTo(oldFile);
        return "true";
    }

    @Override
    public String changeUserRole(String username, String newRole) throws IOException, FileNotFoundException, ParseException {
        return "Cannot change role when using ACLs";
    }

    @Override
    public String addUserFunction(String username, String function) throws IOException, FileNotFoundException, ParseException {
        if (validateFunctions(function).equals("Invalid function")) {
            return "Invalid operations";
        }
        if (!userAlreadyInAccessControl(username)) {
            return "false";
        }
        if (validateUserPermissions(username, function)) {
            return "Already has permission";
        }

        String lines;
        String fileIn = "accessControlLists.txt";
        String fileOut = "accessControlListsTemp.txt";
        BufferedReader reader = new BufferedReader(new FileReader(fileIn));
        BufferedWriter writer = new BufferedWriter(new FileWriter(fileOut));
        while ((lines = reader.readLine()) != null) {
            String splitString[] = lines.split(":");
            String usersLine = splitString[1];
            String usersArray[] = usersLine.split(";");
            if (splitString[0].equals(function)) {
                usersArray = Arrays.copyOf(usersArray, usersArray.length + 1);
                usersArray[usersArray.length - 1] = username;
                String newUsersLine = String.join(";", usersArray);
                writer.write(function + ":" + newUsersLine + ";");
                writer.newLine();
            } else {
                writer.write(splitString[0] + ":" + usersLine);
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
    
    @Override
    public String removeUserFunction(String username, String function) throws IOException, FileNotFoundException, ParseException {
        if (validateFunctions(function).equals("Invalid function")) {
            return "Invalid operations";
        }
        if (!userAlreadyInAccessControl(username)) {
            return "false";
        }
        if (!validateUserPermissions(username, function)) {
            return "Does not have permission";
        }

        String lines;
        String fileIn = "accessControlLists.txt";
        String fileOut = "accessControlListsTemp.txt";
        BufferedReader reader = new BufferedReader(new FileReader(fileIn));
        BufferedWriter writer = new BufferedWriter(new FileWriter(fileOut));

        while ((lines = reader.readLine()) != null) {
            String splitString[] = lines.split(":");
            String usersLine = splitString[1];
            String usersArray[] = usersLine.split(";");
            String newUsersLine = "";
            if (splitString[0].equals(function)) {
                for (int usersIndexArray = 0; usersIndexArray < usersArray.length; usersIndexArray++) {
                    if (!usersArray[usersIndexArray].equals(username)) {
                        newUsersLine += usersArray[usersIndexArray] + ";";
                    }
                }
                writer.write(function + ":" + newUsersLine);
                writer.newLine();
            } else {
                writer.write(splitString[0] + ":" + usersLine);
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

