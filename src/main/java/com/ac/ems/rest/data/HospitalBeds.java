package com.ac.ems.rest.data;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author ac010168
 *
 */
public class HospitalBeds {

  /** The available trauma beds */
  private int      traumaBedsFree;
  /** The number of occupied trauma beds */
  private int      traumaBedsOccupied;
  /** The number of beds transitioning to cleanup */
  private int      traumaBedsCleanup;
  /** The number of the ER beds currently available */
  private int      erBedsFree;
  /** The number of the ER beds currently occupied */
  private int      erBedsOccupied;
  /** The number of the ER beds transitioning to cleanup */
  private int      erBedsCleanup;
  
  public HospitalBeds() {
    traumaBedsFree     = -1;
    traumaBedsOccupied = -1;
    traumaBedsCleanup  = -1;
    erBedsFree         = -1;
    erBedsOccupied     = -1;
    erBedsCleanup      = -1;
  }
  
  /**
   * Advanced Constructor designed to construct the object from a JSON record
   * 
   * @param jsonString
   */
  public HospitalBeds(String jsonString) {
    super();
    ObjectMapper mapper = new ObjectMapper();
    try {
      HospitalBeds jsonData = mapper.readValue(jsonString, HospitalBeds.class);
      traumaBedsFree     = jsonData.traumaBedsFree;
      traumaBedsOccupied = jsonData.traumaBedsOccupied;
      traumaBedsCleanup  = jsonData.traumaBedsCleanup;
      erBedsFree         = jsonData.erBedsFree;
      erBedsOccupied     = jsonData.erBedsOccupied;
      erBedsCleanup      = jsonData.erBedsOccupied;
    } catch (JsonParseException jpe) {
      jpe.printStackTrace();
    } catch (JsonMappingException jme) {
      jme.printStackTrace();
    } catch (IOException ioe) {
      ioe.printStackTrace();
    }
  }

  /**
   * @return the traumaBedsFree
   */
  public int getTraumaBedsFree() {
    return traumaBedsFree;
  }
  /**
   * @param traumaBedsFree the traumaBedsFree to set
   */
  public void setTraumaBedsFree(int traumaBedsFree) {
    this.traumaBedsFree = traumaBedsFree;
  }
  /**
   * @return the traumaBedsOccupied
   */
  public int getTraumaBedsOccupied() {
    return traumaBedsOccupied;
  }
  /**
   * @param traumaBedsOccupied the traumaBedsOccupied to set
   */
  public void setTraumaBedsOccupied(int traumaBedsOccupied) {
    this.traumaBedsOccupied = traumaBedsOccupied;
  }
  /**
   * @return the traumaBedsCleanup
   */
  public int getTraumaBedsCleanup() {
    return traumaBedsCleanup;
  }
  /**
   * @param traumaBedsCleanup the traumaBedsCleanup to set
   */
  public void setTraumaBedsCleanup(int traumaBedsCleanup) {
    this.traumaBedsCleanup = traumaBedsCleanup;
  }
  /**
   * @return the erBedsFree
   */
  public int getErBedsFree() {
    return erBedsFree;
  }
  /**
   * @param erBedsFree the erBedsFree to set
   */
  public void setErBedsFree(int erBedsFree) {
    this.erBedsFree = erBedsFree;
  }
  /**
   * @return the erBedsOccupied
   */
  public int getErBedsOccupied() {
    return erBedsOccupied;
  }
  /**
   * @param erBedsOccupied the erBedsOccupied to set
   */
  public void setErBedsOccupied(int erBedsOccupied) {
    this.erBedsOccupied = erBedsOccupied;
  }
  /**
   * @return the erBedsCleanup
   */
  public int getErBedsCleanup() {
    return erBedsCleanup;
  }
  /**
   * @param erBedsCleanup the erBedsCleanup to set
   */
  public void setErBedsCleanup(int erBedsCleanup) {
    this.erBedsCleanup = erBedsCleanup;
  }
  
}
