package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.User;

import java.math.BigDecimal;
import java.util.List;

public interface UserDAO {

    public List<User> findAll();
    
    public User findByUsername(String username);

    public int findIdByUsername(String username);

    public boolean create(String username, String password);
    
    public String getUsernameByAccountId(int accountId);
    
    public int getUserIdByAccountId(int accountId);
    
    public BigDecimal getBalanceExchange(int id);
}
