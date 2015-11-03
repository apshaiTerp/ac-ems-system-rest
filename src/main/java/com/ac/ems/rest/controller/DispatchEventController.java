package com.ac.ems.rest.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ac.ems.data.DispatchDetails;
import com.ac.ems.data.DispatchEvent;
import com.ac.ems.data.EMSProvider;
import com.ac.ems.data.enums.SeverityLevelConverter;
import com.ac.ems.db.EMSDatabase;
import com.ac.ems.db.MongoDBFactory;
import com.ac.ems.db.exception.ConfigurationException;
import com.ac.ems.db.exception.DatabaseOperationException;
import com.ac.ems.rest.Application;
import com.ac.ems.rest.data.EventTableData;
import com.ac.ems.rest.data.GenericListSuccessData;
import com.ac.ems.rest.message.SimpleErrorData;

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

  //TODO - Need to add a PUT method (or sub controllers), a POST method, and then figure out about delete
}
