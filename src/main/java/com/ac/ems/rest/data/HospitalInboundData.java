package com.ac.ems.rest.data;

/**
 * @author ac010168
 *
 */
public class HospitalInboundData {

  private long eventID;
  private long ambulanceID;
  private String patientName;
  private String patientInfo;
  private String patientCondition;
  private String etaString;
  
  public HospitalInboundData() {
    eventID          = -1L;
    ambulanceID      = -1L;
    patientName      = null;
    patientInfo      = null;
    patientCondition = null;
    etaString        = null;
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
   * @return the patientName
   */
  public String getPatientName() {
    return patientName;
  }
  /**
   * @param patientName the patientName to set
   */
  public void setPatientName(String patientName) {
    this.patientName = patientName;
  }
  /**
   * @return the patientInfo
   */
  public String getPatientInfo() {
    return patientInfo;
  }
  /**
   * @param patientInfo the patientInfo to set
   */
  public void setPatientInfo(String patientInfo) {
    this.patientInfo = patientInfo;
  }
  /**
   * @return the patientCondition
   */
  public String getPatientCondition() {
    return patientCondition;
  }
  /**
   * @param patientCondition the patientCondition to set
   */
  public void setPatientCondition(String patientCondition) {
    this.patientCondition = patientCondition;
  }
  /**
   * @return the etaString
   */
  public String getEtaString() {
    return etaString;
  }
  /**
   * @param etaString the etaString to set
   */
  public void setEtaString(String etaString) {
    this.etaString = etaString;
  }
}
