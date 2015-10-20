package com.ac.ems.rest.data;

import java.util.List;

import com.ac.ems.data.EMSProvider;

/**
 * Stripped down version of EMSProvider with only the data needed to decide which Ambulance to
 * assign.
 * 
 * @author ac010168
 *
 */
public class EMSProviderWithDistance implements Comparable<EMSProviderWithDistance> {
  
  /** The unique ID for this provider */
  private long       providerID;
  /** The display name for this provider */
  private String     providerName;
  /** The text address for the EMS Provier */
  private String     providerAddress;
  /** The latitude for this provider's location */
  private double     providerLat;
  /** The longitude for this provider's location */
  private double     providerLon;
  /** The list of ambulances currently available right now */
  private List<Long> availAmbulances;

  /** The distance text (the number of miles) */
  private String distanceText;
  /** The distance value (in seconds) */
  private int    distanceValue;
  /** Derived ETA String */
  private String etaString;
  
  
  public EMSProviderWithDistance(EMSProvider provider, String distanceText, int distanceValue) {
    providerID      = provider.getProviderID();
    providerName    = provider.getProviderName();
    providerAddress = provider.getProviderAddress();
    providerLat     = provider.getProviderLat();
    providerLon     = provider.getProviderLon();
    availAmbulances = provider.getAvailAmbulances();
    
    this.distanceText  = distanceText;
    this.distanceValue = distanceValue;
    
    int minutes = distanceValue / 60;
    int seconds = distanceValue % 60;
    if (seconds < 10)
      etaString = "" + minutes + ":0" + seconds;
    else etaString = "" + minutes + ":" + seconds;
  }
  
  @Override
  public int compareTo(EMSProviderWithDistance o) {
    return distanceValue - o.distanceValue;
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
   * @return the providerAddress
   */
  public String getProviderAddress() {
    return providerAddress;
  }

  /**
   * @param providerAddress the providerAddress to set
   */
  public void setProviderAddress(String providerAddress) {
    this.providerAddress = providerAddress;
  }

  /**
   * @return the providerLat
   */
  public double getProviderLat() {
    return providerLat;
  }

  /**
   * @param providerLat the providerLat to set
   */
  public void setProviderLat(double providerLat) {
    this.providerLat = providerLat;
  }

  /**
   * @return the providerLon
   */
  public double getProviderLon() {
    return providerLon;
  }

  /**
   * @param providerLon the providerLon to set
   */
  public void setProviderLon(double providerLon) {
    this.providerLon = providerLon;
  }

  /**
   * @return the availAmbulances
   */
  public List<Long> getAvailAmbulances() {
    return availAmbulances;
  }

  /**
   * @param availAmbulances the availAmbulances to set
   */
  public void setAvailAmbulances(List<Long> availAmbulances) {
    this.availAmbulances = availAmbulances;
  }

  /**
   * @return the distanceText
   */
  public String getDistanceText() {
    return distanceText;
  }

  /**
   * @param distanceText the distanceText to set
   */
  public void setDistanceText(String distanceText) {
    this.distanceText = distanceText;
  }

  /**
   * @return the distanceValue
   */
  public int getDistanceValue() {
    return distanceValue;
  }

  /**
   * @param distanceValue the distanceValue to set
   */
  public void setDistanceValue(int distanceValue) {
    this.distanceValue = distanceValue;
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
