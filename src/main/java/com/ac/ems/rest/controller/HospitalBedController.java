package com.ac.ems.rest.controller;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ac.ems.data.Hospital;
import com.ac.ems.db.EMSDatabase;
import com.ac.ems.db.MongoDBFactory;
import com.ac.ems.db.exception.ConfigurationException;
import com.ac.ems.db.exception.DatabaseOperationException;
import com.ac.ems.rest.Application;
import com.ac.ems.rest.data.HospitalBedActionData;
import com.ac.ems.rest.data.HospitalBeds;
import com.ac.ems.rest.message.SimpleErrorData;

/**
 * @author ac010168
 *
 */
@RestController
@RequestMapping("/hospital/beds")
public class HospitalBedController {

  @RequestMapping(method = RequestMethod.POST, consumes = "application/json;charset=UTF-8", produces="application/json;charset=UTF-8")
  public Object changeBedCounts(@RequestBody HospitalBedActionData actionData) {
    if (actionData == null)
      return new SimpleErrorData("Request Error", "There was no valid bed data provided");
    if (actionData.getHospitalID() < 0)
      return new SimpleErrorData("Request Error", "The provided hospitalID was not valid");
    if ((!actionData.getActionType().equalsIgnoreCase("admiter")) && (!actionData.getActionType().equalsIgnoreCase("admittrauma")) &&
        (!actionData.getActionType().equalsIgnoreCase("dischargeer")) && (!actionData.getActionType().equalsIgnoreCase("dischargetrauma")) &&
        (!actionData.getActionType().equalsIgnoreCase("cleanuper")) && (!actionData.getActionType().equalsIgnoreCase("cleanuptrauma")))
      return new SimpleErrorData("Request Error", "The actionType value provided was invalid.");
    
    //Create all the database stuff
    EMSDatabase database = null;
    
    try {
      if (Application.database == null)
        Application.database = MongoDBFactory.createMongoDatabase(Application.databaseHost, Application.databasePort, Application.databaseName);
      database = Application.database;
      database.initializeDBConnection();

      Hospital hospital = (Hospital)database.querySingleRow(EMSDatabase.HOSPITAL_TABLE_NAME, "hospitalID", actionData.getHospitalID());
      if (hospital == null)
        return new SimpleErrorData("No Results Found", "The provided hospitalID could not be found.");

      if (actionData.getActionType().equalsIgnoreCase("admiter")) {
        if (hospital.getErBedsFree() <= 0)
          return new SimpleErrorData("Insufficient Beds", "There are not enough available beds to perform this task");
        hospital.setErBedsFree(hospital.getErBedsFree() - 1);
        hospital.setErBedsOccupied(hospital.getErBedsOccupied() + 1);
      } else if (actionData.getActionType().equalsIgnoreCase("dischargeer")) {
        if (hospital.getErBedsOccupied() <= 0)
          return new SimpleErrorData("Insufficient Beds", "There are not enough available beds to perform this task");
        hospital.setErBedsOccupied(hospital.getErBedsOccupied() - 1);
        hospital.setErBedsCleanup(hospital.getErBedsCleanup() + 1);
      } else if (actionData.getActionType().equalsIgnoreCase("cleanuper")) {
        if (hospital.getErBedsCleanup() <= 0)
          return new SimpleErrorData("Insufficient Beds", "There are not enough available beds to perform this task");
        hospital.setErBedsCleanup(hospital.getErBedsCleanup() - 1);
        hospital.setErBedsFree(hospital.getErBedsFree() + 1);
      } else if (actionData.getActionType().equalsIgnoreCase("admittrauma")) {
        if (hospital.getTraumaBedsFree() <= 0)
          return new SimpleErrorData("Insufficient Beds", "There are not enough available beds to perform this task");
        hospital.setTraumaBedsFree(hospital.getTraumaBedsFree() - 1);
        hospital.setTraumaBedsOccupied(hospital.getTraumaBedsOccupied() + 1);
      } else if (actionData.getActionType().equalsIgnoreCase("dischargetrauma")) {
        if (hospital.getTraumaBedsOccupied() <= 0)
          return new SimpleErrorData("Insufficient Beds", "There are not enough available beds to perform this task");
        hospital.setTraumaBedsOccupied(hospital.getTraumaBedsOccupied() - 1);
        hospital.setTraumaBedsCleanup(hospital.getTraumaBedsCleanup() + 1);
      } else if (actionData.getActionType().equalsIgnoreCase("cleanuptrauma")) {
        if (hospital.getTraumaBedsCleanup() <= 0)
          return new SimpleErrorData("Insufficient Beds", "There are not enough available beds to perform this task");
        hospital.setTraumaBedsCleanup(hospital.getTraumaBedsCleanup() - 1);
        hospital.setTraumaBedsFree(hospital.getTraumaBedsFree() + 1);
      }
      
      //Push the change back into the database
      database.updateHospitalData(hospital);
      
      HospitalBeds beds = new HospitalBeds();
      beds.setErBedsFree(hospital.getErBedsFree());
      beds.setErBedsOccupied(hospital.getErBedsOccupied());
      beds.setErBedsCleanup(hospital.getErBedsCleanup());
      beds.setTraumaBedsFree(hospital.getTraumaBedsFree());
      beds.setTraumaBedsOccupied(hospital.getTraumaBedsOccupied());
      beds.setTraumaBedsCleanup(hospital.getTraumaBedsCleanup());

      return beds;
    } catch (DatabaseOperationException doe) {
      doe.printStackTrace();
      return new SimpleErrorData("Database Operation Error", "An error occurred running the request: " + doe.getMessage());
    } catch (ConfigurationException ce) {
      ce.printStackTrace();
      return new SimpleErrorData("Database Configuration Error", "An error occurred accessing the database: " + ce.getMessage());
    } 
  }
}
