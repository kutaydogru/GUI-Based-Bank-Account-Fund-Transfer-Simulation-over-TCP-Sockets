package client.net;

import java.io.*;
import java.net.Socket;

public class ClientNetworkManager {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    public boolean connect(String host, int port) {
        try {
            socket = new Socket(host, port);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            return true;
        } catch (IOException e) {
            System.out.println("Connection failed: " + e.getMessage());
            return false;
        }
    }
    public String readLine() throws IOException {
        return in.readLine();
    }
    public void sendLine(String line) {
        out.println(line);
    }
    public void close() {
        try {
            if (socket != null) socket.close();
        } catch (IOException ignored) {}
    }
}