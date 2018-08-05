package com.revolut.task.dao;

public abstract class DAOFactory {
    public abstract UserDAO getUserDAO();
   
	public abstract AccountDAO getAccountDAO();

	public abstract void populateTestData();
	

}
