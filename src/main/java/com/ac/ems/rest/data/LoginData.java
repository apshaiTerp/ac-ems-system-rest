package com.ac.ems.rest.data;

import java.io.IOException;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Container for Login fields so they can be submitted via POST.
 * 
 * @author ac010168
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class LoginData {
  
  private String userName;
  private String password;
  private String userRole;
  private long   authorizeID;  //This value is optional
  
  public LoginData() {
    userName    = null;
    password    = null;
    userRole    = null;
    authorizeID = 0L;
  }

  public LoginData(String jsonString) {
    super();
    ObjectMapper mapper = new ObjectMapper();
    try {
      LoginData jsonData = mapper.readValue(jsonString, LoginData.class);
      userName    = jsonData.userName;
      password    = jsonData.password;
      userRole    = jsonData.userRole;
      authorizeID = jsonData.authorizeID;
    } catch (JsonParseException jpe) {
      jpe.printStackTrace();
    } catch (JsonMappingException jme) {
      jme.printStackTrace();
    } catch (IOException ioe) {
      ioe.printStackTrace();
    }
  }
  
  /**
   * @return the userName
   */
  public String getUserName() {
    return userName;
  }

  /**
   * @param userName the userName to set
   */
  public void setUserName(String userName) {
    this.userName = userName;
  }

  /**
   * @return the password
   */
  public String getPassword() {
    return password;
  }

  /**
   * @param password the password to set
   */
  public void setPassword(String password) {
    this.password = password;
  }

  /**
   * @return the userRole
   */
  public String getUserRole() {
    return userRole;
  }

  /**
   * @param userRole the userRole to set
   */
  public void setUserRole(String userRole) {
    this.userRole = userRole;
  }

  /**
   * @return the authorizeID
   */
  public long getAuthorizeID() {
    return authorizeID;
  }

  /**
   * @param authorizeID the authorizeID to set
   */
  public void setAuthorizeID(long authorizeID) {
    this.authorizeID = authorizeID;
  }

}
