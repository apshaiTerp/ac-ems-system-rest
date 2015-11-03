package com.ac.ems.rest.controller;

import java.util.Date;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ac.ems.data.DispatchDetails;
import com.ac.ems.data.enums.SeverityLevel;
import com.ac.ems.db.EMSDatabase;
import com.ac.ems.db.MongoDBFactory;
import com.ac.ems.db.exception.ConfigurationException;
import com.ac.ems.db.exception.DatabaseOperationException;
import com.ac.ems.rest.Application;
import com.ac.ems.rest.data.DispatchDetailSubmit;
import com.ac.ems.rest.message.SimpleErrorData;
import com.ac.ems.rest.message.SimpleMessageData;

/**
 * @author ac010168
 *
 */
@RestController
@RequestMapping("/dispatch")
public class DispatchDetailsController {
  
  @RequestMapping(method = RequestMethod.GET, produces="application/json;charset=UTF-8")
  public Object getDispatchDetails(@RequestParam(value="id", defaultValue="-1") long dispatchID,
                                   @RequestParam(value="filterevent", defaultValue="yes") String filterEvent) {
    if (!filterEvent.equalsIgnoreCase("yes") && !filterEvent.equalsIgnoreCase("no"))
      return new SimpleErrorData("Invalid Parameters", "The filterevent value was not valid.");
    
    //Create all the database stuff
    EMSDatabase database = null;
    
    try {
      if (Application.database == null)
        Application.database = MongoDBFactory.createMongoDatabase(Application.databaseHost, Application.databasePort, Application.databaseName);
      database = Application.database;
      database.initializeDBConnection();
      
      Object result = null;
      if (dispatchID != -1) {
        //We want to get just a single detail
        result = database.querySingleRow(EMSDatabase.DISPATCH_DETAILS_TABLE_NAME, "dispatchID", dispatchID);
        if (result == null)
          return new SimpleErrorData("No Results Found", "No Results Found");
      } else {
        result = database.getDispatchDetails(filterEvent.equalsIgnoreCase("yes"));
      }

      return result;
    } catch (DatabaseOperationException doe) {
      doe.printStackTrace();
      return new SimpleErrorData("Database Operation Error", "An error occurred running the request: " + doe.getMessage());
    } catch (ConfigurationException ce) {
      ce.printStackTrace();
      return new SimpleErrorData("Database Configuration Error", "An error occurred accessing the database: " + ce.getMessage());
    } 
  }

