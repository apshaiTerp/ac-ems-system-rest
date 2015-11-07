package com.ac.ems.rest.controller;

import java.util.Date;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ac.ems.data.Ambulance;
import com.ac.ems.data.AmbulanceTravelHistory;
import com.ac.ems.db.EMSDatabase;
import com.ac.ems.db.MongoDBFactory;
import com.ac.ems.db.exception.ConfigurationException;
import com.ac.ems.db.exception.DatabaseOperationException;
import com.ac.ems.rest.Application;
import com.ac.ems.rest.data.AmbulanceLocationData;
import com.ac.ems.rest.message.SimpleErrorData;
import com.ac.ems.rest.message.SimpleMessageData;

/**
 * @author ac010168
 *
 */
@RestController
@RequestMapping("/ambulance")
public class AmbulanceController {

  @RequestMapping(method = RequestMethod.GET, produces="application/json;charset=UTF-8")
  public Object getAmbulanceInfo(@RequestParam(value="id") long ambulanceID) {
    
    //TODO
    return new SimpleErrorData("Unsupported Operation", "I haven't done this yet.  Not sure I need it at the moment.");
  }
  
  @RequestMapping(method = RequestMethod.POST, consumes = "application/json;charset=UTF-8", produces="application/json;charset=UTF-8")
  public Object postAmbulanceLocation(@RequestBody AmbulanceLocationData data) {
    if (data == null)
      return new SimpleErrorData("Request Error", "There was no valid ambulance data provided");
    if (data.getAmbulanceID() < 0)
      return new SimpleErrorData("Request Error", "There was no valid ambulance data provided");

    //Create all the database stuff
    EMSDatabase database = null;
    
    try {
      if (Application.database == null)
        Application.database = MongoDBFactory.createMongoDatabase(Application.databaseHost, Application.databasePort, Application.databaseName);
      database = Application.database;
      database.initializeDBConnection();

      Ambulance ambulance = (Ambulance)database.querySingleRow(EMSDatabase.AMBULANCE_TABLE_NAME, "ambulanceID", data.getAmbulanceID());
      if (ambulance == null)
        return new SimpleErrorData("Ambulance Not Found", "I could not find an Ambulance with the provided Ambulance ID");
      
      ambulance.setAmbLat(data.getAmbLat());
      ambulance.setAmbLon(data.getAmbLon());
      ambulance.setLastUpdate(new Date());
      
      database.updateAmbulanceData(ambulance);
      
      if (data.getEventID() != -1) {
        AmbulanceTravelHistory history = new AmbulanceTravelHistory();
        history.setAmbulanceID(data.getAmbulanceID());
        history.setAmbLat(data.getAmbLat());
        history.setAmbLon(data.getAmbLon());
        history.setEventID(data.getEventID());
        history.setRecordedDate(new Date());
        
        database.insertAmbulanceTravelHistoryData(history);
      }
      
      return new SimpleMessageData("Success", "Location Update posted successfully");
    } catch (DatabaseOperationException doe) {
      doe.printStackTrace();
      return new SimpleErrorData("Database Operation Error", "An error occurred running the request: " + doe.getMessage());
    } catch (ConfigurationException ce) {
      ce.printStackTrace();
      return new SimpleErrorData("Database Configuration Error", "An error occurred accessing the database: " + ce.getMessage());
    } 
  }
}
