package com.AccessControl;

// import java.io.FileNotFoundException;
import org.json.JSONObject;
import org.json.JSONArray
class RoleBasedAccessControl extends AccessControl{
    

    @Override
    public void loadAccessControlFile(){
         JSONArray a = (JSONArray) parser.parse(new FileReader("c:\\exer4-courses.json"));

    }



    // @Override
    // public void validateUserPermissions() {
    //     System.out.println("RoleBasedAccessControl.validateRoles()");
    // }

            
    
}
