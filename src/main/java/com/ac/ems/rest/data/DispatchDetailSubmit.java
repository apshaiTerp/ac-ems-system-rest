package com.ac.ems.rest.data;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author ac010168
 *
 */
public class DispatchDetailSubmit {

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
  /** The user ID for the dispatcher who assigned this request */
  private long   dispatchUserID;
  /** Redesign to associate to a specific provider */
  private long   providerID;
  /** Need to store this somewhere */
  private double incidentLat;
  /** Need to store this somewhere */
  private double incidentLon;
  
  public DispatchDetailSubmit() {
    patientName          = null;
    patientGender        = null;
    patientAgeRange      = null;
    patientAddress       = null;
    patientComplaint     = null;
    reportedSeverity     = null;
    reportedByName       = null;
    dispatchUserID       = -1L;
    providerID           = -1L;
    incidentLat          = 0.0;
    incidentLon          = 0.0;
  }

  /**
   * Advanced Constructor designed to construct the object from a JSON record
   * 
   * @param jsonString
   */
  public DispatchDetailSubmit(String jsonString) {
    super();
    ObjectMapper mapper = new ObjectMapper();
    try {
      DispatchDetailSubmit jsonData = mapper.readValue(jsonString, DispatchDetailSubmit.class);
      patientName          = jsonData.patientName;
      patientGender        = jsonData.patientGender;
      patientAgeRange      = jsonData.patientAgeRange;
      patientAddress       = jsonData.patientAddress;
      patientComplaint     = jsonData.patientComplaint;
      reportedSeverity     = jsonData.reportedSeverity;
      reportedByName       = jsonData.reportedByName;
      dispatchUserID       = jsonData.dispatchUserID;
      providerID           = jsonData.providerID;
      incidentLat          = jsonData.incidentLat;
      incidentLon          = jsonData.incidentLon;
    } catch (JsonParseException jpe) {
      jpe.printStackTrace();
    } catch (JsonMappingException jme) {
      jme.printStackTrace();
    } catch (IOException ioe) {
      ioe.printStackTrace();
    }
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
   * @return the dispatchUserID
   */
  public long getDispatchUserID() {
    return dispatchUserID;
  }

  /**
   * @param dispatchUserID the dispatchUserID to set
   */
  public void setDispatchUserID(long dispatchUserID) {
    this.dispatchUserID = dispatchUserID;
  }

  /**
   * @return the providerID
   */
  public long getProviderID() {
    return providerID;
  }

  /**
   * @param providerID the providerID to set
   */
  public void setProviderID(long providerID) {
    this.providerID = providerID;
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
}
