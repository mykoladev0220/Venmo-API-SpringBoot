package com.techelevator.tenmo.dao;

import java.util.List;

import com.techelevator.tenmo.model.Transfer;


public interface TransferDAO {
	
	void send(Transfer transfer);
	
	List<Transfer> getAllTransfers();
	
	List<Transfer> getAllTransfersByAccountId(int accountId);
	
	List<Transfer> getPendingTransfersByAccountId(int accountId);
	
	void changeTransferStatusByTransferIdandStatus(int transferId, int transferStatus);
}
