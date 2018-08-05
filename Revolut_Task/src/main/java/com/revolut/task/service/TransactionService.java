package com.revolut.task.service;

import java.math.BigDecimal;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.revolut.task.dao.DAOFactory;
import com.revolut.task.dao.impl.ImplDAOFactory;
import com.revolut.task.exception.CustomException;
import com.revolut.task.model.CurrencyValidationUtil;
import com.revolut.task.model.UserTransaction;

@Path("/transaction")
@Produces(MediaType.APPLICATION_JSON)
public class TransactionService {

	private static final DAOFactory factoryOBJ=new ImplDAOFactory();
	
	//Transfer From One Account To Another.
	@POST
	public Response transferFund(@FormParam(value = "currencyCode") String currencyCode,
    		@FormParam(value = "amount") BigDecimal amount,
    		@FormParam(value = "fromAccountId") long fromAccountId,
    		@FormParam(value = "toAccountId") long toAccountId) throws CustomException {
		UserTransaction transaction = new UserTransaction(currencyCode,amount,fromAccountId,toAccountId);
		String currency = transaction.getCurrencyCode();
		if (CurrencyValidationUtil.INSTANCE.validateCurrencycCode(currency)) {
			int updateCount = factoryOBJ.getAccountDAO().transferAccountBalance(transaction);
			if (updateCount == 1) {
				return Response.status(Response.Status.OK).build();
			} else {
				throw new WebApplicationException("Transaction failed", Response.Status.BAD_REQUEST);
			}

		} else {
			throw new WebApplicationException("Currency Code Invalid ", Response.Status.BAD_REQUEST);
		}

	}

}
