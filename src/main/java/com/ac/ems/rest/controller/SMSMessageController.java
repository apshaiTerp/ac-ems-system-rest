package com.ac.ems.rest.controller;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ac.ems.rest.data.SMSMessageData;
import com.ac.ems.rest.message.SimpleErrorData;
import com.ac.ems.rest.message.SimpleMessageData;
import com.twilio.sdk.TwilioRestClient;
import com.twilio.sdk.resource.factory.MessageFactory;
import com.twilio.sdk.resource.instance.Message;

/**
 * @author ac010168
 *
 */
@RestController
@RequestMapping("/message/sms")
public class SMSMessageController {
  
  // Find your Account Sid and Token at twilio.com/user/account
  public static final String ACCOUNT_SID = "AC3f4b876396776cc6e53e89f0c0645026";
  public static final String AUTH_TOKEN  = "6fbfe1074239e246136b4201c1fa8e0c";

  @RequestMapping(method = RequestMethod.POST, consumes = "application/json;charset=UTF-8", produces="application/json;charset=UTF-8")
  public Object postSMSMessage(@RequestBody SMSMessageData messageData) {
    if (messageData == null)
      return new SimpleErrorData("SMS Message Error", "There was no valid message data provided");
    if (messageData.getTextMessage() == null)
      return new SimpleErrorData("SMS Message Error", "There was no valid message text provided");
    if (messageData.getContactPhone() == null)
      return new SimpleErrorData("SMS Message Error", "There was no valid contact phone number provided");
    
    String updatedPhoneNumber = messageData.getContactPhone();
    if (updatedPhoneNumber.startsWith("1"))
      updatedPhoneNumber = "+" + updatedPhoneNumber;
    else updatedPhoneNumber = "+1" + updatedPhoneNumber;
    
    TwilioRestClient client = new TwilioRestClient(ACCOUNT_SID, AUTH_TOKEN);
    
    // Build a filter for the MessageList
    List<NameValuePair> params = new ArrayList<NameValuePair>();
    params.add(new BasicNameValuePair("Body", messageData.getTextMessage()));
    params.add(new BasicNameValuePair("To",   updatedPhoneNumber));
    params.add(new BasicNameValuePair("From", "+18162375526"));
 
    Object result = null;
    try {
      MessageFactory messageFactory = client.getAccount().getMessageFactory();
      Message message = messageFactory.create(params);
      System.out.println(message.getSid());

      SimpleMessageData data = new SimpleMessageData();
      data.setMessage("Success");
      data.setMessageType("I think my SMS Message was sent correctly.  SID: " + message.getSid());
      result = data;
    } catch (Throwable t) {
      t.printStackTrace();
      SimpleErrorData data = new SimpleErrorData();
      data.setErrorMessage("SMS Message no send");
      data.setErrorType("SMS No Work!");
      result = data;
    }
    
    //TODO
    return result;
  }

}
