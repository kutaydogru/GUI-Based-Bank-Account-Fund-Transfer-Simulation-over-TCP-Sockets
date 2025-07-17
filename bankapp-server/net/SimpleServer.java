package net;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class SimpleServer {
    public static void main(String[] args) {
        int port = 5000;

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Sunucu başlatıldı. Port: " + port);

            Socket clientSocket = serverSocket.accept();
            System.out.println("Bir istemci bağlandı: " + clientSocket.getInetAddress());

            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

            String received = in.readLine();
            System.out.println("İstemciden gelen mesaj: " + received);
            out.println("Hoş Geldin! (Sunucudan cevap)");

            clientSocket.close();
            System.out.println("Bağlantı sonlandırıldı.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}