package com.revolut.task.service;

import com.revolut.task.dao.DAOFactory;
import com.revolut.task.dao.impl.ImplDAOFactory;
import com.revolut.task.exception.CustomException;
import com.revolut.task.model.Account;
import com.revolut.task.model.CurrencyValidationUtil;

import org.apache.log4j.Logger;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * Account Service 
 */
@Path("/account")
@Produces(MediaType.APPLICATION_JSON)
public class AccountService {
	
	private static final DAOFactory factoryOBJ=new ImplDAOFactory();
    private static Logger log = Logger.getLogger(AccountService.class);

    
    /**
     * Find all accounts
     * @return
     * @throws CustomException
     */
    @GET
    @Path("/all")
    public List<Account> getAllAccounts() throws CustomException {
        return factoryOBJ.getAccountDAO().getAllAccounts();
    }

    /**
     * Find by account id
     * @param accountId
     * @return
     * @throws CustomException
     */
    @GET
    @Path("/{accountId}")
    public Account getAccount(@PathParam("accountId") long accountId) throws CustomException {
        return factoryOBJ.getAccountDAO().getAccountById(accountId);
    }
    
    /**
     * Find balance by account Id
     * @param accountId
     * @return
     * @throws CustomException
     */
    @GET
    @Path("/{accountId}/balance")
    public BigDecimal getBalance(@PathParam("accountId") long accountId) throws CustomException {
        final Account account = factoryOBJ.getAccountDAO().getAccountById(accountId);

        if(account == null){
            throw new WebApplicationException("Account not found", Response.Status.NOT_FOUND);
        }
        return account.getBalance();
    }
    
    /**
     * Create Account
     * @param account
     * @return
     * @throws CustomException
     */
    @Path("/create")
	@POST
    public Account createAccount(@FormParam(value = "userName") String userName,
    		@FormParam(value = "currencyCode") String currencyCode,
    		@FormParam(value = "balance") String balance,
    		@FormParam(value = "accountId") int accountId) throws CustomException {
    	Account account = new Account(accountId,userName,new BigDecimal(balance), currencyCode);
        final long createdAccountId = factoryOBJ.getAccountDAO().createAccount(account);
        return factoryOBJ.getAccountDAO().getAccountById(createdAccountId);
    }

    /**
     * Deposit amount by account Id
     * @param accountId
     * @param amount
     * @return
     * @throws CustomException
     */
    @PUT
    @Path("/{accountId}/deposit/{amount}")
    public Account deposit(@PathParam("accountId") long accountId,@PathParam("amount") BigDecimal amount) throws CustomException {

        if (amount.compareTo(CurrencyValidationUtil.zeroAmount) <=0){
            throw new WebApplicationException("Invalid Deposit amount", Response.Status.BAD_REQUEST);
        }

        factoryOBJ.getAccountDAO().updateAccountBalance(accountId,amount.setScale(4, RoundingMode.HALF_EVEN));
        return factoryOBJ.getAccountDAO().getAccountById(accountId);
    }

    /**
     * Withdraw amount by account Id
     * @param accountId
     * @param amount
     * @return
     * @throws CustomException
     */
    @PUT
    @Path("/{accountId}/withdraw/{amount}")
    public Account withdraw(@PathParam("accountId") long accountId,@PathParam("amount") BigDecimal amount) throws CustomException {

        if (amount.compareTo(CurrencyValidationUtil.zeroAmount) <=0){
            throw new WebApplicationException("Invalid Deposit amount", Response.Status.BAD_REQUEST);
        }
        BigDecimal delta = amount.negate();
        if (log.isDebugEnabled())
            log.debug("Withdraw service: delta change to account  " + delta + " Account ID = " +accountId);
        factoryOBJ.getAccountDAO().updateAccountBalance(accountId,delta.setScale(4, RoundingMode.HALF_EVEN));
        return factoryOBJ.getAccountDAO().getAccountById(accountId);
    }
   
    
    /**
     * Delete amount by account Id
     * @param accountId
     * @param amount
     * @return
     * @throws CustomException
     */
    @DELETE
    @Path("/{accountId}")
    public Response deleteAccount(@PathParam("accountId") long accountId) throws CustomException {
        Account deletedAccount = factoryOBJ.getAccountDAO().deleteAccountById(accountId);
        if (factoryOBJ.getAccountDAO().deleteAccountById(accountId) == null) {
            return Response.status(Response.Status.OK).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

}
