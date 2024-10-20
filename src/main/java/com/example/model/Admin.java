package com.example.model;

public class Admin extends User {

    public Admin(String name, String password) {
        super(name, password, UserType.ADMIN);
    }

    @Override
    public String toString(){
        return "Added Admin Name = " + this.getName();
    }
}
