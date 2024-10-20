package com.example;

import com.example.dao.*;
import com.example.service.*;

import java.io.*;
import java.net.Socket;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

public class ClientHandlerServer extends Thread {

    private final Socket clientSocket;
    private final Connection dbConnection;
    private BufferedReader bufferReader;
    private PrintWriter outputStream;
    String username = "";
    String userType = "";

    public ClientHandlerServer(Socket clientSocket, Connection dbConnection) {
        this.clientSocket = clientSocket;
        this.dbConnection = dbConnection;
    }

    @Override
    public void run() {
        try {
            bufferReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            outputStream = new PrintWriter(clientSocket.getOutputStream(), true);

            LoginPage(bufferReader, outputStream);

            outputStream.println(userType);
            outputStream.flush();


            if (userType.equalsIgnoreCase("Admin")) {
                AdminInterFace(bufferReader, outputStream);
            } else if (userType.equalsIgnoreCase("Instructor")) {
                InstructorInterFace(bufferReader, outputStream, username);
            } else {
                StudentInterFace(bufferReader, outputStream,username);
            }

            clientSocket.close();

        } catch (IOException ex) {
            System.out.println("Server exception: " + ex.getMessage());
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void LoginPage(BufferedReader bufferReader, PrintWriter outputStream) throws IOException, SQLException {

        String password;
        boolean isAuthenticated = false;

        UserAuthenticationService userAuthenticationService = new UserAuthenticationServiceImplementation(dbConnection);

        do {
            outputStream.println("Enter UserName: ");
            outputStream.flush();
            username = bufferReader.readLine();

            outputStream.println("Enter Password: ");
            outputStream.flush();
            password = bufferReader.readLine();

            isAuthenticated = userAuthenticationService.authenticateUser(username, password);

            if (!isAuthenticated) {
                outputStream.println("Invalid credentials. Please try again.");
                outputStream.flush();
            }

        } while (!isAuthenticated);

        outputStream.println("Authentication successful! Welcome, " + username + ".");
        outputStream.flush();

        userType = userAuthenticationService.getUserType(username);
        outputStream.println("Hello " + username + ", you are logged in as: " + userType);
        outputStream.flush();

    }

    public void AdminInterFace(BufferedReader bufferedReader, PrintWriter out) throws IOException, SQLException, ClassNotFoundException {
        AdminDAO admin = new AdminDAOImplement(dbConnection);

        while (true) {
            out.println(
            "Welcome to The Registration System - Admins \n " +
            "1- Add new user \n " +
            "2- Add course \n " +
            "3- enroll students to the courses \n " +
            "4- Exit \n " +
            "Enter a number between <1-3> about what you want to do \n " +
            "-------------------------------------------------------------");
            out.flush();

            String option = bufferedReader.readLine();

            switch (option) {
                case "1":
                    out.println("Enter the usertype:");
                    out.flush();
                    String usertype = bufferedReader.readLine();

                    out.println("Enter the username:");
                    out.flush();
                    String username = bufferedReader.readLine();

                    out.println("Enter the password:");
                    out.flush();
                    String password = bufferedReader.readLine();

                    String insertingState = admin.createUser(username, password, usertype);
                    out.println(insertingState);
                    out.flush();
                    break;

                case "2":
                    out.println("Enter the course name:");
                    out.flush();
                    String courseName = bufferedReader.readLine();

                    out.println("Enter the Instructor name:");
                    out.flush();
                    String instructorName = bufferedReader.readLine();

                    insertingState = admin.createCourse(courseName, instructorName);
                    out.println(insertingState);
                    out.flush();
                    break;

                case "3":
                    out.println("Enter the course name:");
                    out.flush();
                    courseName = bufferedReader.readLine();

                    out.println("Enter the student name:");
                    out.flush();
                    String studentUsername = bufferedReader.readLine();

                    insertingState = admin.enrollStudentInTheCourse(studentUsername,courseName);
                    out.println(insertingState);
                    out.flush();
                    break;

                case "4":
                    out.println("Exiting...");
                    out.flush();
                    return;

                default:
                    out.println("Invalid option. Please enter a number between 1 and 3.");
                    out.flush();
                    break;
            }
        }
    }

    public void InstructorInterFace(BufferedReader bufferedReader, PrintWriter out, String username) throws IOException, SQLException, ClassNotFoundException {
        InstructorDAO instructor = new InstructorDAOImplement(dbConnection);

        while (true) {
            out.println(
            "Welcome to our educational system - Instructors \n " +
            "1- Show your Courses \n " +
            "2- Show Students in the Course \n " +
            "3- Enter Student Grades \n " +
            "4- Exit \n " +
            "-------------------------------------------------------------");
            out.flush();

            String option = bufferedReader.readLine();

            switch (option) {
                case "1":
                    ArrayList<String> courses = instructor.showInstructorCourses(username);
                    try {
                        ObjectOutputStream sendCourses = new ObjectOutputStream(clientSocket.getOutputStream());
                        sendCourses.writeObject(courses);
                        System.out.println("Option received: " + courses.toString());

                        sendCourses.flush();
                        sendCourses.reset();
                    } catch (IOException e) {
                        System.out.println("An error occurred while processing the request.");
                    }
                    break;

                case "2":
                    out.println("Enter the course name to view students:");
                    out.flush();
                    String courseChooseName = bufferedReader.readLine();

                    ArrayList<String> studentsList = instructor.showStudentsInTheCourse(courseChooseName, username);
                    try {
                        ObjectOutputStream sendCourses = new ObjectOutputStream(clientSocket.getOutputStream());
                        sendCourses.writeObject(studentsList);

                        sendCourses.flush();
                        sendCourses.reset();
                    } catch (IOException e) {
                        System.out.println("An error occurred while processing the request.");
                    }
                    break;

                case "3":
                    out.println("Enter the course name to enter student grades:");
                    out.flush();
                    courseChooseName = bufferedReader.readLine();

                    studentsList = instructor.showStudentsInTheCourse(courseChooseName, username);
                    try {
                        ObjectOutputStream sendCourses = new ObjectOutputStream(clientSocket.getOutputStream());
                        sendCourses.writeObject(studentsList);
                        sendCourses.flush();
                        sendCourses.reset();
                    } catch (IOException e) {
                        System.out.println("An error occurred while processing the request.");
                    }

                    while (true) {
                        out.println("Enter the student name to enter his/her grade:");
                        out.flush();
                        String studentUsername = bufferedReader.readLine();

                        out.println("Enter the grade:");
                        out.flush();
                        int grade = Integer.parseInt(bufferedReader.readLine());

                        String result = instructor.enterStudentGrades(courseChooseName, studentUsername, grade);
                        out.println(result);
                        out.flush();

                        out.println("Do you want to enter another grade? (Y/N)");
                        out.flush();
                        String response = bufferedReader.readLine();
                        if (response.equalsIgnoreCase("N")) {
                            break;
                        }
                    }
                    break;

                case "4":
                    out.println("Exiting...");
                    out.flush();
                    return;

                default:
                    out.println("Invalid option. Please enter a number between 1 and 4.");
                    out.flush();
                    break;
            }
        }
    }

    public void StudentInterFace(BufferedReader bufferedReader, PrintWriter out, String username) throws IOException, SQLException {
        StudentDAO student = new StudentDAOImplement(dbConnection);
        while (true) {
            out.println("Welcome to our grading system - Students");
            out.println("1- Show your grades");
            out.println("2- Show your GPA");
            out.println("3- Exit");
            out.println("-------------------------------------------------------------");
            out.flush();

            String option = bufferedReader.readLine();
            switch (option) {
                case "1":

                    ArrayList<String> grades = student.showGrades(username);
                    String gradesStr = String.join(",", grades);
                    out.println(gradesStr);
                    out.flush();
                    break;


                case "2":
                    String GPA = student.showGPA(username);
                    out.println(GPA);
                    out.flush();
                    break;

                case "3":
                    return;

                default:
                    out.println("Invalid option. Please enter 1, 2, or 3.");
                    out.flush();
                    break;
            }
        }
    }

}
