package com.ac.ems.rest.data;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author ac010168
 *
 */
public class SMSMessageData {
  
  private String textMessage;
  private String contactPhone;
  
  public SMSMessageData() {
    textMessage  = null;
    contactPhone = null;
  }
  
  public SMSMessageData(String jsonString) {
    super();
    ObjectMapper mapper = new ObjectMapper();
    try {
      SMSMessageData jsonData = mapper.readValue(jsonString, SMSMessageData.class);
      textMessage  = jsonData.textMessage;
      contactPhone = jsonData.contactPhone;
    } catch (JsonParseException jpe) {
      jpe.printStackTrace();
    } catch (JsonMappingException jme) {
      jme.printStackTrace();
    } catch (IOException ioe) {
      ioe.printStackTrace();
    }
  }
  /**
   * @return the textMessage
   */
  public String getTextMessage() {
    return textMessage;
  }
  
  /**
   * @param textMessage the textMessage to set
   */
  public void setTextMessage(String textMessage) {
    this.textMessage = textMessage;
  }
  
  /**
   * @return the contactPhone
   */
  public String getContactPhone() {
    return contactPhone;
  }
  
  /**
   * @param contactPhone the contactPhone to set
   */
  public void setContactPhone(String contactPhone) {
    this.contactPhone = contactPhone;
  }
}
