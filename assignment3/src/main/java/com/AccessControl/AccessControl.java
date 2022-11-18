package com.AccessControl;
import java.io.IOException;
import org.json.simple.parser.ParseException;
import java.io.FileNotFoundException;

public abstract class AccessControl {
    
    protected AccessControl() {

    }

    public static AccessControl runACL(String type) {
        if (type.equals("RBAC")) {
            return new RoleBasedAccessControl();
        } else if (type.equals("ACL")) {
            return new AccessControlList();
        } else {
            return null;
        } 
    }

    public boolean validateUserPermissions(String username, String function) throws IOException, ParseException {
        return false;
    }

    public boolean createUser(String username, String role) throws IOException, FileNotFoundException, ParseException {
        return false;
    }

    public boolean deleteUser(String username) throws IOException, FileNotFoundException, ParseException {
        return false;
    }
}
