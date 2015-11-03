package com.ac.ems.rest.data;

/**
 * @author ac010168
 *
 */
public class EventTableData {

  private long eventID;
  private long dispatchID;
  private long ambulanceID;
  private String eventState;
  private String patientSeverity;
  private String destinationHospital;
  private String eventStartDate;
  
  public EventTableData() {
    eventID             = -1;
    dispatchID          = -1;
    ambulanceID         = -1;
    eventState          = null;
    patientSeverity     = null;
    destinationHospital = null;
    eventStartDate      = null;
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
   * @return the patientSeverity
   */
  public String getPatientSeverity() {
    return patientSeverity;
  }

  /**
   * @param patientSeverity the patientSeverity to set
   */
  public void setPatientSeverity(String patientSeverity) {
    this.patientSeverity = patientSeverity;
  }

  /**
   * @return the destinationHospital
   */
  public String getDestinationHospital() {
    return destinationHospital;
  }

  /**
   * @param destinationHospital the destinationHospital to set
   */
  public void setDestinationHospital(String destinationHospital) {
    this.destinationHospital = destinationHospital;
  }

  /**
   * @return the eventStartDate
   */
  public String getEventStartDate() {
    return eventStartDate;
  }

  /**
   * @param eventStartDate the eventStartDate to set
   */
  public void setEventStartDate(String eventStartDate) {
    this.eventStartDate = eventStartDate;
  }
}
