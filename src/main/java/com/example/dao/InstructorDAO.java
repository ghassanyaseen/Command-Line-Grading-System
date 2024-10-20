package com.example.dao;

import java.util.ArrayList;

public interface InstructorDAO {

    ArrayList<String> showInstructorCourses(String name);

    public String enterStudentGrades(String courseName, String studentUsername, int grade);

    public ArrayList<String> showStudentsInTheCourse(String courseName, String instructorUsername);

}
