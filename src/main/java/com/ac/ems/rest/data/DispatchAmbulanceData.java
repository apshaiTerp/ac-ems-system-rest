package com.ac.ems.rest.data;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author ac010168
 *
 */
public class DispatchAmbulanceData {
  
  private long dispatchID;
  private long ambulanceID;
  private long userID;
  
  public DispatchAmbulanceData() {
    dispatchID  = -1L;
    ambulanceID = -1L;
    userID      = -1L;
  }
  
  public DispatchAmbulanceData(String jsonString) {
    super();
    ObjectMapper mapper = new ObjectMapper();
    try {
      DispatchAmbulanceData jsonData = mapper.readValue(jsonString, DispatchAmbulanceData.class);
      dispatchID  = jsonData.dispatchID;
      ambulanceID = jsonData.ambulanceID;
      userID      = jsonData.userID;
    } catch (JsonParseException jpe) {
      jpe.printStackTrace();
    } catch (JsonMappingException jme) {
      jme.printStackTrace();
    } catch (IOException ioe) {
      ioe.printStackTrace();
    }
  }

  /**
   * @return the dispatchID
   */
  public long getDispatchID() {
    return dispatchID;
  }

  /**
   * @param dispatchID the dispatchID to set
   */
  public void setDispatchID(long dispatchID) {
    this.dispatchID = dispatchID;
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
   * @return the userID
   */
  public long getUserID() {
    return userID;
  }

  /**
   * @param userID the userID to set
   */
  public void setUserID(long userID) {
    this.userID = userID;
  }

}