  @RequestMapping(method = RequestMethod.POST, consumes = "application/json;charset=UTF-8", produces="application/json;charset=UTF-8")
  public Object addNewDispatchDetail(@RequestBody DispatchDetailSubmit detail) {
    if (detail == null)
      return new SimpleErrorData("Request Error", "There was no valid dispatch data provided");
    if (detail.getPatientName() == null)
      return new SimpleErrorData("Request Error", "The provided patient name was not valid");
    if (detail.getPatientAgeRange() == null)
      return new SimpleErrorData("Request Error", "The provided patient age range was not valid");
    if (detail.getPatientGender() == null)
      return new SimpleErrorData("Request Error", "The provided patient gender was not valid");
    if (detail.getPatientAddress() == null)
      return new SimpleErrorData("Request Error", "The provided patient address was not valid");
    if (detail.getPatientComplaint() == null)
      return new SimpleErrorData("Request Error", "The provided patient complaint was not valid");
    if (detail.getReportedByName() == null)
      return new SimpleErrorData("Request Error", "The provided reported by name was not valid");
    if (detail.getDispatchUserID() <= 0l)
      return new SimpleErrorData("Request Error", "The provided dispatch userID was not valid");
    if (detail.getReportedSeverity() == null)
      return new SimpleErrorData("Request Error", "The provided severity level was not valid");
    if (detail.getProviderID() <= 0)
      return new SimpleErrorData("Request Error", "The provided providerID was not valid");
    if (detail.getIncidentLat() == 0.0)
      return new SimpleErrorData("Request Error", "The provided incident latitude value was not valid");
    if (detail.getIncidentLon() == 0.0)
      return new SimpleErrorData("Request Error", "The provided incident longitude value was not valid");
    
    if ((!detail.getPatientAgeRange().equalsIgnoreCase("unknown")) && (!detail.getPatientAgeRange().equalsIgnoreCase("child")) &&
        (!detail.getPatientAgeRange().equalsIgnoreCase("teen")) && (!detail.getPatientAgeRange().equalsIgnoreCase("adult")))
      return new SimpleErrorData("Request Error", "The provided patient age range was not valid");
    if ((!detail.getPatientGender().equalsIgnoreCase("unknown")) && (!detail.getPatientGender().equalsIgnoreCase("male")) &&
        (!detail.getPatientGender().equalsIgnoreCase("female")))
      return new SimpleErrorData("Request Error", "The provided patient gender was not valid");
    if ((!detail.getReportedSeverity().equalsIgnoreCase("Unknown")) && (!detail.getReportedSeverity().equalsIgnoreCase("Non-Critical Injuries")) &&
        (!detail.getReportedSeverity().equalsIgnoreCase("Minor Trauma")) && (!detail.getReportedSeverity().equalsIgnoreCase("Severe Trauma")) &&
        (!detail.getReportedSeverity().equalsIgnoreCase("Severe Burns")) && (!detail.getReportedSeverity().equalsIgnoreCase("STEMI")) &&
        (!detail.getReportedSeverity().equalsIgnoreCase("Stroke")))
      return new SimpleErrorData("Request Error", "The provided severity level was not valid");
    
    //Before we commit this record, we need to convert it into the proper DispatchDetails object
    //This means we need to cast the severity level, and add the dispatchID and date
    DispatchDetails newDispatch = new DispatchDetails();

    newDispatch.setPatientName(detail.getPatientName());
    newDispatch.setPatientAddress(detail.getPatientAddress());
    newDispatch.setPatientGender(detail.getPatientGender());
    newDispatch.setPatientAgeRange(detail.getPatientAgeRange());
    newDispatch.setPatientComplaint(detail.getPatientComplaint());
    newDispatch.setReportedByName(detail.getReportedByName());
    newDispatch.setDispatchUserID(detail.getDispatchUserID());
    newDispatch.setProviderID(detail.getProviderID());
    newDispatch.setIncidentLat(detail.getIncidentLat());
    newDispatch.setIncidentLon(detail.getIncidentLon());
    //Set to the current date
    newDispatch.setDispatchReceivedDate(new Date());

    //We need to cast this correctly
    if (detail.getReportedSeverity().equalsIgnoreCase("Unknown")) newDispatch.setReportedSeverity(SeverityLevel.UNKNOWN);
    if (detail.getReportedSeverity().equalsIgnoreCase("Non-Critical Injuries")) {
      if (detail.getPatientAgeRange().equalsIgnoreCase("adult") || detail.getPatientAgeRange().equalsIgnoreCase("unknown"))
        newDispatch.setReportedSeverity(SeverityLevel.NONCRITICAL);
      else newDispatch.setReportedSeverity(SeverityLevel.PEDNONCRITICAL);
    }
    if (detail.getReportedSeverity().equalsIgnoreCase("Minor Trauma")) {
      if (detail.getPatientAgeRange().equalsIgnoreCase("adult") || detail.getPatientAgeRange().equalsIgnoreCase("unknown"))
        newDispatch.setReportedSeverity(SeverityLevel.MINORTRAUMA);
      else newDispatch.setReportedSeverity(SeverityLevel.PEDTRAUMA);
    }
    if (detail.getReportedSeverity().equalsIgnoreCase("Severe Trauma")) {
      if (detail.getPatientAgeRange().equalsIgnoreCase("adult") || detail.getPatientAgeRange().equalsIgnoreCase("unknown"))
        newDispatch.setReportedSeverity(SeverityLevel.SEVERETRAUMA);
      else newDispatch.setReportedSeverity(SeverityLevel.PEDTRAUMA);
    }
    if (detail.getReportedSeverity().equalsIgnoreCase("Severe Burns")) {
      if (detail.getPatientAgeRange().equalsIgnoreCase("adult") || detail.getPatientAgeRange().equalsIgnoreCase("unknown"))
        newDispatch.setReportedSeverity(SeverityLevel.BURN);
      else newDispatch.setReportedSeverity(SeverityLevel.PEDBURN);
    }
    if (detail.getReportedSeverity().equalsIgnoreCase("STEMI")) newDispatch.setReportedSeverity(SeverityLevel.STEMI);
    if (detail.getReportedSeverity().equalsIgnoreCase("Stroke")) newDispatch.setReportedSeverity(SeverityLevel.STROKE);
    
    //Create all the database stuff
    EMSDatabase database = null;
    
    try {
      if (Application.database == null)
        Application.database = MongoDBFactory.createMongoDatabase(Application.databaseHost, Application.databasePort, Application.databaseName);
      database = Application.database;
      database.initializeDBConnection();
      
      newDispatch.setDispatchID(database.getGenericMaxID(EMSDatabase.DISPATCH_DETAILS_TABLE_NAME, "dispatchID") + 1);
      
      database.insertDispatchDetailsData(newDispatch);

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
