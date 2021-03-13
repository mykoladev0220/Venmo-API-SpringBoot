package com.techelevator.tenmo.services;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import com.techelevator.tenmo.App;
import com.techelevator.tenmo.models.Account;
import com.techelevator.tenmo.models.AuthenticatedUser;
import com.techelevator.tenmo.models.Transfer;
import com.techelevator.tenmo.models.User;

public class UserService {

	private final String BASE_URL;
	private final RestTemplate restTemplate = new RestTemplate();
	public AuthenticatedUser currentUser;

	public UserService(String url, AuthenticatedUser currentUser) {
		this.currentUser = currentUser;
		BASE_URL = url;
	}

	public List<User> listAllUsers(int idToRemove) {

		User[] users = null;

		users = restTemplate.exchange(BASE_URL + "users/", HttpMethod.GET, makeAuthEntity(), User[].class).getBody();
		List<User> usersWithout = new ArrayList<>();

		for (int i=0; i<users.length; i++) {
			if (users[i].getId() != idToRemove) {
				usersWithout.add(users[i]);
			}
		}
		return usersWithout;
	}

	public BigDecimal getBalanceByExchange() {
		BigDecimal currentBalance = new BigDecimal("0");
		currentBalance = restTemplate.exchange
				(BASE_URL + "balance/" + App.USERNAME
						, HttpMethod.GET, makeAuthEntity(), BigDecimal.class).getBody();
		return currentBalance;
	}

	// used to be called sendMoney
	public boolean validateTransfer(int receiverId, BigDecimal amountToSend, String sender, int senderId) {

		boolean isSuccessful = false;
		boolean validUser = false;
		
		List<User> users = listAllUsers(senderId);

		for (User user : users) {
			if (user.getId() == receiverId) {
				validUser = true;
			}
		}

		if (!validUser) {
			System.out.println("Invalid user!");
			return isSuccessful;
		}

		if (getBalanceByExchange().compareTo(amountToSend) >= 0 && validUser) {

			System.out.println("\nTransaction successful!");
			return isSuccessful = true;

		} else {
			System.out.println("\nInsufficient funds!");
			return isSuccessful = false;
		}
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
