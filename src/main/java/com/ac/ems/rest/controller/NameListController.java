package com.ac.ems.rest.controller;

import java.util.List;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ac.ems.db.EMSDatabase;
import com.ac.ems.db.MongoDBFactory;
import com.ac.ems.db.exception.ConfigurationException;
import com.ac.ems.db.exception.DatabaseOperationException;
import com.ac.ems.rest.Application;
import com.ac.ems.rest.data.ListSuccessData;
import com.ac.ems.rest.message.SimpleErrorData;

/**
 * @author ac010168
 *
 */
@RestController
@RequestMapping("/namelist")
public class NameListController {
  
  /**
   * GET method to generate lists needed for dynamic drop down lists
   * 
   * @param type The list type that needs to be pulled.
   * 
   * @return A List of Strings for selecting against the requested group.
   */
  @RequestMapping(method = RequestMethod.GET, produces="application/json;charset=UTF-8")
  public Object getNameList(@RequestParam(value="type") String type) {
    if (type == null)
      return new SimpleErrorData("Invalid Parameters", "No Type Value was provided");
    if (!type.equalsIgnoreCase("hospital") && !type.equalsIgnoreCase("provider"))
      return new SimpleErrorData("Invalid Parameters", "The provided type was not valid.");
    
    //Create all the database stuff
    EMSDatabase database = null;
    
    try {
      if (Application.database == null)
        Application.database = MongoDBFactory.createMongoDatabase(Application.databaseHost, Application.databasePort, Application.databaseName);
      database = Application.database;
      database.initializeDBConnection();

      List<String> results = null;
      if (type.equalsIgnoreCase("hospital"))
        results = database.getHospitalNames();
      if (type.equalsIgnoreCase("provider"))
        results = database.getProviderNames();
      
      if (results == null)
        return new SimpleErrorData("No Results Found", "There were problems finding the search results.  Null was returned.");
      
      ListSuccessData data = new ListSuccessData();
      data.setResultList(results);
    
      return data;
    } catch (DatabaseOperationException doe) {
      doe.printStackTrace();
      return new SimpleErrorData("Database Operation Error", "An error occurred running the request: " + doe.getMessage());
    } catch (ConfigurationException ce) {
      ce.printStackTrace();
      return new SimpleErrorData("Database Configuration Error", "An error occurred accessing the database: " + ce.getMessage());
    } 
  }

}
