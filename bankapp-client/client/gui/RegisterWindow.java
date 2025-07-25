package client.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.json.JSONObject;

/**
 * Registration screen for new user signup.
 * Handles creation of new user accounts.
 */
public class RegisterWindow {
    private final Stage stage;

    public RegisterWindow(Stage stage) {
        this.stage = stage;
    }

    public void show() {
        // Registration form container
        VBox registerBox = new VBox(15);
        registerBox.setAlignment(Pos.CENTER);
        registerBox.setMaxWidth(380);
        registerBox.setStyle("-fx-background-color: " + Theme.PANEL_BG + "; -fx-border-color: " + Theme.BORDER_COLOR +
                "; -fx-border-radius: 5; -fx-padding: 30;");

        // App title
        Label logoLabel = new Label("Banking Application");
        logoLabel.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #89CFF0;");

        // Registration title
        Label titleLabel = new Label("Create New Account");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: " + Theme.FG + "; -fx-padding: 0 0 10 0;");

        // Username field
        Label lblUser = new Label("Username:");
        lblUser.setStyle("-fx-text-fill: " + Theme.FG + ";");
        TextField txtUser = new TextField();
        txtUser.setStyle(Theme.TXT_FIELD);
        txtUser.setPromptText("Choose a username");

        // Password field
        Label lblPass = new Label("Password:");
        lblPass.setStyle("-fx-text-fill: " + Theme.FG + ";");
        PasswordField txtPass = new PasswordField();
        txtPass.setStyle(Theme.TXT_FIELD);
        txtPass.setPromptText("Create a password");

        // Full name field
        Label lblFullName = new Label("Full Name:");
        lblFullName.setStyle("-fx-text-fill: " + Theme.FG + ";");
        TextField txtFullName = new TextField();
        txtFullName.setStyle(Theme.TXT_FIELD);
        txtFullName.setPromptText("Enter your full name");

        // Register button
        Button btnRegister = new Button("Register");
        btnRegister.setStyle(Theme.SUCCESS_BTN);
        btnRegister.setPrefWidth(320);

        // Login link
        HBox loginBox = new HBox();
        loginBox.setAlignment(Pos.CENTER);
        Label lblLogin = new Label("Already have an account? ");
        lblLogin.setStyle("-fx-text-fill: " + Theme.FG + ";");
        Button btnLogin = new Button("Login");
        btnLogin.setStyle("-fx-background-color: transparent; -fx-text-fill: #62aaff; -fx-underline: true; -fx-cursor: hand;");
        loginBox.getChildren().addAll(lblLogin, btnLogin);

        // Message label for error/status
        Label lblMsg = new Label();
        lblMsg.setWrapText(true);
        lblMsg.setMaxWidth(320);

        registerBox.getChildren().addAll(logoLabel, titleLabel, lblUser, txtUser, lblPass, txtPass, lblFullName, txtFullName, btnRegister, loginBox, lblMsg);

        // Register button action
        btnRegister.setOnAction(e -> {
            String user = txtUser.getText().trim();
            String pass = txtPass.getText();
            String fullName = txtFullName.getText().trim();
            lblMsg.setText("");

            if (user.isEmpty() || pass.isEmpty() || fullName.isEmpty()) {
                lblMsg.setStyle(Theme.ERROR);
                lblMsg.setText("Please fill in all fields");
                return;
            }

            btnRegister.setDisable(true);
            lblMsg.setStyle("-fx-text-fill: #89CFF0;");
            lblMsg.setText("Registering...");

            String cmd = "{\"command\":\"REGISTER\",\"username\":\"" + user + "\",\"password\":\"" + pass + "\",\"fullname\":\"" + fullName + "\"}";
            MainApp.network.sendLine(cmd);

            String response = null;
            try {
                response = MainApp.network.readLine();
                System.out.println("Server response: " + response);
            } catch (Exception ex) {
                lblMsg.setStyle(Theme.ERROR);
                lblMsg.setText("Connection error: " + ex.getMessage());
                btnRegister.setDisable(false);
                return;
            }

            if (response != null) {
                try {
                    JSONObject json = new JSONObject(response);
                    if ("OK".equals(json.optString("status"))) {
                        lblMsg.setStyle(Theme.SUCCESS);
                        lblMsg.setText("Registration successful! You can now login.");

                        // Clear fields
                        txtUser.clear();
                        txtPass.clear();
                        txtFullName.clear();

                        // Switch to login after a delay
                        Thread.sleep(1500);
                        new LoginWindow(stage).show();
                    } else {
                        lblMsg.setStyle(Theme.ERROR);
                        String errorMsg = json.optString("error", "Unknown error");

                        if ("exists".equals(errorMsg)) {
                            lblMsg.setText("Username already exists. Please choose another.");
                        } else {
                            lblMsg.setText("Registration failed: " + errorMsg);
                        }
                        btnRegister.setDisable(false);
                    }
                } catch (Exception ex) {
                    lblMsg.setStyle(Theme.ERROR);
                    lblMsg.setText("Error processing response: " + ex.getMessage());
                    btnRegister.setDisable(false);
                }
            } else {
                lblMsg.setStyle(Theme.ERROR);
                lblMsg.setText("No response from server");
                btnRegister.setDisable(false);
            }
        });

        btnLogin.setOnAction(e -> new LoginWindow(stage).show());

        // Main container with background
        StackPane rootPane = new StackPane();
        rootPane.setStyle("-fx-background-color: " + Theme.BG + ";");
        rootPane.getChildren().add(registerBox);

        Scene scene = new Scene(rootPane, 800, 600);
        stage.setTitle("Banking Application - Register");
        stage.setScene(scene);
        stage.show();
    }
}