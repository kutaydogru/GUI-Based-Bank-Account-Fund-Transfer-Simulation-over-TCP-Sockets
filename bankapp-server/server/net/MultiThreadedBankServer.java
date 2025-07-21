package server.net;

import server.dao.AccountDAO;
import server.dao.UserDAO;
import server.services.AuthenticationService;
import server.services.TransactionService;

import java.net.ServerSocket;
import java.net.Socket;

public class MultiThreadedBankServer {
    public static void main(String[] args) {
        int port = 5000;
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Bank Server started on port " + port);

            // Shared DAOs and services
            UserDAO userDAO = new UserDAO();
            AccountDAO accountDAO = new AccountDAO();
            AuthenticationService authService = new AuthenticationService(userDAO);
            TransactionService txService = new TransactionService(accountDAO);

            // Accept loop
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket.getInetAddress());

                // Start a new client handler thread, share service instances
                ClientHandler handler = new ClientHandler(clientSocket, authService, txService);
                new Thread(handler).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}