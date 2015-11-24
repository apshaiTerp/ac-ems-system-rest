package com.ac.ems.rest.controller;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.ac.ems.data.Ambulance;
import com.ac.ems.data.DispatchDetails;
import com.ac.ems.data.DispatchEvent;
import com.ac.ems.data.Hospital;
import com.ac.ems.data.enums.SeverityLevelConverter;
import com.ac.ems.db.EMSDatabase;
import com.ac.ems.db.MongoDBFactory;
import com.ac.ems.db.exception.ConfigurationException;
import com.ac.ems.db.exception.DatabaseOperationException;
import com.ac.ems.rest.Application;
import com.ac.ems.rest.data.GenericListSuccessData;
import com.ac.ems.rest.data.HospitalInboundData;
import com.ac.ems.rest.message.SimpleErrorData;
import com.ac.ems.rest.message.SimpleMessageData;

/**
 * @author ac010168
 *
 */
@RestController
@RequestMapping("/hospital/inbound")
public class HospitalInboundController {
  
  //This is the 
  public static String URL_ROOT = "https://maps.googleapis.com/maps/api/distancematrix/json?units=imperial&origins=<originLat>,<originLon>&destinations=<destLat>,<destLon>&clientID=662159346848-deoqbkle9scov01ehtobm9lealqglt5a.apps.googleusercontent.com";

  public final static DecimalFormat formatter = new DecimalFormat("##.########");

  @RequestMapping(method = RequestMethod.GET, produces="application/json;charset=UTF-8")
  public Object getInboundAmbulances(@RequestParam(value="id") long hospitalID) {
    if (hospitalID < 0)
      return new SimpleErrorData("Invalid Parameters", "The hospitalID value was not valid.");
    
    //Create all the database stuff
    EMSDatabase database = null;
    
    try {
      if (Application.database == null)
        Application.database = MongoDBFactory.createMongoDatabase(Application.databaseHost, Application.databasePort, Application.databaseName);
      database = Application.database;
      database.initializeDBConnection();
    
      List<DispatchEvent> events = database.getEventsByTargetHospital(hospitalID);
      if (events == null)
        return new SimpleMessageData("No Results Found", "There are no events associated to this Hospital");
      if (events.size() == 0)
        return new SimpleMessageData("No Results Found", "There are no events associated to this Hospital");

      List<HospitalInboundData> resultList = new ArrayList<HospitalInboundData>(events.size());
      
      for (DispatchEvent event : events) {
        //Assume all this stuff is there, since we already qualified on a targetHospital
        DispatchDetails detail  = (DispatchDetails)database.querySingleRow(EMSDatabase.DISPATCH_DETAILS_TABLE_NAME, "dispatchID", event.getDispatchID());
        Hospital targetHospital = (Hospital)database.querySingleRow(EMSDatabase.HOSPITAL_TABLE_NAME, "hospitalID", event.getTargetHospitalID());
        Ambulance ambulance     = (Ambulance)database.querySingleRow(EMSDatabase.AMBULANCE_TABLE_NAME, "ambulanceID", event.getAmbulanceID());
        
        HospitalInboundData data = new HospitalInboundData();
        data.setEventID(event.getEventID());
        data.setAmbulanceID(event.getAmbulanceID());
        data.setPatientName(detail.getPatientName());
        data.setPatientInfo(event.getActualAgeRange() + " " + detail.getPatientGender());
        data.setPatientCondition(SeverityLevelConverter.convertSeverityToString(event.getObservedSeverity()));
        
        //Now we need to get the ETA.  We need the ambulance coords and hospital coords.
        String dynamicURL = URL_ROOT.replace("<originLat>", formatter.format(ambulance.getAmbLat()));
        dynamicURL = dynamicURL.replace("<originLon>", formatter.format(ambulance.getAmbLon()));
        dynamicURL = dynamicURL.replace("<destLat>", formatter.format(targetHospital.getHospitalLat()));
        dynamicURL = dynamicURL.replace("<destLon>", formatter.format(targetHospital.getHospitalLon()));
        
        //DEBUG
        System.out.println ("dynamicURL:" + dynamicURL);
        
        RestTemplate restTemplate = new RestTemplate();
        //MyCustomObject models the return fields from the JSON API call.
        String jsonResults = restTemplate.getForObject(dynamicURL, String.class);
        JSONObject jsonObject = new JSONObject(jsonResults);
        JSONObject distanceObject = jsonObject.getJSONArray("rows").getJSONObject(0).getJSONArray("elements").getJSONObject(0).getJSONObject("distance");
        JSONObject durationObject = jsonObject.getJSONArray("rows").getJSONObject(0).getJSONArray("elements").getJSONObject(0).getJSONObject("duration");
        
        String distanceText = distanceObject.getString("text");
        int distanceValue   = durationObject.getInt("value");
        
        data.setEtaString(convertTimeToETA(distanceValue) + " mins.  (" + distanceText + ")");
        
        resultList.add(data);
      }
      
      GenericListSuccessData result = new GenericListSuccessData();
      result.setResultList(resultList);
      
      return result;
    } catch (DatabaseOperationException doe) {
      doe.printStackTrace();
      return new SimpleErrorData("Database Operation Error", "An error occurred running the request: " + doe.getMessage());
    } catch (ConfigurationException ce) {
      ce.printStackTrace();
      return new SimpleErrorData("Database Configuration Error", "An error occurred accessing the database: " + ce.getMessage());
    } 
  }

  /**
   * Simple helper method to convert seconds into a time value.
   * 
   * @param timeInSeconds
   * 
   * @return A String in the format of minutes:seconds.
   */
  private String convertTimeToETA(int timeInSeconds) {
    int minutes = timeInSeconds / 60;
    int seconds = timeInSeconds % 60;
    if (seconds < 10)
      return "" + minutes + ":0" + seconds;
    else return "" + minutes + ":" + seconds;
  }
}
