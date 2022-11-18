package com.AccessControl;
import java.io.IOException;
import org.json.simple.parser.ParseException;

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

    public int validateUserPermissions(String username, String function) throws IOException, ParseException {
        return 0;
    }
}
