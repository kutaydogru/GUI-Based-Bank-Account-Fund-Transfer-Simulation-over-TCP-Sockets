package client.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

/**
 * Screen for creating a new bank account.
 */
public class CreateAccountWindow {
    private final Stage stage;
    private final String username;

    public CreateAccountWindow(Stage stage, String username) {
        this.stage = stage;
        this.username = username;
    }

    public void show() {
        // Create sidebar
        SideBarMenu sidebar = new SideBarMenu(stage, username, "create");

        // Main content area
        VBox contentArea = new VBox(20);
        contentArea.setStyle("-fx-background-color: " + Theme.CONTENT_BG + ";");
        contentArea.setPadding(new Insets(30));

        // Page title
        Label titleLabel = new Label("Create New Account");
        titleLabel.setStyle(Theme.PAGE_TITLE);

        // Form with inputs
        Label lblDescription = new Label("Complete the form below to create a new bank account:");
        lblDescription.setStyle("-fx-font-size: 14px; -fx-text-fill: " + Theme.FG + ";");

        Label lblAccountNo = new Label("Account Number:");
        lblAccountNo.setStyle("-fx-text-fill: " + Theme.FG + ";");
        TextField txtAccountNo = new TextField();
        txtAccountNo.setStyle(Theme.TXT_FIELD);
        txtAccountNo.setMaxWidth(300);
        txtAccountNo.setPromptText("Enter account number");

        Label lblInitialBalance = new Label("Initial Balance:");
        lblInitialBalance.setStyle("-fx-text-fill: " + Theme.FG + ";");
        TextField txtInitialBalance = new TextField();
        txtInitialBalance.setStyle(Theme.TXT_FIELD);
        txtInitialBalance.setMaxWidth(300);
        txtInitialBalance.setPromptText("Enter initial balance");

        Button btnCreate = new Button("Create Account");
        btnCreate.setStyle(Theme.SUCCESS_BTN);
        btnCreate.setMaxWidth(300);

        // Success message container
        VBox successBox = new VBox(10);
        successBox.setAlignment(Pos.CENTER_LEFT);
        successBox.setPadding(new Insets(15));
        successBox.setMaxWidth(400);
        successBox.setStyle("-fx-background-color: #2d7d4c30; -fx-border-color: #2d7d4c; -fx-border-radius: 5;");

        Label successLabel = new Label("Account Created Successfully!");
        successLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #6aff81;");

        Label successDetails = new Label();
        successDetails.setStyle("-fx-text-fill: #6aff81;");

        Button btnViewAccounts = new Button("View My Accounts");
        btnViewAccounts.setStyle("-fx-background-color: #2d7d4c; -fx-text-fill: white;");
        btnViewAccounts.setOnAction(e -> new ListAccountsWindow(stage, username).show());

        successBox.getChildren().addAll(successLabel, successDetails, btnViewAccounts);
        successBox.setVisible(false);

        // Error message
        Label lblError = new Label();
        lblError.setStyle(Theme.ERROR);
        lblError.setWrapText(true);
        lblError.setMaxWidth(400);

        VBox formContainer = new VBox(10);
        formContainer.getChildren().addAll(lblDescription, lblAccountNo, txtAccountNo,
                lblInitialBalance, txtInitialBalance, btnCreate, lblError);
        formContainer.setMaxWidth(400);
        formContainer.setStyle(Theme.CARD);

        contentArea.getChildren().addAll(titleLabel, formContainer, successBox);

        // Create account action
        btnCreate.setOnAction(e -> {
            String accountNo = txtAccountNo.getText().trim();
            String balanceStr = txtInitialBalance.getText().trim();

            if (accountNo.isEmpty()) {
                lblError.setText("Please enter an account number");
                successBox.setVisible(false);
                return;
            }

            double balance;
            try {
                balance = Double.parseDouble(balanceStr);
                if (balance < 0) {
                    lblError.setText("Balance cannot be negative");
                    successBox.setVisible(false);
                    return;
                }
            } catch (NumberFormatException ex) {
                lblError.setText("Please enter a valid initial balance");
                successBox.setVisible(false);
                return;
            }

            String cmd = "{\"command\":\"CREATE\",\"accountNo\":\"" + accountNo + "\",\"balance\":" + balance + "}";
            MainApp.network.sendLine(cmd);
            String response = null;
            try {
                response = MainApp.network.readLine();
            } catch (Exception ex) {
                lblError.setText("Error connecting to server: " + ex.getMessage());
                successBox.setVisible(false);
                return;
            }

            if (response != null && response.contains("\"status\":\"OK\"")) {
                lblError.setText("");
                successDetails.setText("Account Number: " + accountNo + "\nInitial Balance: " + balance + " TL");
                successBox.setVisible(true);
                txtAccountNo.clear();
                txtInitialBalance.clear();
            } else {
                lblError.setText("Failed to create account: " + (response == null ? "No server response" : response));
                successBox.setVisible(false);
            }
        });

        // Main layout
        BorderPane mainLayout = new BorderPane();
        mainLayout.setLeft(sidebar);
        mainLayout.setCenter(contentArea);

        Scene scene = new Scene(mainLayout, 1000, 700);
        stage.setTitle("Banking Application - Create Account");
        stage.setScene(scene);
        stage.show();
    }
}