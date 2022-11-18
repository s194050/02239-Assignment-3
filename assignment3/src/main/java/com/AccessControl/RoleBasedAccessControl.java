package com.AccessControl;

import org.json.simple.JSONObject;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.json.simple.JSONArray;
import org.json.simple.parser.*;

class RoleBasedAccessControl extends AccessControl {
    
    @Override
    public int validateUserPermissions(String username, String function) throws IOException, FileNotFoundException, ParseException {
        int status = 0;
        JSONParser parser = new JSONParser();
        Object rbac_ = parser.parse(new FileReader("role_based_access_control.json"));
        Object users_roles_ = parser.parse(new FileReader("users_roles.json"));
        JSONObject rbac = (JSONObject) rbac_;
        JSONObject users_roles = (JSONObject) users_roles_;
        // 
        JSONArray roles = (JSONArray) rbac.get("roles");
        JSONObject permissions = (JSONObject) rbac.get("permissions");
        JSONObject roles_with_names = (JSONObject) users_roles.get("roles");
        //
        // first check if the username belongs to some role roles_with_names
        for (int i = 0; i < roles.size(); i++) {
            String role = roles.get(i).toString();
            JSONArray users_for_role = (JSONArray) roles_with_names.get(role);
            for (int j = 0; j < users_for_role.size(); j++) {
                if (users_for_role.get(j).toString().equals(username)) {
                    JSONArray functions_of_role = (JSONArray) permissions.get(role);
                    for (int k = 0; k < functions_of_role.size(); k++) {
                        if (functions_of_role.get(k).toString().equals(function)) {
                            status = 1;
                            break;
                        }
                    }
                }
            }   
        }
        return status;
    }
}
