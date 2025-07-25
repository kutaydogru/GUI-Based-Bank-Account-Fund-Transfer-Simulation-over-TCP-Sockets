package client.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.json.JSONObject;

/**
 * Screen for transferring money between accounts.
 */
public class TransferMoneyWindow {
    private final Stage stage;
    private final String username;

    public TransferMoneyWindow(Stage stage, String username) {
        this.stage = stage;
        this.username = username;
    }

    public void show() {
        // Create sidebar
        SideBarMenu sidebar = new SideBarMenu(stage, username, "transfer");

        // Main content area
        VBox contentArea = new VBox(20);
        contentArea.setStyle("-fx-background-color: " + Theme.CONTENT_BG + ";");
        contentArea.setPadding(new Insets(30));

        // Page title
        Label titleLabel = new Label("Transfer Money");
        titleLabel.setStyle(Theme.PAGE_TITLE);

        // Form with inputs
        Label lblDescription = new Label("Enter details to transfer money between accounts:");
        lblDescription.setStyle("-fx-font-size: 14px; -fx-text-fill: " + Theme.FG + ";");

        Label lblFromAccount = new Label("From Account:");
        lblFromAccount.setStyle("-fx-text-fill: " + Theme.FG + ";");
        TextField txtFromAccount = new TextField();
        txtFromAccount.setStyle(Theme.TXT_FIELD);
        txtFromAccount.setMaxWidth(300);
        txtFromAccount.setPromptText("Enter source account number");

        Label lblToAccount = new Label("To Account:");
        lblToAccount.setStyle("-fx-text-fill: " + Theme.FG + ";");
        TextField txtToAccount = new TextField();
        txtToAccount.setStyle(Theme.TXT_FIELD);
        txtToAccount.setMaxWidth(300);
        txtToAccount.setPromptText("Enter destination account number");

        Label lblAmount = new Label("Amount (TL):");
        lblAmount.setStyle("-fx-text-fill: " + Theme.FG + ";");
        TextField txtAmount = new TextField();
        txtAmount.setStyle(Theme.TXT_FIELD);
        txtAmount.setMaxWidth(300);
        txtAmount.setPromptText("Enter amount to transfer");

        Button btnTransfer = new Button("Transfer Money");
        btnTransfer.setStyle(Theme.PRIMARY_BTN);
        btnTransfer.setMaxWidth(300);

        // Success message container
        VBox successBox = new VBox(10);
        successBox.setAlignment(Pos.CENTER_LEFT);
        successBox.setPadding(new Insets(15));
        successBox.setMaxWidth(400);
        successBox.setStyle("-fx-background-color: #2d7d4c30; -fx-border-color: #2d7d4c; -fx-border-radius: 5;");

        Label successLabel = new Label("Transfer Completed Successfully!");
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
        formContainer.getChildren().addAll(lblDescription, lblFromAccount, txtFromAccount,
                lblToAccount, txtToAccount, lblAmount, txtAmount,
                btnTransfer, lblError);
        formContainer.setMaxWidth(400);
        formContainer.setStyle(Theme.CARD);

        contentArea.getChildren().addAll(titleLabel, formContainer, successBox);

        // Transfer action
        btnTransfer.setOnAction(e -> {
            String fromAccount = txtFromAccount.getText().trim();
            String toAccount = txtToAccount.getText().trim();
            String amountStr = txtAmount.getText().trim();

            if (fromAccount.isEmpty() || toAccount.isEmpty() || amountStr.isEmpty()) {
                lblError.setText("Please fill in all fields");
                successBox.setVisible(false);
                return;
            }

            double amount;
            try {
                amount = Double.parseDouble(amountStr);
                if (amount <= 0) {
                    lblError.setText("Amount must be positive");
                    successBox.setVisible(false);
                    return;
                }
            } catch (NumberFormatException ex) {
                lblError.setText("Please enter a valid amount");
                successBox.setVisible(false);
                return;
            }

            String cmd = "{\"command\":\"TRANSFER\",\"fromAccount\":\"" + fromAccount +
                    "\",\"toAccount\":\"" + toAccount + "\",\"amount\":" + amount + "}";

            MainApp.network.sendLine(cmd);
            String response = null;
            try {
                response = MainApp.network.readLine();
            } catch (Exception ex) {
                lblError.setText("Error connecting to server: " + ex.getMessage());
                successBox.setVisible(false);
                return;
            }

            if (response != null) {
                try {
                    JSONObject json = new JSONObject(response);
                    if ("OK".equals(json.optString("status"))) {
                        lblError.setText("");
                        successDetails.setText("From Account: " + fromAccount +
                                "\nTo Account: " + toAccount +
                                "\nAmount: " + String.format("%.2f TL", amount));
                        successBox.setVisible(true);
                        txtAmount.clear();
                    } else {
                        lblError.setText(getErrorMessage(json.optString("error")));
                        successBox.setVisible(false);
                    }
                } catch (Exception ex) {
                    lblError.setText("Error processing response: " + ex.getMessage());
                    successBox.setVisible(false);
                }
            } else {
                lblError.setText("No response from server");
                successBox.setVisible(false);
            }
        });

        // Main layout
        BorderPane mainLayout = new BorderPane();
        mainLayout.setLeft(sidebar);
        mainLayout.setCenter(contentArea);

        Scene scene = new Scene(mainLayout, 1000, 700);
        stage.setTitle("Banking Application - Transfer Money");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Convert error codes to user-friendly messages
     */
    private String getErrorMessage(String errorCode) {
        switch (errorCode) {
            case "insufficientfunds": return "Insufficient funds in source account";
            case "sourcenoteexist": return "Source account doesn't exist";
            case "destinationnotexist": return "Destination account doesn't exist";
            case "notyouraccount": return "Source account doesn't belong to you";
            case "invalidamount": return "Invalid amount";
            case "notloggedin": return "You are not logged in";
            default: return "Error: " + errorCode;
        }
    }
}