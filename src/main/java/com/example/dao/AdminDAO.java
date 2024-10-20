package com.example.dao;


public interface AdminDAO {
    String createUser(String username, String password, String userType);

    String createCourse(String courseName, String instructorUsername);

    String enrollStudentInTheCourse(String studentUsername, String courseName);

}


