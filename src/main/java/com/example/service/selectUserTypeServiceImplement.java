package com.example.service;

import com.example.model.Admin;
import com.example.model.Instructor;
import com.example.model.Student;
import com.example.model.User;

public class selectUserTypeServiceImplement {
    public static User selectUserType(String username , String password, String usertype) {
        if (usertype.equalsIgnoreCase("student")) {
            return new Student(username, password);
        } else if (usertype.equalsIgnoreCase("instructor")) {
            return new Instructor(username, password);
        } else if (usertype.equalsIgnoreCase("admin")) {
            return new Admin(username, password);
        }else return null;
    }
}
