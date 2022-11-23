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
    private boolean roleExists(String role) throws IOException, FileNotFoundException, ParseException {
        // Check if role exists in the user_roles.json file
        boolean roleExists = false;
        JSONParser parser = new JSONParser();
        Object rbac_ = parser.parse(new FileReader("role_based_access_control.json"));
        JSONObject rbac = (JSONObject) rbac_;
        JSONArray roles = (JSONArray) rbac.get("roles");
        for (int i = 0; i < roles.size(); i++) {
            String this_role = roles.get(i).toString();
            if (this_role.equals(role)) {
                roleExists = true;
                break;
            }
        }

        return roleExists;
    }
    private boolean userExistsInAccessControl(String username) throws IOException, FileNotFoundException, ParseException {
        // Check if user exists in the user_roles.json file
        boolean userExists = false;
        JSONParser parser = new JSONParser();
        Object users_roles_ = parser.parse(new FileReader("users_roles.json"));
        JSONObject users_roles = (JSONObject) users_roles_;
        JSONObject roles_with_names = (JSONObject) users_roles.get("roles");
        for (int i = 0; i < roles_with_names.size(); i++) { // iterate through the roles
            String role = roles_with_names.keySet().toArray()[i].toString(); // get the role name
            boolean userInAccessControl = userAlreadyInAccessControlWithRole(username, role); // check if user exists in the role
            if(userInAccessControl){ // if user exists in the role
                userExists = true;
                break;
            }
        }
        return userExists;
    }
    private boolean userAlreadyInAccessControlWithRole(String username, String role) throws IOException, FileNotFoundException, ParseException {
        // Check if user exists in the user_roles.json file with a specific role
        boolean userAlreadyInAccessControl = false;
        JSONParser parser = new JSONParser();
        Object users_roles_ = parser.parse(new FileReader("users_roles.json"));
        JSONObject users_roles = (JSONObject) users_roles_;
        JSONObject roles_with_names = (JSONObject) users_roles.get("roles");
        JSONArray users = (JSONArray) roles_with_names.get(role);
        for (int i = 0; i < users.size(); i++) {
            String this_user = users.get(i).toString();
            if (this_user.equals(username)) { // if the user exists for the role
                userAlreadyInAccessControl = true;
                break;
            }
        }
        return userAlreadyInAccessControl;
    }

    @Override
    public boolean isUserAdmin(String username) throws IOException, ParseException {
        // Check if user is admin
        boolean isAdmin = false;
        try {
            JSONParser parser = new JSONParser();
            Object users_roles_ = parser.parse(new FileReader("users_roles.json"));
            JSONObject users_roles = (JSONObject) users_roles_;
            JSONObject roles_with_names = (JSONObject) users_roles.get("roles");
            JSONArray admins = (JSONArray) roles_with_names.get("admin");
            for (int i = 0; i < admins.size(); i++) {
                String this_admin = admins.get(i).toString();
                if (this_admin.equals(username)) {
                    isAdmin = true;
                    return isAdmin;
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return isAdmin;
    }

    @Override
    public boolean validateUserPermissions(String username, String function)
            throws IOException, FileNotFoundException, ParseException {
        // Check if user has permissions to access a function
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
    public String createUser(String username, String role) throws IOException, FileNotFoundException, ParseException {
        boolean status = false;
        
        if (!roleExists(role)) { // if the role does not exist
            return "Role does not exist";
        }
        if (userExistsInAccessControl(username)) { // if the user already exists in the access control
            return "User already in access control";
        }

        try{
            JSONParser parser = new JSONParser(); // parse the users_roles.json file

            Object users_roles_ = parser.parse(new FileReader("users_roles.json"));
            JSONObject users_roles = (JSONObject) users_roles_;

            JSONObject roles_with_names = (JSONObject) users_roles.get("roles");
            JSONArray users = (JSONArray) roles_with_names.get(role);

            // if the user does not exist, add the user to the role

            users.add(username);
            roles_with_names.put(role, users);
            users_roles.put("roles", roles_with_names);

            FileWriter fstream = new FileWriter("users_roles.json", false);
            BufferedWriter out = new BufferedWriter(fstream);
            System.out.println(username + " added to " + role);
            // Write to file and make a new line.
            out.write(users_roles.toJSONString());
            out.flush();
            out.close();
            status = true;
            return status ? "true" : "false";
        }
        catch(Exception e){
            return status ? "true" : "false";
        }
    }

    @Override
    public String deleteUser(String username) throws IOException, FileNotFoundException, ParseException {
        // delete the user from the users_roles.json file
        boolean status = false;
        JSONParser parser = new JSONParser(); // create a parser
        Object users_roles_ = parser.parse(new FileReader("users_roles.json")); // parse the file
        JSONObject users_roles = (JSONObject) users_roles_; // cast the parsed file to a JSONObject

        JSONObject roles_with_names = (JSONObject) users_roles.get("roles"); // get the roles object
        try {
            for(int i = 0; i < roles_with_names.size(); i++) { // iterate through the roles
                String role = roles_with_names.keySet().toArray()[i].toString(); // get the role name
                JSONArray users = (JSONArray) roles_with_names.get(role); // get the users for the role

                for(int j = 0; j < users.size(); j++) { // iterate through the users
                    if(users.get(j).toString().equals(username)) { // if the user is found
                        users.remove(j); // remove the user
                        roles_with_names.put(role, users); // update the users for the role
                        users_roles.put("roles", roles_with_names); // update the roles_with_names
                        status = true;
                        System.out.println(username + ": deleted from " + role);
                        break;
                    }
                }
            }
            FileWriter fstream = new FileWriter("users_roles.json", false);
            BufferedWriter out = new BufferedWriter(fstream);
            // Write to file
            out.write(users_roles.toJSONString());
            out.flush();
            out.close();
            return status ? "true" : "false";
        }catch(Exception e){
            return status ? "true" : "false";
        }
    }

    @Override
    public String changeUserRole(String username, String newRole)
            throws IOException, FileNotFoundException, ParseException {
        // Change the role of a user to the new role
        String status = "false";

        if (!roleExists(newRole)) {
            return "Role does not exist";
        }
        if (userAlreadyInAccessControlWithRole(username, newRole)) {
            return "User already in access control";
        }

        status = deleteUser(username);
        if (status.equals("true")) {
            status = createUser(username, newRole);
            System.out.println(username + ": changed role to " + newRole);
        }

        return status;
    }
    
    @Override
    public String addUserFunction(String username, String function)
            throws IOException, FileNotFoundException, ParseException {
        return "Cannot add functions when using RBAC";
    }

    @Override
    public String removeUserFunction(String username, String function)
            throws IOException, FileNotFoundException, ParseException {
        return "Cannot remove functions when using RBAC";
    }

}
