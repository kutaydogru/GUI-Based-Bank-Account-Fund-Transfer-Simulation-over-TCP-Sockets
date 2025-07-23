package client.gui;

import client.net.ClientNetworkManager;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Main application class for BankApp client.
 * Connects to the server once at startup and keeps the connection open for the entire session.
 */
public class MainApp extends Application {

    // Shared static network client for all GUI classes
    public static ClientNetworkManager network = new ClientNetworkManager();

    @Override
    public void start(Stage primaryStage) {
        // Try to connect to server at launch
        boolean connected = network.connect("192.168.137.179", 5000);
        if (!connected) {
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                    javafx.scene.control.Alert.AlertType.ERROR,
                    "Cannot connect to server! Exiting..."
            );
            alert.showAndWait();
            System.exit(1);
            return;
        }
        // IMPORTANT: Read and ignore the initial welcome message from the server,
        // so that all future reads will get only true server responses
        try {
            String serverGreeting = network.readLine();
            System.out.println("Server says: " + serverGreeting);
        } catch (Exception ignored) {}
        new HomeWindow(primaryStage).show();
    }

    @Override
    public void stop() {
        // Called when the application is closing, to clean up resources
        if (network != null) {
            network.close();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}