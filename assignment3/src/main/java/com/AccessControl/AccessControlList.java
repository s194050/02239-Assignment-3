package com.AccessControl;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

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
}

