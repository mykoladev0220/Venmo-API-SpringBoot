package com.techelevator.tenmo.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.techelevator.tenmo.dao.JdbcAccountDAO;
import com.techelevator.tenmo.dao.JdbcTransferDAO;
import com.techelevator.tenmo.dao.TransferDAO;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;

@PreAuthorize("isAuthenticated()")
@RestController
public class TransferController {

	private JdbcTransferDAO transferDAO;
	
	public TransferController(JdbcTransferDAO transfer) {
		this.transferDAO = transfer;
	}
	
	@RequestMapping(path= "transfers/{id}", method = RequestMethod.GET)
	public List<Transfer> listAllTransfersById(@PathVariable int id) {
		return transferDAO.getAllTransfersByAccountId(id);
	}
	
	@ResponseStatus(HttpStatus.CREATED)
	@RequestMapping(path= "transfers", method = RequestMethod.POST)
	public void createSendRequest(@RequestBody Transfer transfer ) {
		transferDAO.send(transfer);
	}
	
	@RequestMapping(path= "transfers/pending/{accountId}", method = RequestMethod.GET)
	public List<Transfer> listAllPendingTransfersById(@PathVariable int accountId) {
		return transferDAO.getPendingTransfersByAccountId(accountId);
	}
	
	@RequestMapping(path = "transfers/status/{transferId}/{statusId}", method = RequestMethod.PUT) 
	public void changeTransferStatusByTransferId(@PathVariable int transferId, @PathVariable int statusId) {
		transferDAO.changeTransferStatusByTransferIdandStatus(transferId, statusId);
	}
	
}
