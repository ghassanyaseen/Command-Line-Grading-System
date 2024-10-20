package com.example.service;

import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserAuthenticationServiceImplementation implements UserAuthenticationService {

    private Connection connection;
    private HashPasswordService hashPasswordService = new HashPasswordService();

    public UserAuthenticationServiceImplementation(Connection connection) {
        this.connection = connection;
    }

    public boolean authenticateUser(String username, String enteredPassword) throws SQLException {
        String sql = "SELECT password, salt FROM Users WHERE username = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);
            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                String storedHashedPassword = rs.getString("password");
                String salt = rs.getString("salt");

                try {
                    return hashPasswordService.verifyPassword(enteredPassword, storedHashedPassword, salt);
                } catch (NoSuchAlgorithmException e) {
                    throw new RuntimeException("An error occurred while verifying the password.", e);
                }
            }
        }

        return false;
    }

    public String getUserType(String username) throws SQLException {
        String sql = "SELECT userType FROM Users WHERE username = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);
            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                return rs.getString("userType");
            }
        }
        throw new SQLException("User type not found for username: " + username);
    }
}
