package com.ac.ems.rest.data;

import java.util.Date;

import com.ac.ems.data.DispatchDetails;
import com.ac.ems.data.DispatchEvent;
import com.ac.ems.data.enums.SeverityLevelConverter;
import com.ac.ems.rest.message.SimpleMessageData;

/**
 * @author ac010168
 *
 */
public class AmbulanceDispatchData extends SimpleMessageData {

  //Core Event Fields
  /** The eventID that was generated for this event */
  private long   eventID;
  /** The dispatch ID that was generated for this dispatch */
  private long   dispatchID;
  /** The hospital ID recommended at dispatch */
  private long   recommendedHospitalID;
  /** The current state of the event */
  private String eventState;
  /** The date the event began */
  private Date   eventStartDate;

  //Event Fields that don't get set until activily transporting patients
  /** The actual destination hospital.  Can be blank (-1). */
  private long   targetHospitalID;
  /** The date transport of the patient began.  Can be null. */
  private Date   beginTransportDate;
  /** The observed age range of the patient */
  private String actualAgeRange;  //Should be limited to "child", "teen", "adult", "unknown"
  /** The observed severity of the patient's condition */
  private String observedSeverity;
  
  //Core fields from the Dispatch
  /** The patient name.  Can be undefined. */
  private String patientName;
  /** The patient Gender */
  private String patientGender;   //Should be limited to "Male", "Female", "Unknown"
  /** The age range of the patient */
  private String patientAgeRange;  //Should be limited to "child", "teen", "adult", "unknown"
  /** The address where response is requested */
  private String patientAddress;
  /** The primary complaint recorded as part of the dispatch */
  private String patientComplaint;
  /** The reported initial severity level (May not be known) */
  private String reportedSeverity;
  /** The name of the reporter */
  private String reportedByName;
  /** Need to store this somewhere */
  private double incidentLat;
  /** Need to store this somewhere */
  private double incidentLon;

  //Potential Additional details from Hospital data
  /** Text name for the recommended Hospital, if one exists */
  private String recommendedHospitalName;
  /** Text name for the target Hospital, if assigned */
  private String targetHospitalName;
  
  public AmbulanceDispatchData() {
    super ("Success", "Found the Event Associated with this Ambulance.");
    eventID                 = -1L;
    dispatchID              = -1L;
    recommendedHospitalID   = -1L;
    targetHospitalID        = -1L;
    eventState              = null;
    eventStartDate          = null;
    beginTransportDate      = null;
    actualAgeRange          = null;
    observedSeverity        = null;

    patientName             = null;
    patientGender           = null;
    patientAgeRange         = null;
    patientAddress          = null;
    patientComplaint        = null;
    reportedSeverity        = null;
    reportedByName          = null;
    incidentLat             = 0.0;
    incidentLon             = 0.0;
    
    recommendedHospitalName = null;
    targetHospitalName      = null;
  }
  
  public void assignDispatchEvent(DispatchEvent event) {
    eventID                 = event.getEventID();
    dispatchID              = event.getDispatchID();
    recommendedHospitalID   = event.getRecommendedHospitalID();
    targetHospitalID        = event.getTargetHospitalID();
    eventState              = event.getEventState();
    eventStartDate          = event.getEventStartDate();
    beginTransportDate      = event.getBeginTransportDate();
    actualAgeRange          = event.getActualAgeRange();
    observedSeverity        = SeverityLevelConverter.convertSeverityToString(event.getObservedSeverity());
  }
  
