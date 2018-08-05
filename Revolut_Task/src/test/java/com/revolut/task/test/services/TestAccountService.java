package com.revolut.task.test.services;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Test;

import com.revolut.task.model.Account;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;


public class TestAccountService extends TestServerConnection {


    /*
     Test get user account by user name
              return 200 OK
     */
    @Test
    public void testGetAccountByUserName() throws IOException, URISyntaxException {
        URI uri = builder.setPath("/account/1").build();
        HttpGet request = new HttpGet(uri);
        HttpResponse response = client.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();

        assertTrue(statusCode == 200);
        String jsonString = EntityUtils.toString(response.getEntity());
        Account account = mapper.readValue(jsonString, Account.class);
        assertTrue(account.getUserName().equals("Sweta"));
    }

    /*
    Test get all user accounts
              return 200 OK
    */
    @Test
    public void testGetAllAccounts() throws IOException, URISyntaxException {
        URI uri = builder.setPath("/account/all").build();
        HttpGet request = new HttpGet(uri);
        HttpResponse response = client.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();
        assertTrue(statusCode == 200);
        String jsonString = EntityUtils.toString(response.getEntity());
        Account[] accounts = mapper.readValue(jsonString, Account[].class);
        assertTrue(accounts.length > 0);
    }

    /*
    Test get account balance given account ID
              return 200 OK
    */
    @Test
    public void testGetAccountBalance() throws IOException, URISyntaxException, ParseException {
        URI uri = builder.setPath("/account/1/balance").build();
        HttpGet request = new HttpGet(uri);
        HttpResponse response = client.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();
        assertTrue(statusCode == 200);
        String jsonString = EntityUtils.toString(response.getEntity());
        JSONParser parser = new JSONParser();
        JSONObject json = (JSONObject) parser.parse(jsonString);
        String balance = json.get("value").toString();
        BigDecimal res = new BigDecimal(balance).setScale(4, RoundingMode.HALF_EVEN);
        BigDecimal db = new BigDecimal(100).setScale(4, RoundingMode.HALF_EVEN);
        assertTrue(res.equals(db));
    }

    /*
     Test create new user account
              return 200 OK
    */
    @Test
    public void testCreateAccount() throws IOException, URISyntaxException {
        URI uri = builder.setPath("/account/create").build();
        BigDecimal balance = new BigDecimal(10).setScale(4, RoundingMode.HALF_EVEN);
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("userName", "test"));
        params.add(new BasicNameValuePair("currencyCode", "USD"));
        params.add(new BasicNameValuePair("balance", "110"));
        params.add(new BasicNameValuePair("accountId", "11"));
        HttpPost request = new HttpPost(uri);
        request.setHeader("Content-type", "application/x-www-form-urlencoded");
        request.setEntity(new UrlEncodedFormEntity(params));
        HttpResponse response = client.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();
        assertTrue(statusCode == 200);
        HttpEntity respEntity = response.getEntity();

	     if (respEntity != null) {
	         String content =  EntityUtils.toString(respEntity);
	     }
    }

    /*
    Test create user account already existed.
              return 204 
    */
    @Test
    public void testCreateExistingAccount() throws IOException, URISyntaxException {
        URI uri = builder.setPath("/account/create").build();
        Account acc = new Account(1,"Sweta", new BigDecimal(100), "USD");
        HttpPost request = new HttpPost(uri);
        request.setHeader("Content-type", "application/x-www-form-urlencoded");
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("userName", "Sweta"));
        params.add(new BasicNameValuePair("currencyCode", "USD"));
        params.add(new BasicNameValuePair("balance", "100"));
        params.add(new BasicNameValuePair("accountId", "1"));
        request.setEntity(new UrlEncodedFormEntity(params));
        HttpResponse response = client.execute(request);
        
        HttpEntity respEntity = response.getEntity();

	     if (respEntity != null) {
	         String content =  EntityUtils.toString(respEntity);
	     }
	     int statusCode = response.getStatusLine().getStatusCode();
        assertTrue(statusCode == 204);

    }

    /*
  Delete valid user account
              return 200 OK
    */
    @Test
    public void testDeleteAccount() throws IOException, URISyntaxException {
        URI uri = builder.setPath("/account/3").build();
        HttpDelete request = new HttpDelete(uri);
        request.setHeader("Content-type", "application/json");
        HttpResponse response = client.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();
        assertTrue(statusCode == 200);
    }


    /*
  Test delete non-existent account.
              return 200 
    */
    @Test
    public void testDeleteNonExistingAccount() throws IOException, URISyntaxException {
        URI uri = builder.setPath("/account/300").build();
        HttpDelete request = new HttpDelete(uri);
        request.setHeader("Content-type", "application/json");
        HttpResponse response = client.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();
        assertTrue(statusCode == 200);
    }


}
