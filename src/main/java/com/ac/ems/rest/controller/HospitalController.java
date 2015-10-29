package com.ac.ems.rest.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ac.ems.data.Hospital;
import com.ac.ems.db.EMSDatabase;
import com.ac.ems.db.MongoDBFactory;
import com.ac.ems.db.exception.ConfigurationException;
import com.ac.ems.db.exception.DatabaseOperationException;
import com.ac.ems.rest.Application;
import com.ac.ems.rest.message.SimpleErrorData;

/**
 * @author ac010168
 *
 */
@RestController
@RequestMapping("/hospital")
public class HospitalController {
  
  @RequestMapping(method = RequestMethod.GET, produces="application/json;charset=UTF-8")
  public Object getHospital(@RequestParam(value="id") long hospitalID) {
    if (hospitalID < 0)
      return new SimpleErrorData("Invalid Parameters", "The id provided is invalid.");
      
    //Create all the database stuff
    EMSDatabase database = null;
    
    try {
      if (Application.database == null)
        Application.database = MongoDBFactory.createMongoDatabase(Application.databaseHost, Application.databasePort, Application.databaseName);
      database = Application.database;
      database.initializeDBConnection();

      Hospital hospital = (Hospital)database.querySingleRow(EMSDatabase.HOSPITAL_TABLE_NAME, "hospitalID", hospitalID);
      if (hospital == null)
        return new SimpleErrorData("No Results Found", "There were problems finding the search results.  Null was returned.");
      
      return hospital;
    } catch (DatabaseOperationException doe) {
      doe.printStackTrace();
      return new SimpleErrorData("Database Operation Error", "An error occurred running the request: " + doe.getMessage());
    } catch (ConfigurationException ce) {
      ce.printStackTrace();
      return new SimpleErrorData("Database Configuration Error", "An error occurred accessing the database: " + ce.getMessage());
    } 
  }
}
