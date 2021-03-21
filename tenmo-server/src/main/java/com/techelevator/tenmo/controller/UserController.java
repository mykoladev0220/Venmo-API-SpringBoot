package com.techelevator.tenmo.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.techelevator.tenmo.dao.JdbcUserDAO;
import com.techelevator.tenmo.model.User;

@PreAuthorize("isAuthenticated()")
@RestController
public class UserController {
	
	private JdbcUserDAO user;
	
	public UserController(JdbcUserDAO user) {
		this.user = user;
	}
	
	@RequestMapping(path= "/users", method = RequestMethod.GET)
	public List<User> listAllUsers() {
		return user.findAll();
	}
	
	@RequestMapping(path= "/users/{username}", method = RequestMethod.GET)
	public User viewUser(@PathVariable String username) {
		return user.findByUsername(username);
	}
	
	@RequestMapping(path= "/balance/{username}", method = RequestMethod.GET)
	public BigDecimal viewBalanceExchange(@PathVariable String username) {
		return user.getBalanceExchange(user.findIdByUsername(username));
	}
	
	@RequestMapping(path = "/users/account/{accountId}", method = RequestMethod.GET)
	public String viewUsernameByAccountId(@PathVariable int accountId) {
		return user.getUsernameByAccountId(accountId);
	}
	
	@RequestMapping(path = "/users/userId/{accountId}", method = RequestMethod.GET)
	public int viewUserIdByAccountId(@PathVariable int accountId) {
		return user.getUserIdByAccountId(accountId);
	}
}
