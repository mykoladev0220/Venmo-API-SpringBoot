package com.techelevator.tenmo.dao;

import java.util.List;

import com.techelevator.tenmo.model.Transfer;


public interface TransferDAO {
	
	public void send(Transfer transfer);
	
	public List<Transfer> getAllTransfers();
	
	public List<Transfer> getAllTransfersByAccountId(int accountId);
	
	public List<Transfer> getPendingTransfersByAccountId(int accountId);
	
	public void changeTransferStatusByTransferIdandStatus(int transferId, int transferStatus);
}
