package com.example.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class StudentDAOImplement implements StudentDAO {

    Connection connection;

    public StudentDAOImplement(Connection connection) {
        this.connection = connection;
    }


    public ArrayList<String> showGrades(String studentUsername) throws SQLException {
        ArrayList<String> gradesList = new ArrayList<>();
        String sql = """
    SELECT course.courseName, Grades.grade
    FROM Grades
    JOIN Courses AS course ON Grades.courseId = course.courseId
    JOIN Users AS student ON Grades.userId = student.userId
    WHERE student.username = ?
    """;

        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            statement = connection.prepareStatement(sql);
            statement.setString(1, studentUsername);

            // Execute the query
            resultSet = statement.executeQuery();

            if (!resultSet.isBeforeFirst()) {
                gradesList.add("No grades found for student: " + studentUsername + ".");
                return gradesList;
            }

            while (resultSet.next()) {
                String courseName = resultSet.getString("courseName");
                BigDecimal grade = resultSet.getBigDecimal("grade");

                if (grade == null) {
                    gradesList.add("Course: " + courseName + ", Grade: Not assigned yet");
                } else {
                    gradesList.add("Course: " + courseName + ", Grade: " + grade.doubleValue());
                }
            }
        } finally {

            if (resultSet != null) {
                resultSet.close();
            }
            if (statement != null) {
                statement.close();
            }
        }

        return gradesList;
    }

    @Override
    public String showGPA(String studentUsername) throws SQLException {
        double totalGrades = 0;
        int gradeCount = 0;
        String sql = """
        SELECT Grades.grade
        FROM Grades
        JOIN Users AS student ON Grades.userId = student.userId
        WHERE student.username = ?
    """;

        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            statement = connection.prepareStatement(sql);
            statement.setString(1, studentUsername);
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Double grade = resultSet.getDouble("grade");

                if (!resultSet.wasNull()) {
                    totalGrades += grade;
                    gradeCount++;
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("An error occurred while calculating the GPA.", e);
        } finally {
            if (resultSet != null) {
                resultSet.close();
            }
            if (statement != null) {
                statement.close();
            }
        }

        if (gradeCount == 0) {
            return "No grades found for student: " + studentUsername + ".";
        }

        double average = totalGrades / gradeCount;
        return "The GPA for " + studentUsername + " is: " + String.format("%.2f", average);
    }
}
