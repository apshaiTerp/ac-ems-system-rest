package com.ac.ems.rest.data;

import com.ac.ems.data.EMSProvider;

/**
 * @author ac010168
 *
 */
public class EMSProviderDistanceSortable implements Comparable<EMSProviderDistanceSortable> {

  /** Reference to the EMSProvider data */
  private EMSProvider provider;
  /** The distance value as computed by Pythagorean theorem */
  private Double      distance;
  
  public EMSProviderDistanceSortable(EMSProvider provider, double incidentLat, double incidentLon) {
    this.provider = provider;
    
    double xAxis = Math.abs(provider.getProviderLat() - incidentLat);
    double yAxis = Math.abs(provider.getProviderLon() - incidentLon);
    
    //a^2 + b^2 = c^2
    distance = Math.sqrt((xAxis * xAxis) + (yAxis * yAxis));
  }
  
  /*
   * (non-Javadoc)
   * @see java.lang.Comparable#compareTo(java.lang.Object)
   */
  @Override
  public int compareTo(EMSProviderDistanceSortable o) {
    return distance.compareTo(o.distance);
  }

  /**
   * @return the provider
   */
  public EMSProvider getProvider() {
    return provider;
  }

  /**
   * @param provider the provider to set
   */
  public void setProvider(EMSProvider provider) {
    this.provider = provider;
  }

  /**
   * @return the distance
   */
  public Double getDistance() {
    return distance;
  }

  /**
   * @param distance the distance to set
   */
  public void setDistance(Double distance) {
    this.distance = distance;
  }

}
