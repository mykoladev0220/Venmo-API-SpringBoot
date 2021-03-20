package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.User;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcUserDAO implements UserDAO {

	private static final BigDecimal STARTING_BALANCE = new BigDecimal("1000.00");
	private JdbcTemplate jdbcTemplate;

	public JdbcUserDAO(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	
	@Override
	public int getUserIdByAccountId(int accountId) {
		String sql = "SELECT user_id FROM accounts WHERE account_id = ?";
		int returnedAccountId = 0;
		return returnedAccountId = jdbcTemplate.queryForObject(sql, int.class, accountId);
	}
	
	@Override
	public String getUsernameByAccountId(int accountId) {
		String sql = "SELECT username FROM users" + 
				" INNER JOIN accounts" + 
				" ON users.user_id = accounts.user_id" + 
				" WHERE accounts.account_id = ?";
		
		String username = "";
		return username = jdbcTemplate.queryForObject(sql, String.class, accountId);
	}
	
	@Override
	public int findIdByUsername(String username) {
		String sql = "SELECT user_id FROM users WHERE username ILIKE ?;";
		Integer id = jdbcTemplate.queryForObject(sql, Integer.class, username);
		if (id != null) {
			return id;
		} else {
			return -1;
		}
	}

	@Override
	public List<User> findAll() {
		String sql = "SELECT user_id, username, password_hash FROM users;";
		SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
		
		List<User> users = new ArrayList<>();
		while(results.next()) {
			User user = mapRowToUser(results);
			users.add(user);
		}
		return users;
	}

	@Override
	public User findByUsername(String username) throws UsernameNotFoundException {
		String sql = "SELECT user_id, username, password_hash FROM users WHERE username ILIKE ?;";
		SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, username);
		
		if (rowSet.next()){
			return mapRowToUser(rowSet);
		}
		throw new UsernameNotFoundException("User " + username + " was not found.");
	}

	@Override
	public boolean create(String username, String password) {
		String sql = "INSERT INTO users (username, password_hash) VALUES (?, ?) RETURNING user_id";
		String password_hash = new BCryptPasswordEncoder().encode(password);
		int newUserId;
		try {
			newUserId = jdbcTemplate.queryForObject(sql, Integer.class, username, password_hash);
		} catch (DataAccessException e) {
			return false;
		}

		sql = "INSERT INTO accounts (user_id, balance) values(?, ?)";
		try {
			jdbcTemplate.update(sql, newUserId, STARTING_BALANCE);
		} catch (DataAccessException e) {
			return false;
		}
		return true;
	}
		
	@Override
	public BigDecimal getBalanceExchange(int id) {
		String sql = "SELECT balance FROM accounts WHERE user_id = ?";
		BigDecimal balance = BigDecimal.valueOf(0);
		return balance = jdbcTemplate.queryForObject(sql, BigDecimal.class, id);
	}

	private User mapRowToUser(SqlRowSet rs) {
		User user = new User();
		user.setId(rs.getLong("user_id"));
		user.setUsername(rs.getString("username"));
		user.setPassword(rs.getString("password_hash"));
		user.setActivated(true);
		user.setAuthorities("USER");
		return user;
	}
}
