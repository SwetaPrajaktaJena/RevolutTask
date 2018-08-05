package com.revolut.task.dao;

import com.revolut.task.exception.CustomException;
import com.revolut.task.model.Account;
import com.revolut.task.model.UserTransaction;

import java.math.BigDecimal;
import java.util.List;


public interface AccountDAO {
    List<Account> getAllAccounts() throws CustomException;
    Account getAccountById(long accountId) throws CustomException;
    long createAccount(Account account) throws CustomException;
    Account deleteAccountById(long accountId) throws CustomException;
    Account updateAccountBalance(long accountId, BigDecimal deltaAmount) throws CustomException;
    int transferAccountBalance(UserTransaction userTransaction) throws CustomException;
}
