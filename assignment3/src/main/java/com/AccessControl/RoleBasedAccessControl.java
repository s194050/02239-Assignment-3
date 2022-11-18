package com.AccessControl;

import org.json.simple.JSONObject;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.json.simple.JSONArray;
import org.json.simple.parser.*;

class RoleBasedAccessControl extends AccessControl {
    @Override
    public boolean validateUserPermissions(String username, String function)
            throws IOException, FileNotFoundException, ParseException {
        boolean status = false;
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
                            status = true;
                            break;
                        }
                    }
                }
            }
        }
        return status;
    }
    
    @Override
    public boolean createUser(String username, String role) throws IOException, FileNotFoundException, ParseException {
        boolean status = false;
        JSONParser parser = new JSONParser();
        Object users_roles_ = parser.parse(new FileReader("users_roles.json"));
        JSONObject users_roles = (JSONObject) users_roles_;

        JSONObject roles_with_names = (JSONObject) users_roles.get("roles");
        JSONArray users = (JSONArray) roles_with_names.get(role);
        users.add(username);
        roles_with_names.put(role, users);
        users_roles.put("roles", roles_with_names);

        FileWriter fstream = new FileWriter("users_roles.json", false);
        BufferedWriter out = new BufferedWriter(fstream);
        // Write to file and make a new line.
        out.write(users_roles.toJSONString());
        out.flush();
        out.close();
        return status;
    }

    @Override
    public boolean deleteUser(String username) throws IOException, FileNotFoundException, ParseException {
        return false;
    }
}
