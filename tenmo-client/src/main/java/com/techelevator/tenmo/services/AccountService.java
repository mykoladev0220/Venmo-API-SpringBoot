package com.techelevator.tenmo.services;

import java.math.BigDecimal;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import com.techelevator.tenmo.App;
import com.techelevator.tenmo.models.Account;
import com.techelevator.tenmo.models.AuthenticatedUser;

public class AccountService {
	
	private final String BASE_URL;
	private final RestTemplate restTemplate = new RestTemplate();
	public AuthenticatedUser currentUser;

	public AccountService(String url, AuthenticatedUser currentUser) {
		this.currentUser = currentUser;
		BASE_URL = url;
	}

	public void increaseBalance(int userId, BigDecimal amountToChange) {

		Account account = new Account();
		int accountId = 0;
		accountId = restTemplate.exchange(BASE_URL + "accounts/" + userId, HttpMethod.GET,
				makeAuthEntity(), int.class).getBody();

		account.setAccount_id(accountId);

		BigDecimal balanceBD = new BigDecimal("0");
		balanceBD = restTemplate.exchange(BASE_URL + "accounts/balance/" + userId, HttpMethod.GET,
				makeAuthEntity(), BigDecimal.class).getBody();

		account.setBalance(balanceBD.add(amountToChange));

		account.setUser_id(userId);

		restTemplate.exchange(BASE_URL + "/accounts/" + userId, HttpMethod.PUT, makeAccountEntity(account), Account.class);
	}

	public void decreaseBalance(int userId, BigDecimal amountToChange) {

		Account account = new Account();
		int accountId = 0;
		accountId = restTemplate.exchange(BASE_URL + "accounts/" + userId, HttpMethod.GET,
				makeAuthEntity(), int.class).getBody();

		account.setAccount_id(accountId);

		BigDecimal balanceBD = new BigDecimal("0");
		balanceBD = restTemplate.exchange(BASE_URL + "accounts/balance/" + userId, HttpMethod.GET,
				makeAuthEntity(), BigDecimal.class).getBody();
		// have to retrieve current balance
		account.setBalance(balanceBD.subtract(amountToChange));

		account.setUser_id(userId);

		restTemplate.exchange(BASE_URL + "/accounts/" + userId, HttpMethod.PUT, makeAccountEntity(account), Account.class);
	}
	
	public HttpEntity makeAuthEntity() {
		HttpHeaders headers = new HttpHeaders();
		headers.setBearerAuth(App.AUTH_TOKEN);
		HttpEntity entity = new HttpEntity<>(headers);
		return entity;
	}
	
	public HttpEntity<Account> makeAccountEntity(Account account) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setBearerAuth(App.AUTH_TOKEN);
		HttpEntity<Account> entity = new HttpEntity<>(account, headers);
		return entity;
	}
	
}
