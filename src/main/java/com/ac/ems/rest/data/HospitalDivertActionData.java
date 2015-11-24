package com.ac.ems.rest.data;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author ac010168
 *
 */
public class HospitalDivertActionData {
  private long   hospitalID;
  private String actionType;  //Will be one of [er, trauma, burn, stroke, stemi]
  private String divertState;
  private long   userID;

  public HospitalDivertActionData() {
    hospitalID  = -1;
    actionType  = null;
    divertState = null;
    userID      = -1;
  }
  
  public HospitalDivertActionData(String jsonString) {
    super();
    ObjectMapper mapper = new ObjectMapper();
    try {
      HospitalDivertActionData jsonData = mapper.readValue(jsonString,  HospitalDivertActionData.class);
      hospitalID  = jsonData.hospitalID;
      actionType  = jsonData.actionType;
      divertState = jsonData.divertState;
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
   * @return the hospitalID
   */
  public long getHospitalID() {
    return hospitalID;
  }

  /**
   * @param hospitalID the hospitalID to set
   */
  public void setHospitalID(long hospitalID) {
    this.hospitalID = hospitalID;
  }

  /**
   * @return the actionType
   */
  public String getActionType() {
    return actionType;
  }

  /**
   * @param actionType the actionType to set
   */
  public void setActionType(String actionType) {
    this.actionType = actionType;
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

  /**
   * @return the divertState
   */
  public String getDivertState() {
    return divertState;
  }

  /**
   * @param divertState the divertState to set
   */
  public void setDivertState(String divertState) {
    this.divertState = divertState;
  }

}
