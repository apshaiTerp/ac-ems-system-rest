package com.ac.ems.rest.controller;

import java.text.DecimalFormat;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}
