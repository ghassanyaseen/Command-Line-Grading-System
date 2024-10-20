package com.example.model;

public class Course {

    String courseName;
    String Instructor;

    public Course( String courseName ,String Instructor ){
        this.courseName = courseName;
        this.Instructor = Instructor;
    }

    public String getCourseName() {
        return courseName;
    }

    public String toString(){
        return "Assigning " + this.courseName + " to " + this.Instructor + " is done successfully";
    }

}
