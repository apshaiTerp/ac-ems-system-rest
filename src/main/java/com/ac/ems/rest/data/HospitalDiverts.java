package com.ac.ems.rest.data;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author ac010168
 *
 */
public class HospitalDiverts {

  /** The current ER Divert state */
  private String   erDivert;
  /** The current Trauma Divert state */
  private String   traumaDivert;
  /** The current Burn Divert state */
  private String   burnDivert;
  /** The current STEMI Divert state */
  private String   stemiDivert;
  /** The current Stroke Divert state */
  private String   strokeDivert;

  public HospitalDiverts() {
    erDivert           = null;
    traumaDivert       = null;
    burnDivert         = null;
    stemiDivert        = null;
    strokeDivert       = null;
  }
  
  public HospitalDiverts(String jsonString) {
    super();
    ObjectMapper mapper = new ObjectMapper();
    try {
      HospitalDiverts jsonData = mapper.readValue(jsonString, HospitalDiverts.class);
      erDivert           = jsonData.erDivert;
      traumaDivert       = jsonData.traumaDivert;
      burnDivert         = jsonData.burnDivert;
      stemiDivert        = jsonData.stemiDivert;
      strokeDivert       = jsonData.strokeDivert;
    } catch (JsonParseException jpe) {
      jpe.printStackTrace();
    } catch (JsonMappingException jme) {
      jme.printStackTrace();
    } catch (IOException ioe) {
      ioe.printStackTrace();
    }
  }

  /**
   * @return the erDivert
   */
  public String getErDivert() {
    return erDivert;
  }

  /**
   * @param erDivert the erDivert to set
   */
  public void setErDivert(String erDivert) {
    this.erDivert = erDivert;
  }

  /**
   * @return the traumaDivert
   */
  public String getTraumaDivert() {
    return traumaDivert;
  }

  /**
   * @param traumaDivert the traumaDivert to set
   */
  public void setTraumaDivert(String traumaDivert) {
    this.traumaDivert = traumaDivert;
  }

  /**
   * @return the burnDivert
   */
  public String getBurnDivert() {
    return burnDivert;
  }

  /**
   * @param burnDivert the burnDivert to set
   */
  public void setBurnDivert(String burnDivert) {
    this.burnDivert = burnDivert;
  }

  /**
   * @return the stemiDivert
   */
  public String getStemiDivert() {
    return stemiDivert;
  }

  /**
   * @param stemiDivert the stemiDivert to set
   */
  public void setStemiDivert(String stemiDivert) {
    this.stemiDivert = stemiDivert;
  }

  /**
   * @return the strokeDivert
   */
  public String getStrokeDivert() {
    return strokeDivert;
  }

  /**
   * @param strokeDivert the strokeDivert to set
   */
  public void setStrokeDivert(String strokeDivert) {
    this.strokeDivert = strokeDivert;
  }
}
