package com.ac.ems.rest.data;

/**
 * @author ac010168
 *
 */
public class HospitalRouteData implements Comparable<HospitalRouteData> {

  public static boolean sortByRawDistance = true;
  
  private long   hospitalID;
  private String hospitalName;
  private double hospitalLat;
  private double hospitalLon;
  private String hospitalAddress;
  private Double rawDistance;
  private String distanceText;
  private int    distanceValue;
  private String etaString;
  
  /** Basic Constructor */
  public HospitalRouteData() {
    hospitalID      = 0;
    hospitalName    = null;
    hospitalLat     = 0.0;
    hospitalLon     = 0.0;
    hospitalAddress = null;
    rawDistance     = 0.0;
    distanceText    = null;
    distanceValue   = 0;
    etaString       = null;
  }
  
  /*
   * (non-Javadoc)
   * @see java.lang.Comparable#compareTo(java.lang.Object)
   */
  @Override
  public int compareTo(HospitalRouteData arg0) {
    if (sortByRawDistance)
      return rawDistance.compareTo(arg0.rawDistance);
    else return distanceValue - arg0.distanceValue;
  }
  
  /**
   * Helper method to compute the raw distance value.  Do not use until 
   * hospitalLat and hospitalLon have been set.
   * 
   * @param ambLat
   * @param ambLon
   */
  public void computeRawDistance(double ambLat, double ambLon) {
    double xAxis = Math.abs(hospitalLat - ambLat);
    double yAxis = Math.abs(hospitalLon - ambLon);
    
    //a^2 + b^2 = c^2
    rawDistance = Math.sqrt((xAxis * xAxis) + (yAxis * yAxis));
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
   * @return the hospitalName
   */
  public String getHospitalName() {
    return hospitalName;
  }

  /**
   * @param hospitalName the hospitalName to set
   */
  public void setHospitalName(String hospitalName) {
    this.hospitalName = hospitalName;
  }

  /**
   * @return the hospitalLat
   */
  public double getHospitalLat() {
    return hospitalLat;
  }

  /**
   * @param hospitalLat the hospitalLat to set
   */
  public void setHospitalLat(double hospitalLat) {
    this.hospitalLat = hospitalLat;
  }

  /**
   * @return the hospitalLon
   */
  public double getHospitalLon() {
    return hospitalLon;
  }

  /**
   * @param hospitalLon the hospitalLon to set
   */
  public void setHospitalLon(double hospitalLon) {
    this.hospitalLon = hospitalLon;
  }

  /**
   * @return the hospitalAddress
   */
  public String getHospitalAddress() {
    return hospitalAddress;
  }

  /**
   * @param hospitalAddress the hospitalAddress to set
   */
  public void setHospitalAddress(String hospitalAddress) {
    this.hospitalAddress = hospitalAddress;
  }

  /**
   * @return the rawDistance
   */
  public double getRawDistance() {
    return rawDistance;
  }

  /**
   * @param rawDistance the rawDistance to set
   */
  public void setRawDistance(double rawDistance) {
    this.rawDistance = rawDistance;
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
