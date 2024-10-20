package com.example;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

public class Client {

    public static void main(String[] args) throws IOException {
        String serverAddress = "localhost";
        int serverPort = 888;
        try {
            Socket socket = new Socket(serverAddress, serverPort);
            System.out.println("Connected to the server!");

            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader consoleInput = new BufferedReader(new InputStreamReader(System.in));

            String serverResponse;
            while ((serverResponse = input.readLine()) != null) {
                System.out.println(serverResponse);


                if (serverResponse.contains("Enter")) {
                    String userInput = consoleInput.readLine();
                    output.println(userInput);
                }

                if (serverResponse.contains("Welcome") || serverResponse.contains("Authentication successful")) {
                    System.out.println(input.readLine());
                    break;
                }
            }

            String userInput;

            String line;

            String userType = input.readLine();

            if (userType.equalsIgnoreCase("Student")) {

                //student
                while (true) {

                    while ((line = input.readLine()) != null) {
                        if (line.equals("-------------------------------------------------------------")) {
                            break;
                        }
                        System.out.println(line);
                    }

                    userInput = consoleInput.readLine();
                    output.println(userInput);
                    output.flush();

                    switch (userInput) {
                        case "1":
                            String gradesStr = input.readLine();
                            ArrayList<String> grades = new ArrayList<>(Arrays.asList(gradesStr.split(",")));
                            for (String grade : grades) {
                                System.out.println(grade);
                            }
                            break;

                        case "2":
                            System.out.println(input.readLine());
                            break;

                        case "3":
                            System.out.println("Exiting...");
                            socket.close();
                            return;


                        default:
                            System.out.println("Invalid option. Please enter a number between 1 and 3.");
                            break;
                    }
                }
            } else if (userType.equalsIgnoreCase("Instructor")) {


                //Instructor
                while (true) {

                    while ((line = input.readLine()) != null) {
                        if (line.contains("----------------------------------")) {
                            break;
                        }
                        System.out.println(line);
                    }


                    userInput = consoleInput.readLine();
                    output.println(userInput);
                    output.flush();




                    switch (userInput) {
                        case "1":
                            try {
                                ObjectInputStream receivedCourses = new ObjectInputStream(socket.getInputStream());
                                ArrayList<String> courses = (ArrayList<String>) receivedCourses.readObject();
                                for (String course : courses) {
                                    System.out.println(course);
                                }
                            } catch (IOException | ClassNotFoundException e) {
                                e.printStackTrace();
                            }
                            break;

                        case "2":
                            System.out.println(input.readLine());
                            String usertype = consoleInput.readLine();
                            output.println(usertype);
                            try {
                                ObjectInputStream receivedCourses = new ObjectInputStream(socket.getInputStream());
                                ArrayList<String> courses = (ArrayList<String>) receivedCourses.readObject();
                                for (String course : courses) {
                                    System.out.println(course);
                                }
                            } catch (IOException | ClassNotFoundException e) {
                                e.printStackTrace();
                            }
                            break;

                        case "3":
                            System.out.println(input.readLine());
                            String courseName = consoleInput.readLine();
                            output.println(courseName);

                            try {
                                ObjectInputStream receivedCourses = new ObjectInputStream(socket.getInputStream());
                                ArrayList<String> students = (ArrayList<String>) receivedCourses.readObject();
                                for (String student : students) {
                                    System.out.println(student);
                                }
                            } catch (IOException | ClassNotFoundException e) {
                                e.printStackTrace();
                            }

                            while (true) {

                                System.out.println(input.readLine());
                                String studentUsername = consoleInput.readLine();
                                output.println(studentUsername);

                                System.out.println(input.readLine());
                                String grade = consoleInput.readLine();
                                output.println(grade);

                                System.out.println(input.readLine());

                                System.out.println(input.readLine());
                                String anotherGrade = consoleInput.readLine();
                                output.println(anotherGrade);

                                if (anotherGrade.equalsIgnoreCase("N")) {
                                    break;
                                }
                            }

                            break;


                        case "4":
                            System.out.println("Exiting...");
                            socket.close();
                            return;

                        default:
                            System.out.println("Invalid option. Please enter a number between 1 and 3.");
                            break;
                    }

                }
            } else {
                //Admin
                String readData;

                while (true) {

                    while ((line = input.readLine()) != null) {
                        if (line.contains("----------------------------------")) {
                            break;
                        }
                        System.out.println(line);
                    }


                    userInput = consoleInput.readLine();
                    output.println(userInput);
                    output.flush();


                    switch (userInput) {
                        case "1", "2","3":
                            while (true) {
                                readData = input.readLine();
                                if (readData.contains("Enter")) {
                                    System.out.println(readData);
                                    String usertype = consoleInput.readLine();
                                    output.println(usertype);

                                } else {
                                    break;
                                }
                            }
                            System.out.println(readData);
                            break;

                        case "4":
                            System.out.println("Exiting...");
                            socket.close();
                            return;

                        default:
                            System.out.println("Invalid option. Please enter a number between 1 and 3.");
                            break;
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

