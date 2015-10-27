package com.ac.ems.rest.controller;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONObject;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.ac.ems.data.Hospital;
import com.ac.ems.db.EMSDatabase;
import com.ac.ems.db.MongoDBFactory;
import com.ac.ems.db.exception.ConfigurationException;
import com.ac.ems.db.exception.DatabaseOperationException;
import com.ac.ems.rest.Application;
import com.ac.ems.rest.data.HospitalRouteData;
import com.ac.ems.rest.message.SimpleErrorData;

/**
 * @author ac010168
 *
 */
@RestController
@RequestMapping("/nearesthospital")
public class NearestHospitalController {

  public final static DecimalFormat formatter = new DecimalFormat("##.########");
  
  //This is the 
  public static String URL_ROOT = "https://maps.googleapis.com/maps/api/distancematrix/json?units=imperial&origins=<originLat>,<originLon>&destinations=<destLat>,<destLon>&clientID=662159346848-deoqbkle9scov01ehtobm9lealqglt5a.apps.googleusercontent.com";

  /**
   * 
   * @param ambLat    <latitude coordinate of ambulance>
   * @param ambLon    <longitude coordinate of ambulance>
   * @param age       Should be one of {child|teen|adult}
   * @param condition Should be one of {severe|minor|basicER|burn|stemi|stroke}
   * @param exclude   A List, potentially comma separated, of hospitals to exclude from the algorithm
   * 
   * @return A List of 3 Hospitals that should be reflect our top three (or potentially fewer) destinations.
   */
  @RequestMapping(method = RequestMethod.GET, produces="application/json;charset=UTF-8")
  public Object getShortestPath(@RequestParam(value="amblat", defaultValue="0.0") double ambLat,
                                @RequestParam(value="amblon") double ambLon,
                                @RequestParam(value="age") String age,
                                @RequestParam(value="condition") String condition,
                                @RequestParam(value="exclude", defaultValue="") String exclude) {
    
    if (ambLat == 0.0)
      return new SimpleErrorData("Invalid Parameters", "No Ambulance Latitude Value was provided");
    if (ambLon == 0.0)
      return new SimpleErrorData("Invalid Parameters", "No Ambulance Longitude Value was provided");
    if (age == null)
      return new SimpleErrorData("Invalid Parameters", "No Age Value was provided");
    if (!age.equalsIgnoreCase("child") && !age.equalsIgnoreCase("teen") && !age.equalsIgnoreCase("adult"))
      return new SimpleErrorData("Invalid Parameters", "The Age Value was not in the set of {child|teen|adult}");
    if (condition == null)
      return new SimpleErrorData("Invalid Parameters", "No Condition Value was provided");
    if (!condition.equalsIgnoreCase("severe") && !condition.equalsIgnoreCase("minor") && 
        !condition.equalsIgnoreCase("stroke") && !condition.equalsIgnoreCase("basicER") &&
        !condition.equalsIgnoreCase("burn") && !condition.equalsIgnoreCase("stemi"))
      return new SimpleErrorData("Invalid Parameters", "The Condition Value was not in the set of {severe|minor|basicER|burn|stemi|stroke}");
    List<Long> excludeIDs = null;
    try {
      if (exclude.trim().length() == 0) excludeIDs = new ArrayList<Long>(0);
      else                              excludeIDs = parseExcludeIDs(exclude);
    } catch (Throwable t) {
      return new SimpleErrorData("Invalid Parameters", "Unable to Parse ID values from exclude list");
    }
    
    //New - This is where we are going to convert the condition value into the 'true' condition
    //value that will map to the correct hospital
    String trueCondition = null;
    String altCondition  = null;
    
    //If it's a child, we want to convert the true term to the Pediatric term and set the true term as the alternate
    if (!age.equalsIgnoreCase("adult")) {
      if (condition.equalsIgnoreCase("basicER")) {
        trueCondition = "basicERPed";
        altCondition  = "basicER";
      } else if (condition.equalsIgnoreCase("burn")) {
        trueCondition = "burnPed";
        altCondition  = "trauma2";
      } else if (condition.equalsIgnoreCase("severe")) {
        trueCondition = "traumaPed";
        altCondition  = "trauma2";
      } else if (condition.equalsIgnoreCase("minor")) {
        trueCondition = "traumaPed";
        altCondition  = "trauma3";
      } else {
        //If we got here, we somehow have a child manifesting with a stroke or heart attack....
        trueCondition = "traumaPed";
        altCondition  = "trauma2";
      }
    } else {
      //These are only the adult conditions
      if (condition.equalsIgnoreCase("basicER")) {
        trueCondition = "basicER";
        //No need for an alternate here
        altCondition  = null;
      } else if (condition.equalsIgnoreCase("burn")) {
        trueCondition = "burn";
        altCondition  = "trauma2";
      } else if (condition.equalsIgnoreCase("stemi")) {
        trueCondition = "STEMI";
        //No need for an alternate here, this patient needs to go to a STEMI center
        altCondition  = null;
      } else if (condition.equalsIgnoreCase("stroke")) {
        trueCondition = "stroke";
        //No need for an alternate here, this patient needs to go to a Stroke Center
        altCondition   = null;
      } else if (condition.equalsIgnoreCase("severe")) {
        trueCondition = "trauma2";
        altCondition  = "trauma3";
      } else if (condition.equalsIgnoreCase("minor")) {
        trueCondition = "trauma3";
        altCondition  = null;
      } 
    }
    if (trueCondition == null)
      return new SimpleErrorData("Invalid Parameters", "I'm not sure what to make of condition (" + condition + "), but it wasn't right");
    
    //DEBUG Output
    System.out.println ("Processing trueCondition: " + trueCondition);
    System.out.println ("Considering altCondition: " + altCondition);

    //Create all the database stuff
    EMSDatabase database = null;
    
    try {
      if (Application.database == null)
        Application.database = MongoDBFactory.createMongoDatabase(Application.databaseHost, Application.databasePort, Application.databaseName);
      database = Application.database;
      database.initializeDBConnection();
    
      //Get List of Hospitals with free beds at with this severity
      List<Hospital> qualifyingHospitals = database.getAvailableHospitalsByCondition(trueCondition, excludeIDs);
      
      //DEBUG - Remove before completing
      System.out.println ("There are " + qualifyingHospitals.size() + " candidate hospitals:");
      for (Hospital hospital : qualifyingHospitals) {
        System.out.println (hospital.getHospitalName() + " (hospitalID:" + hospital.getHospitalID() + ")");
      }
      
      //Build a larger exclusionList for the alternate site list
      List<Long> alternateExcludeIDs = new ArrayList<Long>();
      for (Hospital hospital : qualifyingHospitals)
        alternateExcludeIDs.add(hospital.getHospitalID());
      alternateExcludeIDs.addAll(excludeIDs);
      
      List<Hospital> alternateHospitals = null;
      if (altCondition == null) alternateHospitals = new ArrayList<Hospital>(0);
      else alternateHospitals = database.getAvailableHospitalsByCondition(altCondition, alternateExcludeIDs);
      
      //DEBUG - Remove before completing
      System.out.println ("There are " + alternateHospitals.size() + " candidate alternate hospitals:");
      for (Hospital hospital : alternateHospitals) {
        System.out.println (hospital.getHospitalName() + " (hospitalID:" + hospital.getHospitalID() + ")");
      }
      
      //Now let's copy everything over into the new lists
      List<HospitalRouteData> qualifyingRouteHospitals = new ArrayList<HospitalRouteData>(qualifyingHospitals.size());
      List<HospitalRouteData> alternateRouteHospitals  = new ArrayList<HospitalRouteData>(alternateHospitals.size());
      
      for (Hospital hospital : qualifyingHospitals) {
        HospitalRouteData route = new HospitalRouteData();
        route.setHospitalID(hospital.getHospitalID());
        route.setHospitalLat(hospital.getHospitalLat());
        route.setHospitalLon(hospital.getHospitalLon());
        route.setHospitalAddress(hospital.getAddress());
        route.computeRawDistance(ambLat, ambLon);
        
        //If the true Condition is a specialty, we want to indicate that in the name
        if (!trueCondition.equalsIgnoreCase("basicER")) {
          if (trueCondition.equalsIgnoreCase("basicERPed")) {
            route.setHospitalName(hospital.getHospitalName() + " (Pediatric Emergency Care)");
          } else if (trueCondition.equalsIgnoreCase("traumaPed")) {
            route.setHospitalName(hospital.getHospitalName() + " (Pediatric Trauma Center)");
          } else if (trueCondition.equalsIgnoreCase("trauma1")) {
            route.setHospitalName(hospital.getHospitalName() + getTraumaText(hospital));
          } else if (trueCondition.equalsIgnoreCase("trauma2")) {
            route.setHospitalName(hospital.getHospitalName() + getTraumaText(hospital));
          } else if (trueCondition.equalsIgnoreCase("trauma3")) {
            route.setHospitalName(hospital.getHospitalName() + getTraumaText(hospital));
          } else if (trueCondition.equalsIgnoreCase("STEMI")) {
            route.setHospitalName(hospital.getHospitalName() + " (STEMI Receiving Center)");
          } else if (trueCondition.equalsIgnoreCase("stroke")) {
            route.setHospitalName(hospital.getHospitalName() + " (Primary Stroke Center)");
          } else if (trueCondition.equalsIgnoreCase("burn")) {
            route.setHospitalName(hospital.getHospitalName() + " (Burn Unit)");
          } else if (trueCondition.equalsIgnoreCase("burnPed")) {
            route.setHospitalName(hospital.getHospitalName() + " (Pediatric Burn Unit)");
          } else {
            //This shouldn't happen, but just in case
            route.setHospitalName(hospital.getHospitalName());
          }
        } else {
          route.setHospitalName(hospital.getHospitalName());
        }
        qualifyingRouteHospitals.add(route);
      }
      
      //This also applies to the alternateLists
      for (Hospital hospital : alternateHospitals) {
        HospitalRouteData route = new HospitalRouteData();
        route.setHospitalID(hospital.getHospitalID());
        route.setHospitalLat(hospital.getHospitalLat());
        route.setHospitalLon(hospital.getHospitalLon());
        route.setHospitalAddress(hospital.getAddress());
        route.computeRawDistance(ambLat, ambLon);

        if (altCondition == null) route.setHospitalName(hospital.getHospitalName());
        else if (!altCondition.equalsIgnoreCase("basicER")) {
          if (altCondition.equalsIgnoreCase("trauma1")) {
            route.setHospitalName(hospital.getHospitalName() + getTraumaText(hospital));
          } else if (altCondition.equalsIgnoreCase("trauma2")) {
            route.setHospitalName(hospital.getHospitalName() + getTraumaText(hospital));
          } else if (altCondition.equalsIgnoreCase("trauma3")) {
            route.setHospitalName(hospital.getHospitalName() + getTraumaText(hospital));
          }
        } else route.setHospitalName(hospital.getHospitalName());
        alternateRouteHospitals.add(route);
      }

      //Now we need to run the rawDistance Sort
      HospitalRouteData.sortByRawDistance = true;
      Collections.sort(qualifyingRouteHospitals);
      Collections.sort(alternateRouteHospitals);
      
      //Cut the list down
      while (qualifyingRouteHospitals.size() > 6)
        qualifyingRouteHospitals.remove(6);
      while (alternateRouteHospitals.size() > 6)
        alternateRouteHospitals.remove(6);
      
      //Now run the Google API and sort again for the qualifying list
      for (HospitalRouteData hospital : qualifyingRouteHospitals) {
        String dynamicURL = URL_ROOT.replace("<originLat>", formatter.format(ambLat));
        dynamicURL = dynamicURL.replace("<originLon>", formatter.format(ambLon));
        dynamicURL = dynamicURL.replace("<destLat>", formatter.format(hospital.getHospitalLat()));
        dynamicURL = dynamicURL.replace("<destLon>", formatter.format(hospital.getHospitalLon()));
        
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
        
        hospital.setDistanceText(distanceText);
        hospital.setDistanceValue(distanceValue);
        hospital.setEtaString(convertTimeToETA(distanceValue));
      }
      //Change the sort criteria and sort
      HospitalRouteData.sortByRawDistance = false;
      Collections.sort(qualifyingRouteHospitals);
      
      //Cut to top 3
      int capResultSize = 3;
      //This is important so we can ensure that the one single closest basicER also shows up in their recommendations
      if ((!age.equalsIgnoreCase("adult")) && (trueCondition.equalsIgnoreCase("basicERPed")))
        capResultSize = 2;
      
      while (qualifyingRouteHospitals.size() > capResultSize)
        qualifyingRouteHospitals.remove(capResultSize);
      
      //Check to now see if we need to add any alternates
      if (qualifyingRouteHospitals.size() < 3) {
        //Now run the Google API and sort again for the qualifying list
        for (HospitalRouteData hospital : alternateRouteHospitals) {
          String dynamicURL = URL_ROOT.replace("<originLat>", formatter.format(ambLat));
          dynamicURL = dynamicURL.replace("<originLon>", formatter.format(ambLon));
          dynamicURL = dynamicURL.replace("<destLat>", formatter.format(hospital.getHospitalLat()));
          dynamicURL = dynamicURL.replace("<destLon>", formatter.format(hospital.getHospitalLon()));
          
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
          
          hospital.setDistanceText(distanceText);
          hospital.setDistanceValue(distanceValue);
          hospital.setEtaString(convertTimeToETA(distanceValue));
        }
        //Change the sort criteria and sort
        HospitalRouteData.sortByRawDistance = false;
        Collections.sort(alternateRouteHospitals);
        
        int pos = 0;
        while ((qualifyingRouteHospitals.size() < 3) && (pos < alternateRouteHospitals.size())) {
          qualifyingRouteHospitals.add(alternateRouteHospitals.get(pos));
          pos++;
        }
      }
      
      return qualifyingRouteHospitals;
    } catch (DatabaseOperationException doe) {
      doe.printStackTrace();
      return new SimpleErrorData("Database Operation Error", "An error occurred running the request: " + doe.getMessage());
    } catch (ConfigurationException ce) {
      ce.printStackTrace();
      return new SimpleErrorData("Database Configuration Error", "An error occurred accessing the database: " + ce.getMessage());
    } 
  }
  
