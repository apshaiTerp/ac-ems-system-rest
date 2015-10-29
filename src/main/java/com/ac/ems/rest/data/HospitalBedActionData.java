package com.ac.ems.rest.data;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author ac010168
 *
 */
public class HospitalBedActionData {
  
  private long   hospitalID;
  private String actionType;  //Will be one of [admiter, dischargeer, cleanuper, admittrauma, dischargetrauma, cleanuptrauma]

  public HospitalBedActionData() {
    hospitalID = -1;
    actionType = null;
  }
  
  public HospitalBedActionData(String jsonString) {
    super();
    ObjectMapper mapper = new ObjectMapper();
    try {
      HospitalBedActionData jsonData = mapper.readValue(jsonString,  HospitalBedActionData.class);
      hospitalID = jsonData.hospitalID;
      actionType = jsonData.actionType;
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
}
