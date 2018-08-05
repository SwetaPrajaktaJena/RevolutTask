package com.revolut.task.test.dao;

import com.revolut.task.dao.DAOFactory;
import com.revolut.task.dao.impl.ImplDAOFactory;
import com.revolut.task.exception.CustomException;
import com.revolut.task.model.User;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

import static junit.framework.TestCase.assertTrue;

public class TestUserDAO {
	
	private static Logger log = Logger.getLogger(TestUserDAO.class);
	
	private static final DAOFactory factoryOBJ=new ImplDAOFactory();

	@BeforeClass
	public static void setup() {
		
		
	}

	@After
	public void tearDown() {

	}

	@Test
	public void testGetAllUsers() throws CustomException {
		List<User> allUsers = factoryOBJ.getUserDAO().getAllUsers();
		assertTrue(allUsers.size() > 1);
	}

	@Test
	public void testGetUserById() throws CustomException {
		int u = factoryOBJ.getUserDAO().getUserById(1L);
		assertTrue(u == 1);
	}

	@Test
	public void testGetNonExistingUserById() throws CustomException {
		int u = factoryOBJ.getUserDAO().getUserById(10);
		assertTrue(u == 0);
	}
	
	@Test
	public void testGetNonExistingUserByName() throws CustomException {
		User u = factoryOBJ.getUserDAO().getUserByName("abcdeftg");
		assertTrue(u == null);
	}
	
	@Test
	public void testDeleteNonExistingUser() throws CustomException {
		int rowCount = factoryOBJ.getUserDAO().deleteUser(10L);
		// assert no row(user) deleted
		assertTrue(rowCount == 0);

	}
	
	
	@Test
	public void testUpdateNonExistingUser() throws CustomException {
		User u = new User(10, "test2", "test2@gmail.com");
		int rowCount = factoryOBJ.getUserDAO().updateUser(10L, u);
		// assert one row(user) updated
		assertTrue(rowCount == 0);
	}
	
	
	
	@Test
	public void testCreateUser() throws CustomException {
		User u = new User(5L,"liandre", "liandre@gmail.com");
		//long id = h2DaoFactory.getUserDAO().insertUser(u);
		//int uAfterInsert = factoryOBJ.getUserDAO().getUserById(u.getUserId());
		long uAfterInsert = factoryOBJ.getUserDAO().insertUser(u);
		assertTrue(uAfterInsert == 1);
	}

	@Test
	public void testUpdateUser() throws CustomException {
		User u = new User(1, "test2", "test2@gmail.com");
		int rowCount = factoryOBJ.getUserDAO().updateUser(1L,u);
		// assert one row(user) updated
		assertTrue(rowCount == 1);
		//assertTrue(h2DaoFactory.getUserDAO().getUserById(1L).getemail().equals("yanglu@gmail.com"));
	}
	

	@Test
	public void testDeleteUser() throws CustomException {
		int rowCount = factoryOBJ.getUserDAO().deleteUser(3L);
	
		assertTrue(rowCount == 1);
		// assert user no longer there
		//assertTrue(h2DaoFactory.getUserDAO().getUserById(1L) == null);
	}

	

}
