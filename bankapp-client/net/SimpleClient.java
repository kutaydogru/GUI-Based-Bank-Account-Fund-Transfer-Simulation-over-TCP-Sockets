package net;

import java.io.*;
import java.net.Socket;

/**
 * Basic TCP client (expansion example, not strictly required in current phase).
 */
public class SimpleClient {
    public static void main(String[] args) {
        String serverIp = "127.0.0.1"; // Insert server IP here
        int port = 5000;

        try (Socket socket = new Socket(serverIp, port);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            out.println("Hello, I am the client!");
            String response = in.readLine();
            System.out.println("Server response: " + response);
        } catch (IOException e) {
            System.out.println("Unable to connect to server.");
            e.printStackTrace();
        }
    }
}