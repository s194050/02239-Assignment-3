package com.AccessControl;
import java.io.*;
import java.util.Arrays;

import org.json.simple.parser.ParseException;

public class AccessControlList extends AccessControl {
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
    public boolean isUserAdmin(String username) throws ParseException {
        return false;
    }

    public static boolean createUserACL(String username, String operations) throws IOException {
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
        return status;
    }

//    @Override
    public static boolean deleteUserACL(String username) throws IOException {
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
            } else {
                System.out.println("User not found");
            }
            writer.write(lines + System.getProperty("line.separator"));
        }
        reader.close();
        writer.close();
        inputFile.delete();
        status = tempFile.renameTo(inputFile);
        return status;
    }

}

