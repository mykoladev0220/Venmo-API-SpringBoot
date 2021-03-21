package com.techelevator.tenmo.controller;

import java.math.BigDecimal;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.techelevator.tenmo.dao.JdbcAccountDAO;
import com.techelevator.tenmo.model.Account;

@PreAuthorize("isAuthenticated()")
@RestController
public class AccountController {

	private JdbcAccountDAO accountJdbc;
	
	public AccountController(JdbcAccountDAO account) {
		this.accountJdbc = account;
	}
	
	@RequestMapping(path = "/accounts/{userId}", method = RequestMethod.GET) 
	public int getAccountByID(@PathVariable int userId) {
		return accountJdbc.getAccountIdByUserId(userId);
	}
	
	@RequestMapping(path = "/accounts/{userId}",method = RequestMethod.PUT)
	public void updateAccountByID(@RequestBody Account account, @PathVariable int userId) {
		accountJdbc.updateAccountByID(account, userId);
	}
	
	@RequestMapping(path = "/accounts/balance/{userId}", method = RequestMethod.GET)
	public BigDecimal getBalanceByUserId(@PathVariable int userId) {
		return accountJdbc.getAccountBalanceByUserId(userId);
	}
	
}
