package com.ac.ems.rest.data;

import java.util.List;

import com.ac.ems.rest.message.SimpleMessageData;

/**
 * @author ac010168
 *
 */
public class ListSuccessData extends SimpleMessageData {

  private List<String> resultList;
  
  public ListSuccessData() {
    super("Request Successful", "Successfully retrieved the list.");
    resultList = null;
  }

  /**
   * @return the resultList
   */
  public List<String> getResultList() {
    return resultList;
  }

  /**
   * @param resultList the resultList to set
   */
  public void setResultList(List<String> resultList) {
    this.resultList = resultList;
  }
}
