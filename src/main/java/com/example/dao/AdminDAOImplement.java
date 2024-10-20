package com.example.dao;

import com.example.model.User;
import com.example.service.HashPasswordService;
import com.example.service.selectUserTypeServiceImplement;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AdminDAOImplement implements AdminDAO {

    private final Connection connection;
    private final HashPasswordService hashPasswordService = new HashPasswordService();

    public AdminDAOImplement(Connection connection) {
        this.connection = connection;
    }

    @Override
    public String createUser(String username, String password, String userType) {
        boolean userUpdate = false;

        User user = selectUserTypeServiceImplement.selectUserType(username, password, userType);

        if (user == null) {
            return "User could not be created due to invalid type.";
        }

        try {
            // Generate salt and hash the password
            String salt = hashPasswordService.generateSalt();
            String hashedPassword = hashPasswordService.hashPassword(password, salt);

            String sql = "INSERT INTO Users (username, password, salt, userType) VALUES (?, ?, ?, ?)";

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, user.getName());
                statement.setString(2, hashedPassword); // Save hashed password
                statement.setString(3, salt); // Save the salt
                statement.setString(4, user.getUserType().toString());

                int rowsAffected = statement.executeUpdate();

                if (rowsAffected > 0) {
                    userUpdate = true;
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("An error occurred while inserting a new user.", e);
        }

        if (!userUpdate) {
            return "An error occurred while inserting a new user.";
        } else {
            return user.toString();
        }
    }

    @Override
    public String createCourse(String courseName, String instructorUsername) {
        String insertCourseQuery = """
        INSERT INTO Courses (courseName, instructorId)
        SELECT ?, instructor.userId
        FROM Users AS instructor
        WHERE instructor.username = ? AND instructor.userType = 'Instructor'
        """;

        try (PreparedStatement statement = connection.prepareStatement(insertCourseQuery)) {
            statement.setString(1, courseName);
            statement.setString(2, instructorUsername);

            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                return "Course '" + courseName + "' created successfully with instructor '" + instructorUsername + "'.";
            } else {
                return "The instructor '" + instructorUsername + "' is not valid or does not exist.";
            }

        } catch (SQLException e) {
            throw new RuntimeException("An error occurred while creating the course. Please try again later.", e);
        }
    }

    @Override
    public String enrollStudentInTheCourse(String studentUsername, String courseName) {
        String sql = """
        INSERT INTO Grades (userId, courseId)
        SELECT student.userId, course.courseId
        FROM Users AS student
        JOIN Courses AS course ON course.courseName = ?
        WHERE student.username = ? AND student.userType = 'STUDENT'
    """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, courseName);
            statement.setString(2, studentUsername);

            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                return "Student '" + studentUsername + "' has been successfully enrolled in the course '" + courseName + "'.";
            } else {
                return "Enrollment failed. The student or course might not exist.";
            }

        } catch (SQLException e) {
            System.err.println("Error while enrolling student: " + e.getMessage());
            throw new RuntimeException("An error occurred while enrolling the student in the course. Please try again later.", e);
        }
    }


    public List<String> getAllInstructors() {
        List<String> instructors = new ArrayList<>();
        String sql = "SELECT username FROM Users WHERE userType = 'Instructor'";

        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                instructors.add(resultSet.getString("username"));
            }

        } catch (SQLException e) {
            throw new RuntimeException("An error occurred while fetching instructors.", e);
        }

        return instructors;
    }


    public List<String> getAllStudents() {
        List<String> students = new ArrayList<>();
        String sql = "SELECT username FROM Users WHERE userType = 'Student'";

        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                students.add(resultSet.getString("username"));
            }

        } catch (SQLException e) {
            throw new RuntimeException("An error occurred while fetching students.", e);
        }

        return students;
    }


    public List<String> getAllCourses() {
        List<String> courses = new ArrayList<>();
        String sql = "SELECT courseName FROM Courses";

        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                courses.add(resultSet.getString("courseName"));
            }

        } catch (SQLException e) {
            throw new RuntimeException("An error occurred while fetching courses.", e);
        }

        return courses;
    }
}
