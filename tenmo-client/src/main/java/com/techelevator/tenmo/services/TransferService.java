package com.techelevator.tenmo.services;

import java.math.BigDecimal;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import com.techelevator.tenmo.models.AuthenticatedUser;
import com.techelevator.tenmo.models.Transfer;

public class TransferService {

	private final String BASE_URL;
	private final RestTemplate restTemplate = new RestTemplate();
	public AuthenticatedUser currentUser;

	public TransferService(String url, AuthenticatedUser currentUser) {
		this.currentUser = currentUser;
		BASE_URL = url;
	}

	public Transfer createSendTransfer(int senderUserId, int receiverUserId, BigDecimal amount) {
		Transfer newTransfer = new Transfer();
		newTransfer.setTransfer_type_id(2);
		newTransfer.setTransfer_status_id(2);

		int senderAccountId = 0;
		senderAccountId = restTemplate.exchange(BASE_URL + "accounts/" + senderUserId, HttpMethod.GET, makeAuthEntity(), int.class).getBody();
		newTransfer.setAccount_from(senderAccountId);

		int receiverAccountId = 0;
		receiverAccountId = restTemplate.exchange(BASE_URL + "accounts/" + receiverUserId, HttpMethod.GET, makeAuthEntity(), int.class).getBody();

		newTransfer.setAccount_to(receiverAccountId);
		newTransfer.setAmount(amount);

		restTemplate.exchange(BASE_URL + "transfers", HttpMethod.POST, makeTransferEntity(newTransfer), Transfer.class);
		return newTransfer;
	}
	
	public Transfer createRequestTransfer(int senderUserId, int receiverUserId, BigDecimal amount) {
		Transfer newTransfer = new Transfer();

		//type is 1.request 2.send
		newTransfer.setTransfer_type_id(1);
		
		//status is 1. pending 2.approved 3. rejected
		newTransfer.setTransfer_status_id(1);

		int senderAccountId = 0;
		int receiverAccountId = 0;

		receiverAccountId = restTemplate.exchange(BASE_URL + "accounts/" + senderUserId, HttpMethod.GET, makeAuthEntity(), int.class).getBody();
		senderAccountId = restTemplate.exchange(BASE_URL + "accounts/" + receiverUserId, HttpMethod.GET, makeAuthEntity(), int.class).getBody();
		
		newTransfer.setAccount_from(receiverAccountId);
		newTransfer.setAccount_to(senderAccountId);
		newTransfer.setAmount(amount);

		restTemplate.exchange(BASE_URL + "transfers", HttpMethod.POST, makeTransferEntity(newTransfer), Transfer.class);
		return newTransfer;
	}
	
	public Transfer[] listPendingTransfersByUserId(int userId) {
		int accountId = 0;
		accountId = restTemplate.exchange(BASE_URL + "accounts/" + userId, HttpMethod.GET, makeAuthEntity(), int.class).getBody();
		
		Transfer[] transfersByAccountId = null;
		transfersByAccountId = restTemplate.exchange(BASE_URL + "transfers/pending/" + accountId, HttpMethod.GET, makeAuthEntity(), Transfer[].class).getBody();
		return transfersByAccountId;
	}

	public Transfer[] getTransfersByAccountId(int accountId) {
		Transfer[] transfersArray;
		transfersArray = restTemplate.exchange(BASE_URL + "transfers/" + accountId, HttpMethod.GET, makeAuthEntity(), Transfer[].class).getBody();
		return transfersArray;
	}
	
	public void changeTransferStatus(int transferId, int statusCode) {
		restTemplate.exchange(BASE_URL + "transfers/status/" + transferId + "/" + statusCode, HttpMethod.PUT,
				makeAuthEntity(), int.class);
	}
	
	public HttpEntity makeAuthEntity() {
		HttpHeaders headers = new HttpHeaders();
		headers.setBearerAuth(App.AUTH_TOKEN);
		HttpEntity entity = new HttpEntity<>(headers);
		return entity;
	}

	public HttpEntity<Transfer> makeTransferEntity(Transfer transfer) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setBearerAuth(App.AUTH_TOKEN);
		HttpEntity<Transfer> entity = new HttpEntity<>(transfer, headers);
		return entity;
	}
}
