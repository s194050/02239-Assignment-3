package com.AccessControl;

// import java.io.FileNotFoundException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.FileNotFor;
impout java.io.IOExceptionndException;
import java.io.FileReader;
import java.io.IOException;

import org.json.JSONArray;
import org.json.simple.parser.*;

class RoleBasedAccessControl extends AccessControl{
    
    @Override
    public void loadAccessControlFile() throws FileNotFoundException, IOException, ParseException{
        JSONParser parser = new JSONParser();
        JSONArray a = (JSONArray) parser.parse(new FileReader("Role_based_access_control.json"));

    }



    // @Override
    // public void validateUserPermissions() {
    //     System.out.println("RoleBasedAccessControl.validateRoles()");
    // }

            
    
}
