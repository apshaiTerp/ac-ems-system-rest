package com.ac.ems.rest.controller;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.JSONObject;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.ac.ems.data.EMSProvider;
import com.ac.ems.db.EMSDatabase;
import com.ac.ems.db.MongoDBFactory;
import com.ac.ems.db.exception.ConfigurationException;
import com.ac.ems.db.exception.DatabaseOperationException;
import com.ac.ems.rest.Application;
import com.ac.ems.rest.data.EMSProviderDistanceSortable;
import com.ac.ems.rest.data.EMSProviderWithDistance;
import com.ac.ems.rest.message.SimpleErrorData;

/**
 * Should handle GET requests at this root.  An example is:
 * localhost:8080/nearestambulance?incidentlat=39.232571&incidentlon=-94.551309
 * 
 * @author ac010168
 *
 */
@RestController
@RequestMapping("/nearestambulance")
public class NearestAmbulanceController {

  public final static DecimalFormat formatter = new DecimalFormat("##.########");
  
  //This is the 
  public static String URL_ROOT = "https://maps.googleapis.com/maps/api/distancematrix/json?units=imperial&origins=<originLat>,<originLon>&destinations=<destLat>,<destLon>&clientID=662159346848-deoqbkle9scov01ehtobm9lealqglt5a.apps.googleusercontent.com";

  /**
   * This is the GET method for getting the fastest available ambulance responders.
   * 
   * @param incidentLat The geocoordinates for the incident that needs responding to.
   * @param incidentLon The geocoordinates for the incident that needs responding to.
   * 
   * @return The Top 3 available ambulances for dispatch
   */
  @RequestMapping(method = RequestMethod.GET, produces="application/json;charset=UTF-8")
  public Object getNearestAmbulance(@RequestParam(value="incidentlat", defaultValue="0.0") double incidentLat,
                                    @RequestParam(value="incidentlon", defaultValue="0.0") double incidentLon) {
    
    if (incidentLat == 0.0)
      return new SimpleErrorData("Invalid Parameters", "No Incident Latitude Value was provided");
    if (incidentLon == 0.0)
      return new SimpleErrorData("Invalid Parameters", "No Incident Longitude Value was provided");
    
    //Create all the database stuff
    EMSDatabase database = null;
    
    try {
      if (Application.database == null)
        Application.database = MongoDBFactory.createMongoDatabase(Application.databaseHost, Application.databasePort, Application.databaseName);
      database = Application.database;
      database.initializeDBConnection();
      
      //Now we need to get the list of all EMSProviders that have available ambulances
      List<EMSProvider> providers = database.getProvidersWithAvailableAmbulances();
      
      //Once we have that list of providers, run the distance math, then sort
      List<EMSProviderDistanceSortable> providerSort = new ArrayList<EMSProviderDistanceSortable>(providers.size());
      for (EMSProvider provider : providers)
        providerSort.add(new EMSProviderDistanceSortable(provider, incidentLat, incidentLon));
      
      Collections.sort(providerSort);
      
      //Now that we have our list sorted, cut to Top 6, then run the Google API for distance
      List<EMSProviderWithDistance> topXList = new ArrayList<EMSProviderWithDistance>();
      for (int i = 0; i < Math.min(6, providers.size()); i++) {
        EMSProvider provider = providerSort.get(i).getProvider();
        
        //From the provider to the incident
        String dynamicURL = URL_ROOT.replace("<originLat>", formatter.format(provider.getProviderLat()));
        dynamicURL = dynamicURL.replace("<originLon>", formatter.format(provider.getProviderLon()));
        dynamicURL = dynamicURL.replace("<destLat>", formatter.format(incidentLat));
        dynamicURL = dynamicURL.replace("<destLon>", formatter.format(incidentLon));
        
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
        topXList.add(new EMSProviderWithDistance(provider, distanceText, distanceValue));
      }
      
      Collections.sort(topXList);
      
      //Shrink list down to top 3
      while (topXList.size() > 3)
        topXList.remove(3);
      
      return topXList;
    } catch (DatabaseOperationException doe) {
      doe.printStackTrace();
      return new SimpleErrorData("Database Operation Error", "An error occurred running the request: " + doe.getMessage());
    } catch (ConfigurationException ce) {
      ce.printStackTrace();
      return new SimpleErrorData("Database Configuration Error", "An error occurred accessing the database: " + ce.getMessage());
    } 
  }
}
