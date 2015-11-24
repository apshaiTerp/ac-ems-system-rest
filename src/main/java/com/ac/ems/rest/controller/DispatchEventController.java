package com.ac.ems.rest.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ac.ems.data.DispatchDetails;
import com.ac.ems.data.DispatchEvent;
import com.ac.ems.data.DispatchEventHistory;
import com.ac.ems.data.DispatchEventLog;
import com.ac.ems.data.EMSProvider;
import com.ac.ems.data.Hospital;
import com.ac.ems.data.enums.SeverityLevel;
import com.ac.ems.data.enums.SeverityLevelConverter;
import com.ac.ems.db.EMSDatabase;
import com.ac.ems.db.MongoDBFactory;
import com.ac.ems.db.exception.ConfigurationException;
import com.ac.ems.db.exception.DatabaseOperationException;
import com.ac.ems.rest.Application;
import com.ac.ems.rest.data.EventStatusChangeData;
import com.ac.ems.rest.data.EventTableData;
import com.ac.ems.rest.data.GenericListSuccessData;
import com.ac.ems.rest.message.SimpleErrorData;
import com.ac.ems.rest.message.SimpleMessageData;

/**
 * @author ac010168
 *
 */
@RestController
@RequestMapping("/event")
public class DispatchEventController {

  private final static SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yy HH:mm:ss z");

