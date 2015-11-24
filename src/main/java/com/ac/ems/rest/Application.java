package com.ac.ems.rest;

import javax.annotation.PreDestroy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;

import com.ac.ems.db.EMSDatabase;
import com.ac.ems.db.MongoDBFactory;
import com.ac.ems.db.exception.ConfigurationException;

/**
 * @author ac010168
 *
 */
@ComponentScan
@EnableAutoConfiguration
public class Application extends SpringBootServletInitializer {

  /** The Host URL for our Mongo Instance */
  //public static String databaseHost = "192.168.1.9";
  public static String databaseHost = "localhost";
  /** The Host Port for our Mongo Instance */
  public static int    databasePort = 27017;
  /** The Host Database Name for our Mongo Database */
  public static String databaseName = "emsdb";
  
  /** Public global reference to our Database Connection */
  public static EMSDatabase database;
  
  @Override
  protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
    return application.sources(Application.class);
  }

  /** Main method, which is starting point for service using Spring launcher */
  public static void main(String[] args) {
    try {
      database = MongoDBFactory.createMongoDatabase(databaseHost, databasePort, databaseName);
      database.initializeDBConnection();
    } catch (ConfigurationException ce) {
      ce.printStackTrace();
      System.out.println ("Shutting down system!");
      System.exit(1);
    }
    
    SpringApplication.run(Application.class, args);
  }
  
  @PreDestroy
  public static void shutdownHook() {
    try {
      database.closeDBConnection();
    } catch (ConfigurationException ce) {
      ce.printStackTrace();
    }
    System.out.println (">>> I'm inside the shutdownHook <<<");
  }
}
