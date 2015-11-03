package com.ac.ems.rest.data;

import java.util.List;

import com.ac.ems.rest.message.SimpleMessageData;

/**
 * @author ac010168
 *
 */
public class GenericListSuccessData extends SimpleMessageData {
  
  private List<?> resultList;
  
  public GenericListSuccessData() {
    super("Request Successful", "Successfully retrieved the list.");
    resultList = null;
  }

  /**
   * @return the resultList
   */
  public List<?> getResultList() {
    return resultList;
  }

  /**
   * @param resultList the resultList to set
   */
  public void setResultList(List<?> resultList) {
    this.resultList = resultList;
  }
}
