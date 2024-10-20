package com.example.model;

public class StudentGrade {
    private String username;
    private Integer grade;

    public StudentGrade(String username, Integer grade) {
        this.username = username;
        this.grade = grade;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getGrade() {
        return grade;
    }

    public void setGrade(Integer grade) {
        this.grade = grade;
    }
}
