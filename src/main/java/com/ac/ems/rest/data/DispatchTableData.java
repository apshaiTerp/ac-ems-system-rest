package com.ac.ems.rest.data;

/**
 * @author ac010168
 *
 */
public class DispatchTableData {
  
  private long   dispatchID;
  private String patientAddress;
  private String severityLevel;
  private String providerName;
  private String dispatchDate;
  
  public DispatchTableData() {
    dispatchID     = -1;
    patientAddress = null;
    severityLevel  = null;
    providerName   = null;
    dispatchDate   = null;
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
   * @return the severityLevel
   */
  public String getSeverityLevel() {
    return severityLevel;
  }

  /**
   * @param severityLevel the severityLevel to set
   */
  public void setSeverityLevel(String severityLevel) {
    this.severityLevel = severityLevel;
  }

  /**
   * @return the providerName
   */
  public String getProviderName() {
    return providerName;
  }

  /**
   * @param providerName the providerName to set
   */
  public void setProviderName(String providerName) {
    this.providerName = providerName;
  }

  /**
   * @return the dispatchDate
   */
  public String getDispatchDate() {
    return dispatchDate;
  }

  /**
   * @param dispatchDate the dispatchDate to set
   */
  public void setDispatchDate(String dispatchDate) {
    this.dispatchDate = dispatchDate;
  }

}
