package com.revolut.task.dao.impl;

import com.revolut.task.dao.UserDAO;
import com.revolut.task.exception.CustomException;
import com.revolut.task.model.Account;
import com.revolut.task.model.PersistData;
import com.revolut.task.model.User;

import org.apache.commons.dbutils.DbUtils;
import org.apache.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityTransaction;


public class UserDAOImpl implements UserDAO {
	
    private static Logger log = Logger.getLogger(UserDAOImpl.class);
    PersistData st = PersistData.getInstance();
    /**
     * Find all users
     */
    public List<User> getAllUsers() throws CustomException {
    	List<User> allUsers = st.entityManager.createQuery("SELECT p FROM User p").getResultList();//(Account.class, accountId);
		return allUsers;
    }
    
    /**
     * Find user by userId
     */
    public int getUserById(long userId) throws CustomException {
    	User fetchedUser = st.entityManager.find(User.class, userId);
    	if(fetchedUser == null) return 0;
		return 1;
    }
    
    /**
     * Find user by userName
     */
    public User getUserByName(String userName) throws CustomException {
    	User user;
    	try{
    		user = (User) st.entityManager.createQuery("SELECT p FROM User p where userName='"+userName+"'").getSingleResult();//(Account.class, accountId);
    	}catch(Exception e){
    		return null;
    	}
		return user;
    }
    
    /**
     * Save User
     */
    public long insertUser(User user) throws CustomException {
    	User fetchedUser = st.entityManager.find(User.class, user.getUserId());
    	if(fetchedUser == null){
    		EntityTransaction transaction = st.entityManager.getTransaction();
	    	if(!transaction.isActive()) transaction.begin();
			User newUser = new User(user.getUserId(),user.getUserName(), user.getemail());
			st.entityManager.persist(user);
			transaction.commit();	
			return 1;
    	}else{
    		return 0;
    	}
        }
    
    /**
     * Update User
     */
    public int updateUser(Long userId,User user) throws CustomException {
    	User fetchedUser = st.entityManager.find(User.class, userId);
		if(null == fetchedUser) {
			return 0;
		} else {
			EntityTransaction transaction = st.entityManager.getTransaction();
			try{
				if(!transaction.isActive()) transaction.begin();
				fetchedUser.setemail(user.getemail());
				fetchedUser.setUserName(user.getUserName());
				
				st.entityManager.persist(fetchedUser);
				transaction.commit();
				return 1;
			}catch(Exception e){
				return 0;
			}
		}
    }
    
    
    /**
     * Delete User
     */
    public int deleteUser(long userId) throws CustomException {
    	User fetchedUser = st.entityManager.find(User.class, userId);
    	if(fetchedUser!=null){
    		EntityTransaction transaction = st.entityManager.getTransaction();
			if(!transaction.isActive()) {
				transaction.begin();
			}
			fetchedUser = st.entityManager.find(User.class, userId);
			st.entityManager.remove(fetchedUser);
			transaction.commit();
			return 1;
    		
    	}else{
    		return 0;
    	}
    	
    }

}
