package com.ac.ems.rest.data;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author ac010168
 *
 */
public class AmbulanceLocationData {

  /** The unique ID for this ambulance */
  private long   ambulanceID;
  /** The current latitude of the ambulance */
  private double ambLat;
  /** The current longitude of the ambulance */
  private double ambLon;
  /** The event for which this ambulance is responding */
  private long   eventID;

  public AmbulanceLocationData() {
    ambulanceID = -1L;
    ambLat      = 0.0;
    ambLon      = 0.0;
    eventID     = -1L;
  }
  
  public AmbulanceLocationData(String jsonString) {
    super();
    ObjectMapper mapper = new ObjectMapper();
    try {
      AmbulanceLocationData jsonData = mapper.readValue(jsonString, AmbulanceLocationData.class);
      ambulanceID  = jsonData.ambulanceID;
      ambLat       = jsonData.ambLat;
      ambLon       = jsonData.ambLon;
      eventID      = jsonData.eventID;
    } catch (JsonParseException jpe) {
      jpe.printStackTrace();
    } catch (JsonMappingException jme) {
      jme.printStackTrace();
    } catch (IOException ioe) {
      ioe.printStackTrace();
    }
  }

  /**
   * @return the ambulanceID
   */
  public long getAmbulanceID() {
    return ambulanceID;
  }

  /**
   * @param ambulanceID the ambulanceID to set
   */
  public void setAmbulanceID(long ambulanceID) {
    this.ambulanceID = ambulanceID;
  }

  /**
   * @return the ambLat
   */
  public double getAmbLat() {
    return ambLat;
  }

  /**
   * @param ambLat the ambLat to set
   */
  public void setAmbLat(double ambLat) {
    this.ambLat = ambLat;
  }

  /**
   * @return the ambLon
   */
  public double getAmbLon() {
    return ambLon;
  }

  /**
   * @param ambLon the ambLon to set
   */
  public void setAmbLon(double ambLon) {
    this.ambLon = ambLon;
  }

  /**
   * @return the eventID
   */
  public long getEventID() {
    return eventID;
  }

  /**
   * @param eventID the eventID to set
   */
  public void setEventID(long eventID) {
    this.eventID = eventID;
  }
}
