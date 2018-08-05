package com.revolut.task.test.services;


import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.junit.Test;

import com.revolut.task.model.Account;
import com.revolut.task.model.UserTransaction;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class TestTransactionService extends TestServerConnection {
    //Test transaction:Transfer From One Account to AnotherAccount.

    /*
      Test deposit money to given account number
                 return 200 OK
    */
    @Test
    public void testDeposit() throws IOException, URISyntaxException {
        URI uri = builder.setPath("/account/1/deposit/100").build();
        HttpPut request = new HttpPut(uri);
        request.setHeader("Content-type", "application/json");
        HttpResponse response = client.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();
        assertTrue(statusCode == 200);
        String jsonString = EntityUtils.toString(response.getEntity());
        Account afterDeposit = mapper.readValue(jsonString, Account.class);
        assertTrue(afterDeposit.getBalance().equals(new BigDecimal(200).setScale(4, RoundingMode.HALF_EVEN)));

    }

    /*
    Test withdraw money from account given account number
                return statusCode 200 OK
    */
    @Test
    public void testWithDrawSufficientFund() throws IOException, URISyntaxException {
        URI uri = builder.setPath("/account/2/withdraw/100").build();
        HttpPut request = new HttpPut(uri);
        request.setHeader("Content-type", "application/json");
        HttpResponse response = client.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();
        assertTrue(statusCode == 200);
        String jsonString = EntityUtils.toString(response.getEntity());
        Account afterDeposit = mapper.readValue(jsonString, Account.class);
        assertTrue(afterDeposit.getBalance().equals(new BigDecimal(100).setScale(4, RoundingMode.HALF_EVEN)));

    }

    /*
       Test withdraw money from account given account number, no sufficient fund in account
                 return 500 INTERNAL SERVER ERROR
    */
    @Test
    public void testWithDrawNonSufficientFund() throws IOException, URISyntaxException {
        URI uri = builder.setPath("/account/2/withdraw/1000.23456").build();
        HttpPut request = new HttpPut(uri);
        request.setHeader("Content-type", "application/json");
        HttpResponse response = client.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();
        String responseBody = EntityUtils.toString(response.getEntity());
        assertTrue(statusCode == 500);
        assertTrue(responseBody.contains("Not sufficient Fund"));
    }

    /*
       TC B4 Positive Category = AccountService
       Scenario: test transaction from one account to another with source account has sufficient fund
                 return 200 OK
    */
    @Test
    public void testTransactionEnoughFund() throws IOException, URISyntaxException {
        URI uri = builder.setPath("/transaction").build();
        BigDecimal amount = new BigDecimal(10).setScale(4, RoundingMode.HALF_EVEN);
        //UserTransaction transaction = new UserTransaction("EUR", amount, 3L, 4L);

        //String jsonInString = mapper.writeValueAsString(transaction);
        //StringEntity entity = new StringEntity(jsonInString);
        HttpPost request = new HttpPost(uri);
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("currencyCode", "EUR"));
        params.add(new BasicNameValuePair("amount", "10"));
        params.add(new BasicNameValuePair("fromAccountId", "3"));
        params.add(new BasicNameValuePair("toAccountId", "4"));
        request.setEntity(new UrlEncodedFormEntity(params));
        request.setHeader("Content-type", "application/x-www-form-urlencoded");
        //request.setEntity(entity);
        HttpResponse response = client.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();
        assertTrue(statusCode == 200);
    }

    /*
         Test transaction from one account to another with source account has no sufficient fund
                  return 500 INTERNAL SERVER ERROR
     */
    @Test
    public void testTransactionNotEnoughFund() throws IOException, URISyntaxException {
        URI uri = builder.setPath("/transaction").build();
        BigDecimal amount = new BigDecimal(100000).setScale(4, RoundingMode.HALF_EVEN);
        UserTransaction transaction = new UserTransaction("EUR", amount, 3L, 4L);

        String jsonInString = mapper.writeValueAsString(transaction);
        StringEntity entity = new StringEntity(jsonInString);
        HttpPost request = new HttpPost(uri);
        request.setHeader("Content-type", "application/json");
        request.setEntity(entity);
        HttpResponse response = client.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();
        assertTrue(statusCode == 500);
    }

    /*
    Test transaction from one account to another  with different currency code
                 return 500 INTERNAL SERVER ERROR
    */
    @Test
    public void testTransactionDifferentCcy() throws IOException, URISyntaxException {
        URI uri = builder.setPath("/transaction").build();
        BigDecimal amount = new BigDecimal(100).setScale(4, RoundingMode.HALF_EVEN);
        UserTransaction transaction = new UserTransaction("USD", amount, 3L, 4L);

        String jsonInString = mapper.writeValueAsString(transaction);
        StringEntity entity = new StringEntity(jsonInString);
        HttpPost request = new HttpPost(uri);
        request.setHeader("Content-type", "application/json");
        request.setEntity(entity);
        HttpResponse response = client.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();
        assertTrue(statusCode == 500);

    }


}
