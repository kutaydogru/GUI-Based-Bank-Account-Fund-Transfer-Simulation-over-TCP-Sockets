package client.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.json.JSONObject;

/**
 * Login screen for user authentication.
 * Handles user login with server validation.
 */
public class LoginWindow {
    private final Stage stage;

    public LoginWindow(Stage stage) {
        this.stage = stage;
    }

    public void show() {
        // Center login form
        VBox loginBox = new VBox(15);
        loginBox.setAlignment(Pos.CENTER);
        loginBox.setMaxWidth(380);
        loginBox.setStyle("-fx-background-color: " + Theme.PANEL_BG + "; -fx-border-color: " + Theme.BORDER_COLOR +
                "; -fx-border-radius: 5; -fx-padding: 30;");

        // Logo/App title
        Label logoLabel = new Label("Banking Application");
        logoLabel.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #89CFF0;"); // Light blue logo

        // Login title
        Label titleLabel = new Label("Login to Your Account");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: " + Theme.FG + "; -fx-padding: 0 0 10 0;");

        // Username field
        Label lblUser = new Label("Username:");
        lblUser.setStyle("-fx-text-fill: " + Theme.FG + ";");
        TextField txtUser = new TextField();
        txtUser.setStyle(Theme.TXT_FIELD);
        txtUser.setPromptText("Enter your username");

        // Password field
        Label lblPass = new Label("Password:");
        lblPass.setStyle("-fx-text-fill: " + Theme.FG + ";");
        PasswordField txtPass = new PasswordField();
        txtPass.setStyle(Theme.TXT_FIELD);
        txtPass.setPromptText("Enter your password");

        // Login button
        Button btnLogin = new Button("Login");
        btnLogin.setStyle(Theme.PRIMARY_BTN);
        btnLogin.setPrefWidth(320);

        // Register link
        HBox registerBox = new HBox();
        registerBox.setAlignment(Pos.CENTER);
        Label lblRegister = new Label("Don't have an account? ");
        lblRegister.setStyle("-fx-text-fill: " + Theme.FG + ";");
        Button btnRegister = new Button("Register");
        btnRegister.setStyle("-fx-background-color: transparent; -fx-text-fill: #62aaff; -fx-underline: true; -fx-cursor: hand;");
        registerBox.getChildren().addAll(lblRegister, btnRegister);

        // Message label for error/status
        Label lblMsg = new Label();
        lblMsg.setWrapText(true);
        lblMsg.setMaxWidth(320);

        loginBox.getChildren().addAll(logoLabel, titleLabel, lblUser, txtUser, lblPass, txtPass, btnLogin, registerBox, lblMsg);

        // Login button action
        btnLogin.setOnAction(e -> {
            String user = txtUser.getText().trim();
            String pass = txtPass.getText();
            lblMsg.setText(""); // Clear previous messages

            if (user.isEmpty() || pass.isEmpty()) {
                lblMsg.setStyle(Theme.ERROR);
                lblMsg.setText("Please enter both username and password");
                return;
            }

            // Disable the button to prevent multiple clicks
            btnLogin.setDisable(true);
            lblMsg.setStyle("-fx-text-fill: #89CFF0;"); // Light blue processing message
            lblMsg.setText("Connecting to server...");

            String cmd = "{\"command\":\"LOGIN\",\"username\":\"" + user + "\",\"password\":\"" + pass + "\"}";
            System.out.println("Sending login command: " + cmd);
            MainApp.network.sendLine(cmd);

            String response = null;
            try {
                response = MainApp.network.readLine();
                System.out.println("Server response: " + response); // Debug output
            } catch (Exception ex) {
                lblMsg.setStyle(Theme.ERROR);
                lblMsg.setText("Connection error: " + ex.getMessage());
                btnLogin.setDisable(false);
                return;
            }

            if (response != null) {
                try {
                    JSONObject json = new JSONObject(response);
                    if ("OK".equals(json.optString("status"))) {
                        lblMsg.setStyle(Theme.SUCCESS);
                        lblMsg.setText("Login successful!");

                        // Redirect directly to main menu window (dashboard)
                        new MainMenuWindow(stage, user).show();
                    } else {
                        lblMsg.setStyle(Theme.ERROR);
                        String errorMsg = json.optString("error", "Unknown error");

                        // User-friendly error messages
                        switch (errorMsg) {
                            case "notfound":
                                lblMsg.setText("Username not found. Please check and try again.");
                                break;
                            case "wrongpass":
                                lblMsg.setText("Incorrect password. Please try again.");
                                break;
                            case "alreadyloggedin":
                                lblMsg.setText("User is already logged in from another device.");
                                break;
                            default:
                                lblMsg.setText("Login failed: " + errorMsg);
                        }
                        btnLogin.setDisable(false);
                    }
                } catch (Exception ex) {
                    // JSON parsing error
                    lblMsg.setStyle(Theme.ERROR);
                    lblMsg.setText("Error processing server response. Please try again.");
                    System.err.println("JSON parse error: " + ex.getMessage() + " - Response: " + response);
                    btnLogin.setDisable(false);
                }
            } else {
                lblMsg.setStyle(Theme.ERROR);
                lblMsg.setText("No response from server");
                btnLogin.setDisable(false);
            }
        });

        btnRegister.setOnAction(e -> new RegisterWindow(stage).show());

        // Enable login on Enter key press
        txtPass.setOnAction(e -> btnLogin.fire());

        // Main container with background
        StackPane rootPane = new StackPane();
        rootPane.setStyle("-fx-background-color: " + Theme.BG + ";");
        rootPane.getChildren().add(loginBox);

        Scene scene = new Scene(rootPane, 800, 600);
        stage.setTitle("Banking Application - Login");
        stage.setScene(scene);
        stage.show();
    }
}