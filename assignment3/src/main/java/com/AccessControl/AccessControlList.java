package com.AccessControl;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import org.json.simple.parser.ParseException;

public class AccessControlList extends AccessControl {
    @Override
    public int validateUserPermissions(String username, String function) throws IOException, ParseException {
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
                    if (operationsArray[operationsIndexArray].equals(function)) {
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

