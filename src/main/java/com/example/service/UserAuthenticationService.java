package com.example.service;

import java.sql.SQLException;

public interface UserAuthenticationService {

    public boolean authenticateUser(String username, String enteredPassword) throws SQLException;

    public String getUserType(String username) throws SQLException;
}
