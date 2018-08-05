package com.revolut.task.test.dao;

import com.revolut.task.dao.DAOFactory;
import com.revolut.task.dao.impl.ImplDAOFactory;
import com.revolut.task.exception.CustomException;
import com.revolut.task.model.Account;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import static junit.framework.TestCase.assertTrue;

public class TestAccountDAO {

	private static final DAOFactory factoryOBJ=new ImplDAOFactory();

	@BeforeClass
	public static void setup() {
		/*Adding src/main/resources/META-INF/SQL/Testdata.sql file data To EntityManager Objects through
		 *  Herbernate mapping in src/main/resources/META-INF/persistence.xml file
		 * */
		 
		factoryOBJ.populateTestData();
	}

	@After
	public void tearDown() {

	}

	@Test
	public void testGetAllAccounts() throws CustomException {
		List<Account> allAccounts = factoryOBJ.getAccountDAO().getAllAccounts();
		assertTrue(allAccounts.size() > 1);
	}

	@Test
	public void testGetAccountById() throws CustomException {
		// as per testData , account name for account 1 is Sweta.
		Account account = factoryOBJ.getAccountDAO().getAccountById(1);
		assertTrue(account.getUserName().equals("Sweta"));
		
	}

	@Test
	public void testGetNonExistingAccById() throws CustomException {
		//There is no Account With AccountId=100.
		Account account = factoryOBJ.getAccountDAO().getAccountById(100);
		assertTrue(account == null);
	}

	@Test
	public void testCreateAccount() throws CustomException {
		long newaccountId = 7;
		BigDecimal balance = new BigDecimal(10).setScale(4, RoundingMode.HALF_EVEN);
		//Let,s Create an Account with AccountId=7,UserName=Prajukta,Balance=10,CurrencyCode=USD. 
		Account a = new Account(newaccountId, "Prajukta", balance, "USD");
		factoryOBJ.getAccountDAO().createAccount(a);
		Account newAccount = factoryOBJ.getAccountDAO().getAccountById(newaccountId);
		assertTrue(newAccount.getUserName().equals("Prajukta"));
		assertTrue(newAccount.getCurrencyCode().equals("USD"));
		assertTrue(newAccount.getBalance().equals(balance));
	}

	@Test
	public void testDeleteAccount() throws CustomException {
		Account deletedAccount= factoryOBJ.getAccountDAO().deleteAccountById(2);
		// assert user no longer there
		assertTrue(factoryOBJ.getAccountDAO().getAccountById(2) == null);
	}

	@Test
	public void testDeleteNonExistingAccount() throws CustomException {
		Account deletedAccount = factoryOBJ.getAccountDAO().deleteAccountById(9);
		// assert no row(user) deleted
		assertTrue(factoryOBJ.getAccountDAO().getAccountById(9) == null);

	}

	@Test
	public void testUpdateAccountBalance() throws CustomException {
        /*Here Depositing 50 Rs to AccountId=1 whose before Deposition  Balance =100,After Deposition AccountBalance is 150.
         *WithDrawing 50 Rs From AccountId=1 after Withdraw  AccountBalance is 100.
         */
         
		BigDecimal depositAmount = new BigDecimal(50).setScale(4, RoundingMode.HALF_EVEN);
		BigDecimal afterDeposit = new BigDecimal(150).setScale(4, RoundingMode.HALF_EVEN);
		Account accountDeposited = factoryOBJ.getAccountDAO().updateAccountBalance(1, depositAmount);
		assertTrue(accountDeposited != null);
		assertTrue(factoryOBJ.getAccountDAO().getAccountById(1).getBalance().equals(afterDeposit));
		BigDecimal withDrawAmount = new BigDecimal(-50).setScale(4, RoundingMode.HALF_EVEN);
		BigDecimal afterWithDraw = new BigDecimal(100).setScale(4, RoundingMode.HALF_EVEN);
		Account accountWithdrawn = factoryOBJ.getAccountDAO().updateAccountBalance(1, withDrawAmount);
		assertTrue(accountWithdrawn != null);
		assertTrue(factoryOBJ.getAccountDAO().getAccountById(1).getBalance().equals(afterWithDraw));

	}

	@Test(expected = CustomException.class)
	public void testUpdateAccountBalanceNotEnoughFund() throws CustomException {
		/*
		 * Here Account Balance  of AccountId=1 is 100.Account has no Sufficient Fund to withdraw 50000 
		 */
		BigDecimal withDrawAmount = new BigDecimal(-50000).setScale(4, RoundingMode.HALF_EVEN);
		Account accountWithdrawn = factoryOBJ.getAccountDAO().updateAccountBalance(1, withDrawAmount);
		assertTrue(accountWithdrawn == null);

	}

}