  @RequestMapping(method = RequestMethod.GET, produces="application/json;charset=UTF-8")
  public Object getDispatchEvent(@RequestParam(value="id", defaultValue="-1") long eventID,
                                 @RequestParam(value="providerid", defaultValue="-1") long providerID,
                                 @RequestParam(value="result", defaultValue="full") String resultType) {
    if (!resultType.equalsIgnoreCase("full") && !resultType.equalsIgnoreCase("table"))
      return new SimpleErrorData("Invalid Parameters", "The result value was not valid.");

    //Create all the database stuff
    EMSDatabase database = null;
    
    try {
      if (Application.database == null)
        Application.database = MongoDBFactory.createMongoDatabase(Application.databaseHost, Application.databasePort, Application.databaseName);
      database = Application.database;
      database.initializeDBConnection();
      
      Object result = null;
      if (eventID != -1) {
        //We want to get just a single detail
        result = database.querySingleRow(EMSDatabase.DISPATCH_EVENT_TABLE_NAME, "eventID", eventID);
        if (result == null)
          return new SimpleErrorData("No Results Found", "No Results Found");
      } else {
        //We want all, but we may want to filter to just events that relate to a single provider.
        List<DispatchEvent> resultList = null;
        if (providerID == -1)
          resultList = database.getActiveEvents();
        else {
          EMSProvider provider = (EMSProvider)database.querySingleRow(EMSDatabase.EMS_PROVIDER_TABLE_NAME, "providerID", providerID);
          if (provider == null)
            return new SimpleErrorData("No Results Found", "Could not find an EMS Provider with the given providerID");
          List<DispatchEvent> events = database.getActiveEvents();
          resultList = new LinkedList<DispatchEvent>();
          for (DispatchEvent event : events) {
            if (provider.getAmbulances().contains(event.getAmbulanceID()))
              resultList.add(event);
          }
        }
        
        //Now we need to check into formatting
        if (!resultType.equalsIgnoreCase("full")) {
          List<EventTableData> tableResults = new ArrayList<EventTableData>(resultList.size());
          for (DispatchEvent event : resultList) {
            EventTableData data = new EventTableData();
            data.setEventID(event.getEventID());
            data.setDispatchID(event.getDispatchID());
            data.setAmbulanceID(event.getAmbulanceID());
            data.setEventState(event.getEventState());
            data.setEventStartDate(formatter.format(event.getEventStartDate()));
            
            if (event.getObservedSeverity() == null) {
              DispatchDetails detail = (DispatchDetails)database.querySingleRow(EMSDatabase.DISPATCH_DETAILS_TABLE_NAME, "dispatchID", event.getDispatchID());
              data.setPatientSeverity(SeverityLevelConverter.convertSeverityToString(detail.getReportedSeverity()));
            } else data.setPatientSeverity(SeverityLevelConverter.convertSeverityToString(event.getObservedSeverity()));
            
            if (event.getTargetHospitalID() == -1) {
              if (event.getRecommendedHospitalID() == -1)
                data.setDestinationHospital("Not Yet Determined");
              else {
                Hospital hospital = (Hospital)database.querySingleRow(EMSDatabase.HOSPITAL_TABLE_NAME, "hospitalID", event.getRecommendedHospitalID());
                if (hospital == null)
                  data.setDestinationHospital("Not Yet Determined");
                else data.setDestinationHospital(hospital.getHospitalName());
              }
            } else {
              Hospital hospital = (Hospital)database.querySingleRow(EMSDatabase.HOSPITAL_TABLE_NAME, "hospitalID", event.getTargetHospitalID());
              if (hospital == null) {
                if (event.getRecommendedHospitalID() == -1)
                  data.setDestinationHospital("Not Yet Determined");
                else {
                  Hospital hospital2 = (Hospital)database.querySingleRow(EMSDatabase.HOSPITAL_TABLE_NAME, "hospitalID", event.getRecommendedHospitalID());
                  if (hospital2 == null)
                    data.setDestinationHospital("Not Yet Determined");
                  else data.setDestinationHospital(hospital2.getHospitalName());
                }
              } else data.setDestinationHospital(hospital.getHospitalName());
            }
            
            tableResults.add(data);
          }
          GenericListSuccessData success = new GenericListSuccessData();
          success.setResultList(tableResults);
          result = success;
        } else {
          GenericListSuccessData success = new GenericListSuccessData();
          success.setResultList(resultList);
          result = success;
        }
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

  @RequestMapping(method = RequestMethod.PUT, consumes = "application/json;charset=UTF-8", produces="application/json;charset=UTF-8")
  public Object updateEvent(@RequestParam(value="id", defaultValue="-1") long eventID,
                            @RequestBody EventStatusChangeData changeData) {
    if (changeData == null)
      return new SimpleErrorData("Update Error", "There was no valid change data provided");
    if (eventID == -1)
      return new SimpleErrorData("Update Error", "There was no valid eventID provided");
    if (changeData.getEventState() == null)
      return new SimpleErrorData("Update Error", "There was no event state provided");
    if (changeData.getObservedAge() != null) {
      if (!changeData.getObservedAge().equalsIgnoreCase("child") && !changeData.getObservedAge().equalsIgnoreCase("teen") && 
          !changeData.getObservedAge().equalsIgnoreCase("adult"))
        return new SimpleErrorData("Invalid Parameters", "The Age Value was not in the set of {child|teen|adult}");
    }
    if (changeData.getObservedSeverity() != null) {
      if (!changeData.getObservedSeverity().equalsIgnoreCase("severe") && !changeData.getObservedSeverity().equalsIgnoreCase("minor") && 
          !changeData.getObservedSeverity().equalsIgnoreCase("stroke") && !changeData.getObservedSeverity().equalsIgnoreCase("basicER") &&
          !changeData.getObservedSeverity().equalsIgnoreCase("burn") && !changeData.getObservedSeverity().equalsIgnoreCase("stemi"))
        return new SimpleErrorData("Invalid Parameters", "The Condition Value was not in the set of {severe|minor|basicER|burn|stemi|stroke}");
    }
    
    //Create all the database stuff
    EMSDatabase database = null;
    
    try {
      if (Application.database == null)
        Application.database = MongoDBFactory.createMongoDatabase(Application.databaseHost, Application.databasePort, Application.databaseName);
      database = Application.database;
      database.initializeDBConnection();

      DispatchEvent curEvent = (DispatchEvent)database.querySingleRow(EMSDatabase.DISPATCH_EVENT_TABLE_NAME, "eventID", eventID);
      if (curEvent == null)
        return new SimpleErrorData("Event Not Found", "I could not find an event with the provided eventID");
      
      //Now we need to figure out what needs to be done to transition the event
      if (curEvent.getEventState().equalsIgnoreCase("ASSIGNED") && 
          !(changeData.getEventState().equalsIgnoreCase("RESPONDING") || changeData.getEventState().equalsIgnoreCase("CALL CANCELLED")))
        return new SimpleErrorData("Invalid State Change", "This event is in an ASSIGNED state and can only transition to RESPONDING or CALL CANCELLED");
      if (curEvent.getEventState().equalsIgnoreCase("RESPONDING") && 
          !(changeData.getEventState().equalsIgnoreCase("ON SITE") || changeData.getEventState().equalsIgnoreCase("CALL CANCELLED")))
        return new SimpleErrorData("Invalid State Change", "This event is in an RESPONDING state and can only transition to ON SITE or CALL CANCELLED");
      if (curEvent.getEventState().equalsIgnoreCase("ON SITE") && 
          !(changeData.getEventState().equalsIgnoreCase("TRANSPORTING") || changeData.getEventState().equalsIgnoreCase("CALL CANCELLED")))
        return new SimpleErrorData("Invalid State Change", "This event is in an ON SITE state and can only transition to TRANSPORTING or CALL CANCELLED");
      if (curEvent.getEventState().equalsIgnoreCase("TRANSPORTING") && !(changeData.getEventState().equalsIgnoreCase("ARRIVED") || 
          changeData.getEventState().equalsIgnoreCase("CALL CANCELLED") || changeData.getEventState().equalsIgnoreCase("DIVERT")))
        return new SimpleErrorData("Invalid State Change", "This event is in an TRANSPORTING state and can only transition to ARRIVED, DIVERT, or CALL CANCELLED");
      if (curEvent.getEventState().equalsIgnoreCase("DIVERT") && 
          !(changeData.getEventState().equalsIgnoreCase("TRANSPORTING") || changeData.getEventState().equalsIgnoreCase("CALL CANCELLED")))
        return new SimpleErrorData("Invalid State Change", "This event is in an ON SITE state and can only transition to TRANSPORTING or CALL CANCELLED");
      
      //Change the item
      curEvent.setEventState(changeData.getEventState());
      
      if (changeData.getEventState().equalsIgnoreCase("TRANSPORTING")) {
        curEvent.setActualAgeRange(changeData.getObservedAge());
        curEvent.setTargetHospitalID(changeData.getTargetHospitalID());
        
        //Convert the True Condition
        if (changeData.getObservedSeverity().equalsIgnoreCase("basicER")) {
          if (changeData.getObservedAge().equalsIgnoreCase("adult"))
            curEvent.setObservedSeverity(SeverityLevel.NONCRITICAL);
          else curEvent.setObservedSeverity(SeverityLevel.PEDNONCRITICAL);
        } else if (changeData.getObservedSeverity().equalsIgnoreCase("minor")) {
          if (changeData.getObservedAge().equalsIgnoreCase("adult"))
            curEvent.setObservedSeverity(SeverityLevel.MINORTRAUMA);
          else curEvent.setObservedSeverity(SeverityLevel.PEDTRAUMA);
        } else if (changeData.getObservedSeverity().equalsIgnoreCase("severe")) {
          if (changeData.getObservedAge().equalsIgnoreCase("adult"))
            curEvent.setObservedSeverity(SeverityLevel.SEVERETRAUMA);
          else curEvent.setObservedSeverity(SeverityLevel.PEDTRAUMA);
        } else if (changeData.getObservedSeverity().equalsIgnoreCase("burn")) {
          if (changeData.getObservedAge().equalsIgnoreCase("adult"))
            curEvent.setObservedSeverity(SeverityLevel.BURN);
          else curEvent.setObservedSeverity(SeverityLevel.PEDBURN);
        } else if (changeData.getObservedSeverity().equalsIgnoreCase("stemi")) {
          curEvent.setObservedSeverity(SeverityLevel.STEMI);
        } else if (changeData.getObservedSeverity().equalsIgnoreCase("stroke")) {
          curEvent.setObservedSeverity(SeverityLevel.STROKE);
        }

        if (curEvent.getBeginTransportDate() == null)
          curEvent.setBeginTransportDate(new Date());
      } else if (changeData.getEventState().equalsIgnoreCase("DIVERT")) {
        curEvent.setActualAgeRange(null);
        curEvent.setObservedSeverity(null);
        curEvent.setTargetHospitalID(-1L);
      }
      
      DispatchEventLog log = new DispatchEventLog();
      log.setEventID(curEvent.getEventID());
      log.setCurState(curEvent.getEventState());
      log.setChangedOnDate(new Date());
      log.setChangeDescription(changeData.getChangeDescription());
      log.setChangedByUserID(changeData.getUserID());
      
      //We've now updated the event, we're ready to commit, and update the event log
      database.updateDispatchEventData(curEvent);
      database.insertDispatchEventLogData(log);
      
      //I believe at this point, if the status is now either ARRIVED or CALL CANCELLED, we need to move this event out
      //into the History table, then delete the actual event row (this is how it goes away).
      if (changeData.getEventState().equalsIgnoreCase("ARRIVED") || changeData.getEventState().equalsIgnoreCase("CALL CANCELED")) {
        DispatchEventHistory history = new DispatchEventHistory();
        history.setEventID(curEvent.getEventID());
        history.setDispatchID(curEvent.getDispatchID());
        history.setAmbulanceID(curEvent.getAmbulanceID());
        history.setRecommendedHospitalID(curEvent.getRecommendedHospitalID());
        history.setTargetHospitalID(curEvent.getTargetHospitalID());
        history.setEventResolvedState(curEvent.getEventState());
        history.setEventStartDate(curEvent.getEventStartDate());
        history.setBeginTransportDate(curEvent.getBeginTransportDate());
        history.setEventEndDate(new Date());
        history.setActualAgeRange(curEvent.getActualAgeRange());
        history.setObservedSeverity(curEvent.getObservedSeverity());
      
        database.insertDispatchEventHistoryData(history);
        database.deleteData(curEvent);
        
        //We also need to move the ambulance back into an available state
        DispatchDetails detail = (DispatchDetails)database.querySingleRow(EMSDatabase.DISPATCH_DETAILS_TABLE_NAME, "dispatchID", curEvent.getDispatchID());
        if (detail == null)
          return new SimpleErrorData("No Dispatch Found", "There was no Dispatch data found with this dispatchID");
        
        EMSProvider provider = (EMSProvider)database.querySingleRow(EMSDatabase.EMS_PROVIDER_TABLE_NAME, "providerID", detail.getProviderID());
        List<Long> assignedAmbulances = provider.getAssignedAmbulances();
        assignedAmbulances.remove(curEvent.getAmbulanceID());
        List<Long> availAmbulances = provider.getAvailAmbulances();
        availAmbulances.add(curEvent.getAmbulanceID());
        
        provider.setAvailAmbulances(availAmbulances);
        provider.setAssignedAmbulances(assignedAmbulances);
        
        database.updateEMSProviderData(provider);
      }
      
      
      return new SimpleMessageData("Success", "Successfully Updated the Event Status");
    } catch (DatabaseOperationException doe) {
      doe.printStackTrace();
      return new SimpleErrorData("Database Operation Error", "An error occurred running the request: " + doe.getMessage());
    } catch (ConfigurationException ce) {
      ce.printStackTrace();
      return new SimpleErrorData("Database Configuration Error", "An error occurred accessing the database: " + ce.getMessage());
    } 
  }
  
}
