package com.AccessControl;

// import 

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


     public void loadAccessControlFile(){
        System.out.println("Loading Access Control File not implemented");
    }

    // public JSONObject


    public boolean validateUserPermissions(String username, String function){
        System.out.println("Validating user can execute function");
        return false;
    }

}
