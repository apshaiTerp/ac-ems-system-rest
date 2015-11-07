package com.ac.ems.rest.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ac.ems.data.DispatchDetails;
import com.ac.ems.data.DispatchEvent;
import com.ac.ems.data.Hospital;
import com.ac.ems.db.EMSDatabase;
import com.ac.ems.db.MongoDBFactory;
import com.ac.ems.db.exception.ConfigurationException;
import com.ac.ems.db.exception.DatabaseOperationException;
import com.ac.ems.rest.Application;
import com.ac.ems.rest.data.AmbulanceDispatchData;
import com.ac.ems.rest.message.SimpleErrorData;
import com.ac.ems.rest.message.SimpleMessageData;

/**
 * @author ac010168
 *
 */
@RestController
@RequestMapping("/ambulance/event")
public class AmbulanceEventController {

  @RequestMapping(method = RequestMethod.GET, produces="application/json;charset=UTF-8")
  public Object getAmbulanceEvent(@RequestParam(value="id") long ambulanceID) {
    if (ambulanceID < 0)
      return new SimpleErrorData("Invalid Parameters", "The result value was not valid.");
    
    //Create all the database stuff
    EMSDatabase database = null;
    
    try {
      if (Application.database == null)
        Application.database = MongoDBFactory.createMongoDatabase(Application.databaseHost, Application.databasePort, Application.databaseName);
      database = Application.database;
      database.initializeDBConnection();
    
      DispatchEvent event = (DispatchEvent)database.querySingleRow(EMSDatabase.DISPATCH_EVENT_TABLE_NAME, "ambulanceID", ambulanceID);
      if (event == null)
        return new SimpleMessageData("No Results Found", "There are no events associated to this Ambulance");
      
      //We're going to make some assumptions that the data returned from the event is legit.
      DispatchDetails detail = (DispatchDetails)database.querySingleRow(EMSDatabase.DISPATCH_DETAILS_TABLE_NAME, "dispatchID", event.getDispatchID());
      Hospital recommendHospital = null;
      if (event.getRecommendedHospitalID() != -1) {
        recommendHospital = (Hospital)database.querySingleRow(EMSDatabase.HOSPITAL_TABLE_NAME, "hospitalID", event.getRecommendedHospitalID());
      }

      Hospital targetHospital = null;
      if (event.getTargetHospitalID() != -1) {
        targetHospital = (Hospital)database.querySingleRow(EMSDatabase.HOSPITAL_TABLE_NAME, "hospitalID", event.getTargetHospitalID());
      }

      AmbulanceDispatchData result = new AmbulanceDispatchData();
      result.assignDispatchEvent(event);
      result.assignDispatchDetails(detail);
      if (recommendHospital != null)
        result.setRecommendedHospitalName(recommendHospital.getHospitalName());
      if (targetHospital != null)
        result.setTargetHospitalName(targetHospital.getHospitalName());
      
      return result;
    } catch (DatabaseOperationException doe) {
      doe.printStackTrace();
      return new SimpleErrorData("Database Operation Error", "An error occurred running the request: " + doe.getMessage());
    } catch (ConfigurationException ce) {
      ce.printStackTrace();
      return new SimpleErrorData("Database Configuration Error", "An error occurred accessing the database: " + ce.getMessage());
    } 
  }

}
