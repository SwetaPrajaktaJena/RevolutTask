package com.revolut.task.dao.impl;


import javax.persistence.EntityTransaction;

import com.revolut.task.dao.AccountDAO;

import com.revolut.task.exception.CustomException;
import com.revolut.task.model.Account;
import com.revolut.task.model.PersistData;
import com.revolut.task.model.UserTransaction;
import org.apache.log4j.Logger;
import java.math.BigDecimal;
import java.util.*;

public class AccountDAOImpl implements AccountDAO {

	private static Logger log = Logger.getLogger(AccountDAOImpl.class);
	
	PersistData st = PersistData.getInstance();
	
	/**
	 * Get all accounts.
	 */
	public List<Account> getAllAccounts() throws CustomException {
		List<Account> allAccounts = st.entityManager.createQuery("SELECT p FROM Account p").getResultList();
		return allAccounts;
	}
	
	/**
	 * Get account by id
	 */
	public Account getAccountById(long accountId) throws CustomException {
		Account fetchedAccount = st.entityManager.find(Account.class, accountId);
		return fetchedAccount;

	}
	
	/**
	 * Create account
	 */
	public long createAccount(Account newAccount) throws CustomException {
		Account fetchedAccount = st.entityManager.find(Account.class, newAccount.getAccountId());
		if(fetchedAccount==null){
			EntityTransaction transaction = st.entityManager.getTransaction();
			if(!transaction.isActive()) transaction.begin();
			st.entityManager.persist(newAccount);
			transaction.commit();
			return 1;
		}else{
			return 0;
		}
	}
	
	/**
	 * Delete account by id
	 */
	public Account deleteAccountById(long accountId) throws CustomException {
		Account fetchedAccount = st.entityManager.find(Account.class, accountId);
		if(fetchedAccount!=null){
			EntityTransaction transaction = st.entityManager.getTransaction();
			transaction.begin();
			st.entityManager.remove(fetchedAccount);
			transaction.commit();
			return fetchedAccount;
		}else{
			return null;
		}
		
	}
	
	/**
	 * Update account balance
	 */
	public Account updateAccountBalance(long accountId, BigDecimal amount) throws CustomException {
		Account fetchedAccount = st.entityManager.find(Account.class, accountId);
		if(null == fetchedAccount) {
			throw new IllegalArgumentException("Account not found");
		} else if(fetchedAccount.getBalance().add(amount).signum()< 0){
			throw new CustomException("Not sufficient Fund", new Exception());
		}else{
			try{
				EntityTransaction transaction = st.entityManager.getTransaction();
				if(!transaction.isActive()) transaction.begin();
				fetchedAccount.setBalance(fetchedAccount.getBalance().add(amount));
				st.entityManager.persist(fetchedAccount);
				transaction.commit();
				return fetchedAccount;
			}catch(Exception e){
				return null;
			}
		}
	}
	/**
	 * Transfer balance between two accounts.
	 */
	

	public synchronized int transferAccountBalance(UserTransaction userTransaction) throws CustomException {
		int result = -1;
		EntityTransaction transaction = st.entityManager.getTransaction();
		
		if(!transaction.isActive()) transaction.begin();
			Account fromAccount = st.entityManager.find(Account.class, userTransaction.getFromAccountId());
			if(fromAccount == null)
				throw new IllegalArgumentException("Source account does not exist");
			fromAccount.setBalance(fromAccount.getBalance().subtract(userTransaction.getAmount()));
			st.entityManager.persist(fromAccount);
			log.debug("Account From: " + fromAccount+" transaction "+userTransaction.getAmount());
			
			Account toAccount = st.entityManager.find(Account.class, userTransaction.getToAccountId());
			if(toAccount == null)
				throw new IllegalArgumentException("Source account does not exist");
			
			toAccount.setBalance(toAccount.getBalance().add(userTransaction.getAmount()));
			
			st.entityManager.persist(toAccount);
			log.debug("Account To: " + toAccount+" transaction "+userTransaction.getAmount());
			
			String retval = "Account number "+fromAccount.getAccountId() +" debited, New balance is "+fromAccount.getBalance() +" Account number "+ toAccount.getAccountId() +" credited, New balance is "+toAccount.getBalance();
			transaction.commit();
			return 1;
		
	}
	
	
}
