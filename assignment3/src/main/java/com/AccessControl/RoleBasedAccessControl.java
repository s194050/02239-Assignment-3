package com.AccessControl;

import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.json.JSONArray;
import org.json.simple.parser.*;

class RoleBasedAccessControl extends AccessControl{
    
    @Override
    public void loadAccessControlFile() throws FileNotFoundException, IOException, ParseException{
        JSONParser parser = new JSONParser();
        Object obj = parser.parse(new FileReader("Role_based_access_control.json"));
        JSONObject jsonObject = (JSONObject) obj;
        String name = (String) jsonObject.get("name");
        JSONArray roles = (JSONArray) jsonObject.get("roles");
        JSONArray permissions = (JSONArray) jsonObject.get("permissions");

    }



    // @Override
    // public void validateUserPermissions() {
    //     System.out.println("RoleBasedAccessControl.validateRoles()");
    // }

            
    
}
