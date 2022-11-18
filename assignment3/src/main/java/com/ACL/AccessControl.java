package com.ACL;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class AccessControl {
     /*
    Implementation of Access Control
     */
    //            checking if user is allowed to execute this operation
            /*
            checking if user is allowed to execute this operation
            1 means is allowed
            0 means is not allowed

            Example how to use:

            String username = SessionAuth.getUsernameFromToken(userToken);
            if (accessControlStatus(username, "print") == 1) {
                System.out.println("Nice you can do this action");
//                return "Nice you can do this action";
        } else {
                System.out.println("You have no access to this operation");
//                return "You have no access to this operation";
            }


            OR use this one below to apply restrictions to the function:
//            String username = SessionAuth.getUsernameFromToken(userToken);
//            if (accessControlStatus(username, "print") == 1) {
//                System.out.println("You can do this action");
//
//            } else {
//                return "You have no access to this operation";
//            }

             */
            public static int accessControlStatus(String username, String operation) throws IOException {
                int status = 0;
                String lines;
                String fileIn = "accessControlLists.txt";
                BufferedReader reader = new BufferedReader(new FileReader(fileIn));
                while ((lines = reader.readLine()) != null) {
                    String splitString[] = lines.split(":");
                    if (splitString[0].equals(username)) {
                        String operationsLine = splitString[1];
                        String operationsArray[] = operationsLine.split(";");
                        for (int operationsIndexArray = 0; operationsIndexArray < operationsArray.length; operationsIndexArray++) {
                            if (operationsArray[operationsIndexArray].equals(operation)) {
                                status = 1;
                                break;
                            } else {
                                status = 0;
                            }
                        }
                    }
                }
                reader.close();
        
                return status;
            }
        
}
