package com.techelevator.tenmo.dao;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import com.techelevator.tenmo.model.Account;

@Component
public class JdbcAccountDAO implements AccountDAO{

	private JdbcTemplate jdbcTemplate;

	public JdbcAccountDAO(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}


	@Override
	public int getAccountIdByUserId(int user_id) {
		String sql = "SELECT account_id FROM accounts WHERE user_id = ?";
		SqlRowSet results = jdbcTemplate.queryForRowSet(sql, user_id);
		
		int account = 0;
		if (results.next()) {
			account = results.getInt("account_id");
		}
		return account;
	}
	
	@Override
	public BigDecimal getAccountBalanceByUserId(int user_id) {
		String sql = "SELECT balance FROM accounts WHERE user_id = ?";
		SqlRowSet results = jdbcTemplate.queryForRowSet(sql, user_id);

		BigDecimal account = new BigDecimal("0");
		if (results.next()) {
			account = results.getBigDecimal("balance");
		}
		return account;
	}
	
	@Override
	public Account getAccountById(int user_id) {
		String sql = "SELECT * FROM accounts WHERE user_id = ?";
		SqlRowSet results = jdbcTemplate.queryForRowSet(sql, user_id);

		if (results.next()) {
			return mapRowToAccount(results);
		}
		return null;
	}

	@Override
	public void updateAccountById(Account account, int user_id) {
		String sql = "UPDATE accounts SET balance = ? WHERE user_id = ?";
		jdbcTemplate.update(sql, account.getBalance(), UserId);

	}

	public Account mapRowToAccount(SqlRowSet results) {
		Account theAccount = new Account();
		theAccount.setAccount_id(results.getInt("account_id"));
		theAccount.setBalance(results.getBigDecimal("balance"));
		theAccount.setUser_id(results.getInt("user_id"));
		return theAccount;
	}


}
