package com.revolut.task.dao;

import com.revolut.task.exception.CustomException;
import com.revolut.task.model.User;

import java.util.List;

public interface UserDAO {
	
	List<User> getAllUsers() throws CustomException;

	int getUserById(long userId) throws CustomException;

	User getUserByName(String userName) throws CustomException;

	long insertUser(User user) throws CustomException;

	int updateUser(Long userId, User user) throws CustomException;

	int deleteUser(long userId) throws CustomException;

}
