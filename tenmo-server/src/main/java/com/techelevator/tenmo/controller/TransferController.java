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

import com.techelevator.tenmo.dao.JdbcTransferDAO;
import com.techelevator.tenmo.dao.TransferDAO;
import com.techelevator.tenmo.model.Transfer;

@PreAuthorize("isAuthenticated()")
@RestController
public class TransferController {

	private JdbcTransferDAO transferJdbc;
	
	public TransferController(JdbcTransferDAO transfer) {
		this.transferJdbc = transfer;
	}
	
	@ResponseStatus(HttpStatus.CREATED)
	@RequestMapping(path= "/transfers", method = RequestMethod.POST)
	public void createTransfer(@RequestBody Transfer transfer ) {
		transferJdbc.send(transfer);
	}
	
	@RequestMapping(path= "/transfers/{id}", method = RequestMethod.GET)
	public List<Transfer> listAllTransfersById(@PathVariable int id) {
		return transferJdbc.getAllTransfersByAccountId(id);
	}
	
	@RequestMapping(path= "/transfers/pending/{accountId}", method = RequestMethod.GET)
	public List<Transfer> listAllPendingTransfersById(@PathVariable int accountId) {
		return transferJdbc.getPendingTransfersByAccountId(accountId);
	}
	
	@RequestMapping(path = "/transfers/status/{transferId}/{statusId}", method = RequestMethod.PUT) 
	public void changeTransferStatusByTransferId(@PathVariable int transferId, @PathVariable int statusId) {
		transferJdbc.changeTransferStatusByTransferIdandStatus(transferId, statusId);
	}
	
}
