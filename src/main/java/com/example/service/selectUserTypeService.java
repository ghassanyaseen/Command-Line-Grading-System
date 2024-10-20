package com.example.service;

import com.example.model.User;

public interface selectUserTypeService {
    public User selectUserType(String username , String password, String usertype);
}
