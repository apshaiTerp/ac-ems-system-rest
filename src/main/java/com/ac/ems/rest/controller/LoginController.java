package com.ac.ems.rest.controller;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ac.ems.data.enums.UserRole;
import com.ac.ems.data.enums.UserRoleConverter;
import com.ac.ems.data.util.UserComplete;
import com.ac.ems.db.EMSDatabase;
import com.ac.ems.db.MongoDBFactory;
import com.ac.ems.db.exception.ConfigurationException;
import com.ac.ems.db.exception.DatabaseOperationException;
import com.ac.ems.rest.Application;
import com.ac.ems.rest.data.LoginData;
import com.ac.ems.rest.data.LoginSuccessData;
import com.ac.ems.rest.message.SimpleErrorData;

/**
 * @author ac010168
 *
 */
@RestController
@RequestMapping("/login")
public class LoginController {

  @RequestMapping(method = RequestMethod.POST, consumes = "application/json;charset=UTF-8", produces="application/json;charset=UTF-8")
  public Object doLogin(@RequestBody LoginData loginData) {
    //Work through all the pre-reqs for the request
    if (loginData == null)
      return new SimpleErrorData("Login Error", "There was no valid login data provided");
    if (loginData.getUserName() == null)
      return new SimpleErrorData("Login Error", "There was no login user name provided");
    if (loginData.getPassword() == null)
      return new SimpleErrorData("Login Error", "There was no login password provided");
    if (loginData.getUserRole() == null)
      return new SimpleErrorData("Login Error", "There was no login user role provided");
    if ((!loginData.getUserRole().equalsIgnoreCase("EMS")) && (!loginData.getUserRole().equalsIgnoreCase("AMBULANCE")) &&
        (!loginData.getUserRole().equalsIgnoreCase("HOSPITAL")) && (!loginData.getUserRole().equalsIgnoreCase("DISPATCH")))
      return new SimpleErrorData("Login Error", "The userRole value provided was invalid.");
    if ((!loginData.getUserRole().equalsIgnoreCase("DISPATCH")) && (loginData.getAuthorizeID() <= 0))
      return new SimpleErrorData("Login Error", "Login requests for this role require an authorizedID");
    
    //Create all the database stuff
    EMSDatabase database = null;
    
    try {
      if (Application.database == null)
        Application.database = MongoDBFactory.createMongoDatabase(Application.databaseHost, Application.databasePort, Application.databaseName);
      database = Application.database;
      database.initializeDBConnection();

      //Verify the user exists.
      long userID = database.getUserIDByUserName(loginData.getUserName());
      if (userID <= 0)
        return new SimpleErrorData("Login Error", "There is no user with the userName " + loginData.getUserName() + ".");
      
      //If the user exists, pull his full information
      UserComplete user = database.getUserDataByID(userID);
      if (user == null)
        return new SimpleErrorData("Login Error", "There is a problem finding the user data.");
      
      //Validate the password
      if (!user.getUserInformation().getPassword().equals(loginData.getPassword()))
        return new SimpleErrorData("Login Error", "The password provided is not correct.");
      
      //Validate the role type and authorizedID are valid
      if (user.getUserInformation().getUserRole() != UserRole.SUPER) {
        //Reject Role Mismatches
        if (loginData.getUserRole().equalsIgnoreCase("DISPATCH") && (user.getUserInformation().getUserRole() != UserRole.DISPATCH))
          return new SimpleErrorData("Login Error", "This user is not authorized for Dispatcher tasks.");
        if (loginData.getUserRole().equalsIgnoreCase("EMS") && (user.getUserInformation().getUserRole() != UserRole.EMS))
          return new SimpleErrorData("Login Error", "This user is not authorized for EMS Provider tasks.");
        if (loginData.getUserRole().equalsIgnoreCase("HOSPITAL") && (user.getUserInformation().getUserRole() != UserRole.HOSPITAL))
          return new SimpleErrorData("Login Error", "This user is not authorized for Hospital tasks.");
        if (loginData.getUserRole().equalsIgnoreCase("AMBULANCE") && (user.getUserInformation().getUserRole() != UserRole.AMBULANCE))
          return new SimpleErrorData("Login Error", "This user is not authorized for Ambulance tasks.");
        
        //Now check for the valid authID
        if ((loginData.getUserRole().equalsIgnoreCase("EMS") && (user.getUserInformation().getUserRole() == UserRole.EMS)) ||
            (loginData.getUserRole().equalsIgnoreCase("HOSPITAL") && (user.getUserInformation().getUserRole() == UserRole.HOSPITAL)) ||
            (loginData.getUserRole().equalsIgnoreCase("AMBULANCE") && (user.getUserInformation().getUserRole() == UserRole.AMBULANCE))) {
          if (!user.getUserInformation().getAuthorizedIDs().contains(loginData.getAuthorizeID()))
            return new SimpleErrorData("Login Error", "This user is not authorized for this task");
        }
      }
      
      //If we made it this far, we're good
      System.out.println ("[LOG] User " + user.getUser().getUserNameDisplay() + " has logged in.");
      LoginSuccessData successData = new LoginSuccessData();
      successData.setUserName(user.getUser().getUserNameDisplay());
      successData.setUserID(user.getUser().getUserID());
      successData.setUserRole(UserRoleConverter.convertUserRoleToString(user.getUserInformation().getUserRole()));
      
      return successData;
    } catch (DatabaseOperationException doe) {
      doe.printStackTrace();
      return new SimpleErrorData("Database Operation Error", "An error occurred running the request: " + doe.getMessage());
    } catch (ConfigurationException ce) {
      ce.printStackTrace();
      return new SimpleErrorData("Database Configuration Error", "An error occurred accessing the database: " + ce.getMessage());
    } 
  }
}
