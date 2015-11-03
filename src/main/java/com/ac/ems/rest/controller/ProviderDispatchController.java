package com.ac.ems.rest.controller;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ac.ems.data.Ambulance;
import com.ac.ems.data.DispatchDetails;
import com.ac.ems.data.DispatchEvent;
import com.ac.ems.data.DispatchEventLog;
import com.ac.ems.data.EMSProvider;
import com.ac.ems.data.enums.EventState;
import com.ac.ems.db.EMSDatabase;
import com.ac.ems.db.MongoDBFactory;
import com.ac.ems.db.exception.ConfigurationException;
import com.ac.ems.db.exception.DatabaseOperationException;
import com.ac.ems.rest.Application;
import com.ac.ems.rest.data.DispatchAmbulanceData;
import com.ac.ems.rest.data.GenericListSuccessData;
import com.ac.ems.rest.message.SimpleErrorData;
import com.ac.ems.rest.message.SimpleMessageData;

/**
 * @author ac010168
 *
 */
@RestController
@RequestMapping("/provider/dispatch")
public class ProviderDispatchController {

  @RequestMapping(method = RequestMethod.GET, produces="application/json;charset=UTF-8")
  public Object getEMSProvider(@RequestParam(value="id") long providerID) {
    if (providerID < 0)
      return new SimpleErrorData("Invalid Parameters", "An invalid providerID was provided");

    //Create all the database stuff
    EMSDatabase database = null;
    
    try {
      if (Application.database == null)
        Application.database = MongoDBFactory.createMongoDatabase(Application.databaseHost, Application.databasePort, Application.databaseName);
      database = Application.database;
      database.initializeDBConnection();
    
      EMSProvider result = (EMSProvider)database.querySingleRow(EMSDatabase.EMS_PROVIDER_TABLE_NAME, "providerID", providerID);
      if (result == null)
        return new SimpleErrorData("Invalid Provider", "The providerID provided is not a valid EMS Provider");
      
      List<DispatchDetails> curResults    = database.getDispatchDetails(true);
      List<DispatchDetails> filterResults = new LinkedList<DispatchDetails>();
      
      for (DispatchDetails detail : curResults) {
        if (detail.getProviderID() == providerID)
          filterResults.add(detail);
      }
      
      GenericListSuccessData data = new GenericListSuccessData();
      data.setResultList(filterResults);
      
      return data;
    } catch (DatabaseOperationException doe) {
      doe.printStackTrace();
      return new SimpleErrorData("Database Operation Error", "An error occurred running the request: " + doe.getMessage());
    } catch (ConfigurationException ce) {
      ce.printStackTrace();
      return new SimpleErrorData("Database Configuration Error", "An error occurred accessing the database: " + ce.getMessage());
    } 
  }
  
  @RequestMapping(method = RequestMethod.POST, consumes = "application/json;charset=UTF-8", produces="application/json;charset=UTF-8")
  public Object assignAmbulanceToDispatch(@RequestBody DispatchAmbulanceData data) {
    if (data == null)
      return new SimpleErrorData("Request Error", "There was no valid dispatch data provided");
    if (data.getDispatchID() <= 0)
      return new SimpleErrorData("Request Error", "There was no dispatchID provided");
    if (data.getAmbulanceID() <= 0)
      return new SimpleErrorData("Request Error", "There was no ambulanceID provided");
    if (data.getUserID() <= 0)
      return new SimpleErrorData("Request Error", "There was no userID provided");
    
    //Create all the database stuff
    EMSDatabase database = null;
    
    try {
      if (Application.database == null)
        Application.database = MongoDBFactory.createMongoDatabase(Application.databaseHost, Application.databasePort, Application.databaseName);
      database = Application.database;
      database.initializeDBConnection();
    
      DispatchDetails detail = (DispatchDetails)database.querySingleRow(EMSDatabase.DISPATCH_DETAILS_TABLE_NAME, "dispatchID", data.getDispatchID());
      if (detail == null)
        return new SimpleErrorData("No Dispatch Found", "There was no Dispatch data found with this dispatchID");
      
      Ambulance ambulance = (Ambulance)database.querySingleRow(EMSDatabase.AMBULANCE_TABLE_NAME, "ambulanceID", data.getAmbulanceID());
      if (ambulance == null)
        return new SimpleErrorData("No Ambulance Found", "There was no Ambulance data found with this dispatchID");
      
      //Now that we have the key pieces, let's start to assemble the new event
      DispatchEvent event  = new DispatchEvent();
      DispatchEventLog log = new DispatchEventLog();
      
      //Fill out the event template
      event.setDispatchID(detail.getDispatchID());
      event.setAmbulanceID(ambulance.getAmbulanceID());
      event.setEventState(EventState.ASSIGNED_STATE_TEXT);
      event.setEventStartDate(new Date());
      
      log.setCurState(EventState.ASSIGNED_STATE_TEXT);
      log.setChangeDescription("Initial Ambulance Assignment");
      log.setChangedOnDate(new Date());
      log.setChangedByUserID(data.getUserID());
      
      //Generate the new ID value
      event.setEventID(database.getGenericMaxID(EMSDatabase.DISPATCH_EVENT_TABLE_NAME, "eventID"));
      log.setEventID(event.getEventID());
      
      //Write data to database
      database.insertDispatchEventData(event);
      database.insertDispatchEventLogData(log);

      return new SimpleMessageData("Success", "The new Dispatch was successfully created");
    } catch (DatabaseOperationException doe) {
      doe.printStackTrace();
      return new SimpleErrorData("Database Operation Error", "An error occurred running the request: " + doe.getMessage());
    } catch (ConfigurationException ce) {
      ce.printStackTrace();
      return new SimpleErrorData("Database Configuration Error", "An error occurred accessing the database: " + ce.getMessage());
    } 
  }

}