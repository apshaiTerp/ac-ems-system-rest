package com.ac.ems.rest.data;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author ac010168
 *
 */
public class EventStatusChangeData {

  private String eventState;
  private String observedAge;
  private String observedSeverity;
  private long   targetHospitalID;
  private String changeDescription;
  private long   userID;

  public EventStatusChangeData() {
    eventState        = null;
    observedAge       = null;
    observedSeverity  = null;
    targetHospitalID  = -1L;
    changeDescription = null;
    userID            = -1L;
  }
  
  public EventStatusChangeData(String jsonString) {
    super();
    ObjectMapper mapper = new ObjectMapper();
    try {
      EventStatusChangeData jsonData = mapper.readValue(jsonString, EventStatusChangeData.class);
      eventState        = jsonData.eventState;
      observedAge       = jsonData.observedAge;
      observedSeverity  = jsonData.observedSeverity;
      targetHospitalID  = jsonData.targetHospitalID;
      changeDescription = jsonData.changeDescription;
      userID            = jsonData.userID;
    } catch (JsonParseException jpe) {
      jpe.printStackTrace();
    } catch (JsonMappingException jme) {
      jme.printStackTrace();
    } catch (IOException ioe) {
      ioe.printStackTrace();
    }
  }

  /**
   * @return the eventState
   */
  public String getEventState() {
    return eventState;
  }

  /**
   * @param eventState the eventState to set
   */
  public void setEventState(String eventState) {
    this.eventState = eventState;
  }

  /**
   * @return the observedAge
   */
  public String getObservedAge() {
    return observedAge;
  }

  /**
   * @param observedAge the observedAge to set
   */
  public void setObservedAge(String observedAge) {
    this.observedAge = observedAge;
  }

  /**
   * @return the observedSeverity
   */
  public String getObservedSeverity() {
    return observedSeverity;
  }

  /**
   * @param observedSeverity the observedSeverity to set
   */
  public void setObservedSeverity(String observedSeverity) {
    this.observedSeverity = observedSeverity;
  }

  /**
   * @return the targetHospitalID
   */
  public long getTargetHospitalID() {
    return targetHospitalID;
  }

  /**
   * @param targetHospitalID the targetHospitalID to set
   */
  public void setTargetHospitalID(long targetHospitalID) {
    this.targetHospitalID = targetHospitalID;
  }

  /**
   * @return the changeDescription
   */
  public String getChangeDescription() {
    return changeDescription;
  }

  /**
   * @param changeDescription the changeDescription to set
   */
  public void setChangeDescription(String changeDescription) {
    this.changeDescription = changeDescription;
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
