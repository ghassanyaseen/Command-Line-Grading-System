package com.example.model;

public abstract class User {

    private String name;

    UserType userType;

    private String password;


    public User(String name, String password, UserType userType) {
        this.name = name;
        this.userType = userType;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public UserType getUserType() {
        return userType;
    }

    public String getPassword() {
        return password;
    }

}
