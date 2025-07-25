package client.net;

import java.io.*;
import java.net.Socket;

/**
 * Handles network communication between the client and server.
 * Manages socket connections, sending commands and reading responses.
 */
public class ClientNetworkManager {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    /**
     * Attempts to connect to the server at the specified host and port
     *
     * @param host Hostname or IP address of the server
     * @param port Port number the server is listening on
     * @return true if connection is successful, false otherwise
     */
    public boolean connect(String host, int port) {
        try {
            System.out.println("Attempting to connect to server at " + host + ":" + port + "...");
            socket = new Socket(host, port);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            System.out.println("Connected successfully to server at " + host + ":" + port);
            return true;
        } catch (Exception e) {
            System.out.println("Connection failed: " + e.getMessage());
            return false;
        }
    }

    /**
     * Reads a line of text from the server
     *
     * @return The line read from the server
     * @throws IOException if an I/O error occurs
     */
    public String readLine() throws IOException {
        return in.readLine();
    }

    /**
     * Sends a line of text to the server
     *
     * @param line The text to send to the server
     */
    public void sendLine(String line) {
        out.println(line);
    }

    /**
     * Closes the connection to the server
     */
    public void close() {
        try {
            if (socket != null) socket.close();
        } catch (IOException ignored) {}
    }
}