  /**
   * Simple helper method to cleanup the Trauma text, which should match the Hospital max.
   * This is needed because of how we store hospital levels of care.
   * 
   * @param hospital
   * 
   * @return A String with the right text description for Trauma Center capability
   */
  private String getTraumaText(Hospital hospital) {
    boolean hasTrauma1 = false;
    boolean hasTrauma2 = false;
    boolean hasTrauma3 = false;
    
    for (String level : hospital.getLevelOfCare()) {
      if (level.equalsIgnoreCase("trauma1"))      hasTrauma1 = true;
      else if (level.equalsIgnoreCase("trauma2")) hasTrauma2 = true;
      else if (level.equalsIgnoreCase("trauma3")) hasTrauma3 = true;
    }
    
    if (hasTrauma1) return " (Level I Trauma Center)";
    if (hasTrauma2) return " (Level II Trauma Center)";
    if (hasTrauma3) return " (Level III Trauma Center)";
    return "";
  }

  /**
   * Helper method to handle converting the comma-separated text list into a real list
   * 
   * @param exclude A comma-separated list of exclusion IDs (could be empty)
   * 
   * @return A List of all the IDs we want excluded.
   * 
   * @throws Exception In case something goes wrong.
   */
  private List<Long> parseExcludeIDs(String exclude) throws Exception {
    List<Long> excludeIDs = new LinkedList<Long>();
    if (exclude.trim().length() > 0) {
      while (true) {
        String longString = ""; 
        if (exclude.indexOf(",") != -1) {
          longString = exclude.substring(0, exclude.indexOf(","));
          exclude = exclude.substring(exclude.indexOf(",") + 1);
        } else {
          longString = exclude;
          exclude = "";
        }
        long actualValue = Long.parseLong(longString);
        excludeIDs.add(actualValue);
        if (exclude.trim().length() == 0)
          break;
      }
    }
    return excludeIDs;
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
