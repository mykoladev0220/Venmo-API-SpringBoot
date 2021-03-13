package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.User;

import java.math.BigDecimal;
import java.util.List;

public interface UserDAO {

    List<User> findAll();

    User findByUsername(String username);

    int findIdByUsername(String username);

    boolean create(String username, String password);
    
    String getUsernameByAccountId(int accountId);
    
    int getUserIdByAccountId(int accountId);
    
    BigDecimal getBalanceExchange(int id);
}
