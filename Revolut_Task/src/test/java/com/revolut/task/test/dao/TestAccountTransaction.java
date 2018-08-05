package com.revolut.task.test.dao;

import com.revolut.task.dao.AccountDAO;
import com.revolut.task.dao.DAOFactory;
import com.revolut.task.dao.impl.ImplDAOFactory;
import com.revolut.task.exception.CustomException;
import com.revolut.task.model.Account;
import com.revolut.task.model.UserTransaction;

import org.apache.commons.dbutils.DbUtils;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.CountDownLatch;

import static junit.framework.TestCase.assertTrue;

public class TestAccountTransaction {

	private static Logger log = Logger.getLogger(TestAccountDAO.class);
	private static final DAOFactory factoryOBJ=new ImplDAOFactory();
	private static final int THREADS_COUNT = 50;

	@BeforeClass
	public static void setup() {
		factoryOBJ.populateTestData();
	}

	@After
	public void tearDown() {

	}

	@Test
	public void testAccountSingleThreadSameCcyTransfer() throws CustomException {

		final AccountDAO accountDAO =factoryOBJ.getAccountDAO();

		BigDecimal transferAmount = new BigDecimal(50).setScale(4, RoundingMode.HALF_EVEN);

		UserTransaction transaction = new UserTransaction("INR", transferAmount, 5L, 6L);

		long startTime = System.currentTimeMillis();

		accountDAO.transferAccountBalance(transaction);
		long endTime = System.currentTimeMillis();

		log.info("TransferAccountBalance finished, time taken: " + (endTime - startTime) + "ms");

		Account accountFrom = accountDAO.getAccountById(5);

		Account accountTo = accountDAO.getAccountById(6);

		log.debug("Account From: " + accountFrom);

		log.debug("Account From: " + accountTo);

		assertTrue(
				accountFrom.getBalance().compareTo(new BigDecimal(250).setScale(4, RoundingMode.HALF_EVEN)) == 0);
		assertTrue(accountTo.getBalance().equals(new BigDecimal(550).setScale(4, RoundingMode.HALF_EVEN)));

	}

	@Test
	public void testAccountMultiThreadedTransfer() throws InterruptedException, CustomException {
		final AccountDAO accountDAO =factoryOBJ.getAccountDAO();
		// transfer a total of 100 USD from AccountId=1 balance in multi-threaded where threadCount=50,
		// mode, expect half of the transaction fail
		final CountDownLatch latch = new CountDownLatch(THREADS_COUNT);
		for (int i = 0; i < THREADS_COUNT; i++) {
			new Thread(new Runnable() {

				@Override
				public void run() {
					try {
						UserTransaction transaction = new UserTransaction("USD",
								new BigDecimal(2).setScale(4, RoundingMode.HALF_EVEN), 1L, 2L);
						accountDAO.transferAccountBalance(transaction);
					} catch (Exception e) {
						log.error("Error occurred during transfer ", e);
					} finally {
						latch.countDown();
					}
				}
			}).start();
		}

		latch.await();

		Account accountFrom = accountDAO.getAccountById(1);

		Account accountTo = accountDAO.getAccountById(2);

		log.debug("Account From: " + accountFrom);

		log.debug("Account From: " + accountTo);

		assertTrue(accountFrom.getBalance().equals(new BigDecimal(0).setScale(4, RoundingMode.HALF_EVEN)));
		assertTrue(accountTo.getBalance().equals(new BigDecimal(300).setScale(4, RoundingMode.HALF_EVEN)));

	}

}
