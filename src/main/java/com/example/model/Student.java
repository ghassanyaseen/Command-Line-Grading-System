package com.example.model;

public class Student extends User {

    public Student(String name, String password) {
        super(name, password, UserType.STUDENT);
    }

    @Override
    public String toString(){
        return "Added Student Name = " + this.getName();
    }

}
