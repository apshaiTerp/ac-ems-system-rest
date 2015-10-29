package com.ac.ems.rest.controller;

import java.util.Date;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ac.ems.data.Hospital;
import com.ac.ems.data.HospitalDiversionHistory;
import com.ac.ems.db.EMSDatabase;
import com.ac.ems.db.MongoDBFactory;
import com.ac.ems.db.exception.ConfigurationException;
import com.ac.ems.db.exception.DatabaseOperationException;
import com.ac.ems.rest.Application;
import com.ac.ems.rest.data.HospitalDivertActionData;
import com.ac.ems.rest.data.HospitalDiverts;
import com.ac.ems.rest.message.SimpleErrorData;

/**
 * @author ac010168
 *
 */
@RestController
@RequestMapping("/hospital/divert")
public class HospitalDivertController {

  @RequestMapping(method = RequestMethod.POST, consumes = "application/json;charset=UTF-8", produces="application/json;charset=UTF-8")
  public Object changeDivertStatus(@RequestBody HospitalDivertActionData actionData) {
    if (actionData == null)
      return new SimpleErrorData("Request Error", "There was no valid divert data provided");
    if (actionData.getHospitalID() < 0)
      return new SimpleErrorData("Request Error", "The provided hospitalID was not valid");
    if (actionData.getHospitalID() < 0)
      return new SimpleErrorData("Request Error", "The provided userID was not valid");
    if (actionData.getActionType() == null)
      return new SimpleErrorData("Request Error", "The provided actionType was not valid");
    if (actionData.getDivertState() == null)
      return new SimpleErrorData("Request Error", "The provided divert state was not valid");
    if ((!actionData.getActionType().equalsIgnoreCase("er")) && (!actionData.getActionType().equalsIgnoreCase("trauma")) &&
        (!actionData.getActionType().equalsIgnoreCase("burn")) && (!actionData.getActionType().equalsIgnoreCase("stroke")) &&
        (!actionData.getActionType().equalsIgnoreCase("stemi")))
      return new SimpleErrorData("Request Error", "The actionType value provided was invalid.");
    if ((!actionData.getDivertState().equalsIgnoreCase("open")) && (!actionData.getDivertState().equalsIgnoreCase("divert")))
      return new SimpleErrorData("Request Error", "The divertState value provided was invalid.");
    
    //Create all the database stuff
    EMSDatabase database = null;
    
    try {
      if (Application.database == null)
        Application.database = MongoDBFactory.createMongoDatabase(Application.databaseHost, Application.databasePort, Application.databaseName);
      database = Application.database;
      database.initializeDBConnection();

      Hospital hospital = (Hospital)database.querySingleRow(EMSDatabase.HOSPITAL_TABLE_NAME, "hospitalID", actionData.getHospitalID());
      if (hospital == null)
        return new SimpleErrorData("No Results Found", "The provided hospitalID could not be found.");

      //TODO - Make the updates here
      if (actionData.getActionType().equalsIgnoreCase("er")) {
        if (hospital.getErDivert().equalsIgnoreCase("na"))
          return new SimpleErrorData("Diversion Eror", "There hospital does not support this diversion type");
        hospital.setErDivert(actionData.getDivertState());
      } else if (actionData.getActionType().equalsIgnoreCase("trauma")) {
        if (hospital.getTraumaDivert().equalsIgnoreCase("na"))
          return new SimpleErrorData("Diversion Eror", "There hospital does not support this diversion type");
        hospital.setTraumaDivert(actionData.getDivertState());
      } else if (actionData.getActionType().equalsIgnoreCase("burn")) {
        if (hospital.getBurnDivert().equalsIgnoreCase("na"))
          return new SimpleErrorData("Diversion Eror", "There hospital does not support this diversion type");
        hospital.setBurnDivert(actionData.getDivertState());
      } else if (actionData.getActionType().equalsIgnoreCase("stroke")) {
        if (hospital.getStrokeDivert().equalsIgnoreCase("na"))
          return new SimpleErrorData("Diversion Eror", "There hospital does not support this diversion type");
        hospital.setStrokeDivert(actionData.getDivertState());
      } else if (actionData.getActionType().equalsIgnoreCase("stemi")) {
        if (hospital.getStemiDivert().equalsIgnoreCase("na"))
          return new SimpleErrorData("Diversion Eror", "There hospital does not support this diversion type");
        hospital.setStemiDivert(actionData.getDivertState());
      }
      
      //Push the change back into the database
      database.updateHospitalData(hospital);
      
      //Create the new Divert History Row
      HospitalDiversionHistory history = new HospitalDiversionHistory();
      history.setDiversionID(database.getGenericMaxID(EMSDatabase.HOSPITAL_DIVERSION_HISTORY_TABLE_NAME, "diversionID") + 1);
      history.setHospitalID(hospital.getHospitalID());
      history.setDivertCategory(actionData.getActionType());
      history.setCurState(actionData.getDivertState());
      history.setChangedByUserID(actionData.getUserID());
      history.setChangedOnDate(new Date());
      
      //Write the new Divert History Row
      database.insertHospitalDiversionHistoryData(history);
      
      HospitalDiverts diverts = new HospitalDiverts();
      diverts.setErDivert(hospital.getErDivert());
      diverts.setTraumaDivert(hospital.getTraumaDivert());
      diverts.setBurnDivert(hospital.getBurnDivert());
      diverts.setStemiDivert(hospital.getStemiDivert());
      diverts.setStrokeDivert(hospital.getStrokeDivert());

      return diverts;
    } catch (DatabaseOperationException doe) {
      doe.printStackTrace();
      return new SimpleErrorData("Database Operation Error", "An error occurred running the request: " + doe.getMessage());
    } catch (ConfigurationException ce) {
      ce.printStackTrace();
      return new SimpleErrorData("Database Configuration Error", "An error occurred accessing the database: " + ce.getMessage());
    } 
  }
}
