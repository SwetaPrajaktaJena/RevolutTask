package com.revolut.task.dao.impl;

import com.revolut.task.dao.AccountDAO;
import com.revolut.task.dao.DAOFactory;
import com.revolut.task.dao.UserDAO;
import com.revolut.task.model.PersistData;
import com.revolut.task.utils.Utils;

import org.apache.commons.dbutils.DbUtils;
import org.apache.log4j.Logger;
import org.h2.tools.RunScript;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ImplDAOFactory  extends DAOFactory{
	private static Logger log = Logger.getLogger(ImplDAOFactory.class);

	private final UserDAOImpl userDAO = new UserDAOImpl();
	private final AccountDAOImpl accountDAO = new AccountDAOImpl();
	
	
	public UserDAO getUserDAO() {
		return userDAO;
	}

	public AccountDAO getAccountDAO() {
		return accountDAO;
	}
@Override
	public void populateTestData() {
		log.info("Populating Test User And Account and data  ");
		PersistData st = PersistData.getInstance();
	}

}
