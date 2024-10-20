package com.example.dao;

import java.sql.SQLException;
import java.util.ArrayList;

public interface StudentDAO {

    public ArrayList<String> showGrades(String studentName) throws SQLException;

    public String showGPA(String name) throws SQLException;

}
