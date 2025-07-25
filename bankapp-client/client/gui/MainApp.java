package client.gui;

import client.net.ClientNetworkManager;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import java.util.Optional;
import java.util.prefs.Preferences;

/**
 * Main application class that handles initialization,
 * server connection, and starts the application UI.
 */
public class MainApp extends Application {
    public static ClientNetworkManager network;

    // OrangePi server address
    private static final String DEFAULT_SERVER_IP = "192.168.137.179";
    private static final int DEFAULT_SERVER_PORT = 5000;

    private Preferences prefs = Preferences.userNodeForPackage(MainApp.class);

    @Override
    public void start(Stage primaryStage) {
        try {
            // Load last used server details
            String serverIp = prefs.get("server_ip", DEFAULT_SERVER_IP);
            int serverPort = prefs.getInt("server_port", DEFAULT_SERVER_PORT);

            connectToServer(primaryStage, serverIp, serverPort);
        } catch (Exception e) {
            showErrorDialog("Application Error", "An error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Attempts to connect to the specified server
     * @param primaryStage The main application stage
     * @param serverIp IP address or hostname of the server
     * @param serverPort Port number of the server
     */
    private void connectToServer(Stage primaryStage, String serverIp, int serverPort) {
        network = new ClientNetworkManager();

        System.out.println("Connecting to " + serverIp + ":" + serverPort + "...");
        boolean connected = network.connect(serverIp, serverPort);

        if (connected) {
            System.out.println("Connected successfully to " + serverIp + ":" + serverPort);

            // Read and process the welcome message from the server
            try {
                String welcomeMsg = network.readLine();
                System.out.println("Server welcome message: " + welcomeMsg);
            } catch (Exception ex) {
                System.err.println("Error reading initial welcome message: " + ex.getMessage());
                // Continue even if we can't read the welcome message
            }

            new LoginWindow(primaryStage).show();
        } else {
            // Connection error
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Server Connection Error");
            alert.setHeaderText("Cannot connect to server at " + serverIp + ":" + serverPort);
            alert.setContentText("Choose an option:");

            // Apply dark theme to the alert
            DialogPane dialogPane = alert.getDialogPane();
            dialogPane.setStyle("-fx-background-color: " + Theme.PANEL_BG + ";" +
                    "-fx-text-fill: " + Theme.FG + ";");

            ButtonType btnConfig = new ButtonType("Configure Server");
            ButtonType btnTest = new ButtonType("Test Mode");
            ButtonType btnExit = new ButtonType("Exit");

            alert.getButtonTypes().setAll(btnConfig, btnTest, btnExit);

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent()) {
                if (result.get() == btnConfig) {
                    promptForServerAddress(primaryStage);
                }
                else if (result.get() == btnTest) {
                    // Continue in test mode - just showing UI
                    new MainMenuWindow(primaryStage, "testuser").show();
                }
                else {
                    System.exit(0);
                }
            }
        }
    }

    /**
     * Shows a dialog for configuring server connection settings
     */
    private void promptForServerAddress(Stage primaryStage) {
        // Server configuration dialog
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Server Configuration");
        dialog.setHeaderText("Enter OrangePi server details:");

        // Apply dark theme to dialog
        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.setStyle("-fx-background-color: " + Theme.PANEL_BG + ";" +
                "-fx-text-fill: " + Theme.FG + ";");

        ButtonType connectButtonType = new ButtonType("Connect", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(connectButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField ipField = new TextField(prefs.get("server_ip", DEFAULT_SERVER_IP));
        ipField.setPromptText("Hostname or IP address");
        ipField.setStyle(Theme.TXT_FIELD);

        TextField portField = new TextField(String.valueOf(prefs.getInt("server_port", DEFAULT_SERVER_PORT)));
        portField.setPromptText("Port number");
        portField.setStyle(Theme.TXT_FIELD);

        Label ipLabel = new Label("Server Address:");
        ipLabel.setStyle("-fx-text-fill: " + Theme.FG + ";");

        Label portLabel = new Label("Port:");
        portLabel.setStyle("-fx-text-fill: " + Theme.FG + ";");

        grid.add(ipLabel, 0, 0);
        grid.add(ipField, 1, 0);
        grid.add(portLabel, 0, 1);
        grid.add(portField, 1, 1);

        dialog.getDialogPane().setContent(grid);

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == connectButtonType) {
            String serverIp = ipField.getText().trim();

            try {
                int serverPort = Integer.parseInt(portField.getText().trim());

                // Save settings
                prefs.put("server_ip", serverIp);
                prefs.putInt("server_port", serverPort);

                // Try to connect
                connectToServer(primaryStage, serverIp, serverPort);

            } catch (NumberFormatException e) {
                showErrorDialog("Invalid Port", "Please enter a valid port number");
                promptForServerAddress(primaryStage);
            }
        } else {
            // If cancelled, offer test mode or exit
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Setup Cancelled");
            alert.setHeaderText("Server setup was cancelled");
            alert.setContentText("Would you like to continue in TEST MODE or exit?");

            // Apply dark theme
            DialogPane alertPane = alert.getDialogPane();
            alertPane.setStyle("-fx-background-color: " + Theme.PANEL_BG + ";" +
                    "-fx-text-fill: " + Theme.FG + ";");

            ButtonType btnTest = new ButtonType("Test Mode");
            ButtonType btnExit = new ButtonType("Exit");

            alert.getButtonTypes().setAll(btnTest, btnExit);

            Optional<ButtonType> choice = alert.showAndWait();
            if (choice.isPresent() && choice.get() == btnTest) {
                new MainMenuWindow(primaryStage, "testuser").show();
            } else {
                System.exit(0);
            }
        }
    }

    /**
     * Shows an error dialog with dark theme
     */
    private void showErrorDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        // Apply dark theme
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setStyle("-fx-background-color: " + Theme.PANEL_BG + ";" +
                "-fx-text-fill: " + Theme.FG + ";");

        alert.showAndWait();
    }

    @Override
    public void stop() {
        if (network != null) {
            network.close();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}