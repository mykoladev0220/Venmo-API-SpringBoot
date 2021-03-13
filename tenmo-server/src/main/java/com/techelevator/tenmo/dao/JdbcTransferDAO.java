package com.techelevator.tenmo.dao;

import java.util.ArrayList;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import com.techelevator.tenmo.model.Transfer;


@Component
public class JdbcTransferDAO implements TransferDAO{

	private JdbcTemplate jdbcTemplate;

	public JdbcTransferDAO(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@Override
	public List<Transfer> getAllTransfers() {
		List<Transfer> transfers = new ArrayList<>();
		String sql = "SELECT * FROM transfers;";
		SqlRowSet results = jdbcTemplate.queryForRowSet(sql);

		while(results.next()) {
			Transfer transfer = mapRowToTransfer(results);
			transfers.add(transfer);
		}
		return transfers;
	}
	
	@Override
	public List<Transfer> getAllTransfersByAccountId(int accountId)  {
		List<Transfer> transfers = new ArrayList<>();
		
		// maybe add the AND statement to only show accepted transfers, instead of all statuses ?
		String sql = "SELECT * FROM transfers WHERE (account_from = ? OR account_to = ?) ";
			//	+ "AND transfer_status_id = 2";
		
		SqlRowSet results = jdbcTemplate.queryForRowSet(sql, accountId, accountId);

		while(results.next()) {
			Transfer transfer = mapRowToTransfer(results);
			transfers.add(transfer);
		}
		return transfers;
	}
	
	public List<Transfer> getPendingTransfersByAccountId(int accountId) {
		
		List<Transfer> transfers = new ArrayList<>();
		String sql = "SELECT * FROM transfers WHERE (account_from = ? OR account_to = ?) AND transfer_status_id = 1";
		SqlRowSet results = jdbcTemplate.queryForRowSet(sql, accountId, accountId);

		if (results != null) {
			while(results.next()) {
				Transfer transfer = mapRowToTransfer(results);
				transfers.add(transfer);
			}
		}
		return transfers;
	}
	
	public void changeTransferStatusByTransferIdandStatus(int transferId, int transferStatus) {
		
		String sql = "UPDATE transfers SET transfer_status_id = ? WHERE transfer_id = ?";
		jdbcTemplate.update(sql, transferStatus, transferId);
	}
	
	@Override
	public void send(Transfer transfer) {
		
//		Transfer sentTransfer = null;
		
		String sql = "INSERT INTO transfers (transfer_type_id,"
				+ " transfer_status_id, account_from, account_to , amount)"
				+ " VALUES (?, ?, ?, ?, ?)";
//				+ " RETURNING transfer_id";

		jdbcTemplate.update(sql, transfer.getTransfer_type_id(), transfer.getTransfer_status_id(),
				transfer.getAccount_from(), transfer.getAccount_to(), transfer.getAmount());
		
//		Integer newTransferId = jdbcTemplate.queryForObject(sql, new Object[]{transfer.getTransfer_type_id(), transfer.getTransfer_status_id(),
//				transfer.getAccount_from(), transfer.getAccount_to(), transfer.getAccount_from()} 
//				 , Integer.class);

		// retrieve transfer just made and return as object
//		String sqlSelect = "SELECT * FROM transfers WHERE transfer_id = ?";
//
//		SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sqlSelect, newTransferId);
//		if (rowSet.next()){
//			sentTransfer = mapRowToTransfer(rowSet);
//		}
//		return sentTransfer;
	}

	private Transfer mapRowToTransfer(SqlRowSet rs) {
		Transfer transfer = new Transfer();
		transfer.setTransfer_id(rs.getInt("transfer_id"));
		transfer.setTransfer_type_id(rs.getInt("transfer_type_id"));
		transfer.setTransfer_status_id(rs.getInt("transfer_status_id"));
		transfer.setAccount_from(rs.getInt("account_from"));
		transfer.setAccount_to(rs.getInt("account_to"));
		transfer.setAmount(rs.getBigDecimal("amount"));
		return transfer;
	}
}
