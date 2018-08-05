package com.revolut.task.model;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
//Singleton Class 
public class PersistData {
	 private static PersistData myObj;
	 /**Adding src/main/resources/META-INF/SQL/Testdata.sql file data To EntityManager Objects through
	  * Herbernate mapping in src/main/resources/META-INF/persistence.xml file
	   */
		public final EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("h2");
		public final EntityManager entityManager = entityManagerFactory.createEntityManager();
		EntityTransaction transaction = entityManager.getTransaction();
	    /**
	     * Create private constructor
	     */
	    private PersistData(){
	         
	    }
	    /**
	     * Create a static method to get instance.
	     */
	    public static PersistData getInstance(){
	        if(myObj == null){
	            myObj = new PersistData();
	        }
	        return myObj;
	    }
	     
}