  public void assignDispatchDetails(DispatchDetails detail) {
    patientName             = detail.getPatientName();
    patientGender           = detail.getPatientGender();
    patientAgeRange         = detail.getPatientAgeRange();
    patientAddress          = detail.getPatientAddress();
    patientComplaint        = detail.getPatientComplaint();
    reportedSeverity        = SeverityLevelConverter.convertSeverityToString(detail.getReportedSeverity());
    reportedByName          = detail.getReportedByName();
    incidentLat             = detail.getIncidentLat();
    incidentLon             = detail.getIncidentLon();
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
   * @return the recommendedHospitalID
   */
  public long getRecommendedHospitalID() {
    return recommendedHospitalID;
  }

  /**
   * @param recommendedHospitalID the recommendedHospitalID to set
   */
  public void setRecommendedHospitalID(long recommendedHospitalID) {
    this.recommendedHospitalID = recommendedHospitalID;
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
   * @return the eventStartDate
   */
  public Date getEventStartDate() {
    return eventStartDate;
  }

  /**
   * @param eventStartDate the eventStartDate to set
   */
  public void setEventStartDate(Date eventStartDate) {
    this.eventStartDate = eventStartDate;
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
   * @return the beginTransportDate
   */
  public Date getBeginTransportDate() {
    return beginTransportDate;
  }

  /**
   * @param beginTransportDate the beginTransportDate to set
   */
  public void setBeginTransportDate(Date beginTransportDate) {
    this.beginTransportDate = beginTransportDate;
  }

  /**
   * @return the actualAgeRange
   */
  public String getActualAgeRange() {
    return actualAgeRange;
  }

  /**
   * @param actualAgeRange the actualAgeRange to set
   */
  public void setActualAgeRange(String actualAgeRange) {
    this.actualAgeRange = actualAgeRange;
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
   * @return the patientGender
   */
  public String getPatientGender() {
    return patientGender;
  }

  /**
   * @param patientGender the patientGender to set
   */
  public void setPatientGender(String patientGender) {
    this.patientGender = patientGender;
  }

  /**
   * @return the patientAgeRange
   */
  public String getPatientAgeRange() {
    return patientAgeRange;
  }

  /**
   * @param patientAgeRange the patientAgeRange to set
   */
  public void setPatientAgeRange(String patientAgeRange) {
    this.patientAgeRange = patientAgeRange;
  }

  /**
   * @return the patientAddress
   */
  public String getPatientAddress() {
    return patientAddress;
  }

  /**
   * @param patientAddress the patientAddress to set
   */
  public void setPatientAddress(String patientAddress) {
    this.patientAddress = patientAddress;
  }

  /**
   * @return the patientComplaint
   */
  public String getPatientComplaint() {
    return patientComplaint;
  }

  /**
   * @param patientComplaint the patientComplaint to set
   */
  public void setPatientComplaint(String patientComplaint) {
    this.patientComplaint = patientComplaint;
  }

  /**
   * @return the reportedSeverity
   */
  public String getReportedSeverity() {
    return reportedSeverity;
  }

  /**
   * @param reportedSeverity the reportedSeverity to set
   */
  public void setReportedSeverity(String reportedSeverity) {
    this.reportedSeverity = reportedSeverity;
  }

  /**
   * @return the reportedByName
   */
  public String getReportedByName() {
    return reportedByName;
  }

  /**
   * @param reportedByName the reportedByName to set
   */
  public void setReportedByName(String reportedByName) {
    this.reportedByName = reportedByName;
  }

  /**
   * @return the incidentLat
   */
  public double getIncidentLat() {
    return incidentLat;
  }

  /**
   * @param incidentLat the incidentLat to set
   */
  public void setIncidentLat(double incidentLat) {
    this.incidentLat = incidentLat;
  }

  /**
   * @return the incidentLon
   */
  public double getIncidentLon() {
    return incidentLon;
  }

  /**
   * @param incidentLon the incidentLon to set
   */
  public void setIncidentLon(double incidentLon) {
    this.incidentLon = incidentLon;
  }

  /**
   * @return the recommendedHospitalName
   */
  public String getRecommendedHospitalName() {
    return recommendedHospitalName;
  }

  /**
   * @param recommendedHospitalName the recommendedHospitalName to set
   */
  public void setRecommendedHospitalName(String recommendedHospitalName) {
    this.recommendedHospitalName = recommendedHospitalName;
  }

  /**
   * @return the targetHospitalName
   */
  public String getTargetHospitalName() {
    return targetHospitalName;
  }

  /**
   * @param targetHospitalName the targetHospitalName to set
   */
  public void setTargetHospitalName(String targetHospitalName) {
    this.targetHospitalName = targetHospitalName;
  }
}
