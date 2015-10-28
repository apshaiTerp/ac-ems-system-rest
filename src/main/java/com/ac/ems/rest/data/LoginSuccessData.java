package com.ac.ems.rest.data;

import com.ac.ems.rest.message.SimpleMessageData;

/**
 * @author ac010168
 *
 */
public class LoginSuccessData extends SimpleMessageData {

  private String userName;
  private long   userID;
  private String userRole;
  
  public LoginSuccessData() {
    super("Login Successful", "The Login Validated Successfully.");
    userName = null;
    userID   = 0;
    userRole = null;
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
   * @return the userID
   */
  public long getUserID() {
    return userID;
  }

  /**
   * @param userID the userID to set
   */
  public void setUserID(long userID) {
    this.userID = userID;
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
}
