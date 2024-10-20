package com.example;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.SQLException;

public class Server {

    public static void main(String[] args) {

        Connection dbConnection = null;
        ServerSocket serverSocket = null;
        DataBaseManager dbManager = new DataBaseManager();

        try {


            dbConnection = dbManager.getConnection();

            serverSocket = new ServerSocket(888);
            System.out.println("Server started and waiting for client connections...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected!");


                ClientHandlerServer clientHandler = new ClientHandlerServer(clientSocket, dbConnection);
                new Thread(clientHandler).start();
            }

        } catch (Exception e) {
            System.err.println("error: " + e.getMessage());
        } finally {

            try {
                if (dbConnection != null) {
                    dbConnection.close();
                    System.out.println("Database connection closed.");
                }
                if (serverSocket != null) {
                    serverSocket.close();
                    System.out.println("Server socket closed.");
                }
            } catch (SQLException | IOException e) {
                System.err.println("IO error: " + e.getMessage());
            }
        }
    }
}
