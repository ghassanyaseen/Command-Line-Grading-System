package com.example.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class InstructorDAOImplement implements InstructorDAO {

    Connection connection;

    public InstructorDAOImplement(Connection connection) {
        this.connection = connection;
    }

    @Override
    public ArrayList<String> showInstructorCourses(String instructorUsername) {
        ArrayList<String> courses = new ArrayList<>();
        String sql = """
        SELECT Courses.courseName
        FROM Courses
        JOIN Users ON Courses.instructorId = Users.userId
        WHERE Users.username = ? AND Users.userType = 'Instructor'
        """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, instructorUsername);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    courses.add(resultSet.getString("courseName"));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("An error occurred while retrieving the instructor's courses.", e);
        }

        return courses;
    }

    @Override
    public String enterStudentGrades(String courseName, String studentUsername, int grade) {
        String sql = """
    INSERT INTO Grades (courseId, userId, grade)
    SELECT Courses.courseId, Users.userId, ?
    FROM Courses
    JOIN Users ON Users.username = ?
    WHERE Courses.courseName = ? AND Users.userType = 'Student'
    ON DUPLICATE KEY UPDATE grade = VALUES(grade)
""";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, grade);
            statement.setString(2, studentUsername);
            statement.setString(3, courseName);

            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                return "Grade for student '" + studentUsername + "' in course '" + courseName + "' updated successfully.";
            } else {
                return "Failed to update grade. Check if the student or course exists.";
            }

        } catch (SQLException e) {
            throw new RuntimeException("An error occurred while entering student grades. Please try again later.", e);
        }
    }


    @Override
    public ArrayList<String> showStudentsInTheCourse(String courseName, String instructorUsername) {
        ArrayList<String> result = new ArrayList<>();
        String sql = """
    SELECT Users.username, Grades.grade
    FROM Grades
    JOIN Users ON Grades.userId = Users.userId
    JOIN Courses ON Grades.courseId = Courses.courseId
    JOIN Users AS Instructor ON Courses.instructorId = Instructor.userId
    WHERE Courses.courseName = ? 
    AND Instructor.username = ? 
    AND Instructor.userType = 'Instructor'
""";


        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, courseName);
            statement.setString(2, instructorUsername);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.isBeforeFirst()) {

                    result.add("No students found for the course or you are not authorized to view this course.");
                    return result;
                }


                while (resultSet.next()) {
                    String studentUsername = resultSet.getString("username");
                    int grade = resultSet.getInt("grade");

                    if (resultSet.wasNull()) {
                        result.add("Student: " + studentUsername + ", Grade: Not assigned yet");
                    } else {
                        result.add("Student: " + studentUsername + ", Grade: " + grade);
                    }
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("An error occurred while retrieving the course students and grades.", e);
        }

        return result;
    }


}
