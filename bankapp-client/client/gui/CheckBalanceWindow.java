package client.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.json.JSONObject;

/**
 * Screen for checking the balance of a specific account.
 */
public class CheckBalanceWindow {
    private final Stage stage;
    private final String username;
    private String prefilledAccountNo = "";

    public CheckBalanceWindow(Stage stage, String username) {
        this.stage = stage;
        this.username = username;
    }

    /**
     * Pre-fills the account number field
     */
    public void setPrefilledAccountNo(String accountNo) {
        this.prefilledAccountNo = accountNo;
    }

    public void show() {
        // Create sidebar
        SideBarMenu sidebar = new SideBarMenu(stage, username, "balance");

        // Main content area
        VBox contentArea = new VBox(20);
        contentArea.setStyle("-fx-background-color: " + Theme.CONTENT_BG + ";");
        contentArea.setPadding(new Insets(30));

        // Page title
        Label titleLabel = new Label("Check Account Balance");
        titleLabel.setStyle(Theme.PAGE_TITLE);

        // Form with inputs
        Label lblAccountNo = new Label("Account Number:");
        lblAccountNo.setStyle("-fx-text-fill: " + Theme.FG + ";");
        TextField txtAccountNo = new TextField(prefilledAccountNo);
        txtAccountNo.setStyle(Theme.TXT_FIELD);
        txtAccountNo.setMaxWidth(300);

        Button btnCheckBalance = new Button("Check Balance");
        btnCheckBalance.setStyle(Theme.PRIMARY_BTN);
        btnCheckBalance.setMaxWidth(300);

        VBox formContainer = new VBox(10);
        formContainer.getChildren().addAll(lblAccountNo, txtAccountNo, btnCheckBalance);
        formContainer.setMaxWidth(400);
        formContainer.setStyle(Theme.CARD);

        // Result display
        VBox resultBox = new VBox(15);
        resultBox.setAlignment(Pos.CENTER_LEFT);
        resultBox.setMaxWidth(400);
        resultBox.setPadding(new Insets(20));
        resultBox.setStyle("-fx-background-color: " + Theme.PANEL_BG + "; -fx-border-color: " + Theme.SIDEBAR_BG +
                "; -fx-border-radius: 5;");
        resultBox.setVisible(false);

        Label lblResultTitle = new Label("Account Balance");
        lblResultTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: " + Theme.FG + ";");

        Label lblAccountDisplay = new Label();
        lblAccountDisplay.setStyle("-fx-font-size: 14px; -fx-text-fill: " + Theme.FG + ";");

        Label lblBalanceTitle = new Label("Current Balance:");
        lblBalanceTitle.setStyle("-fx-font-size: 14px; -fx-text-fill: " + Theme.FG + ";");

        Label lblBalanceValue = new Label();
        lblBalanceValue.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #62aaff;"); // Light blue balance

        Separator separator = new Separator();
        separator.setStyle("-fx-background-color: " + Theme.BORDER_COLOR + ";");

        resultBox.getChildren().addAll(lblResultTitle, lblAccountDisplay, separator, lblBalanceTitle, lblBalanceValue);

        // Error message
        Label lblError = new Label();
        lblError.setStyle(Theme.ERROR);
        lblError.setWrapText(true);
        lblError.setMaxWidth(400);

        // Check balance action
        btnCheckBalance.setOnAction(e -> {
            String accountNo = txtAccountNo.getText().trim();
            if (accountNo.isEmpty()) {
                lblError.setText("Please enter an account number");
                resultBox.setVisible(false);
                return;
            }

            String cmd = "{\"command\":\"BALANCE\",\"accountNo\":\"" + accountNo + "\"}";
            MainApp.network.sendLine(cmd);
            String response = null;

            try {
                response = MainApp.network.readLine();
            } catch (Exception ex) {
                lblError.setText("Error connecting to server: " + ex.getMessage());
                resultBox.setVisible(false);
                return;
            }

            if (response != null) {
                try {
                    JSONObject json = new JSONObject(response);
                    if (json.has("balance")) {
                        // Success case
                        double balance = json.getDouble("balance");
                        lblError.setText("");
                        lblAccountDisplay.setText("Account Number: " + accountNo);
                        lblBalanceValue.setText(String.format("%.2f TL", balance));
                        resultBox.setVisible(true);
                    } else if ("ERROR".equals(json.optString("status"))) {
                        lblError.setText(getErrorMessage(json.optString("error")));
                        resultBox.setVisible(false);
                    } else {
                        lblError.setText("Unknown response from server");
                        resultBox.setVisible(false);
                    }
                } catch (Exception ex) {
                    lblError.setText("Error processing response: " + ex.getMessage());
                    resultBox.setVisible(false);
                }
            } else {
                lblError.setText("No response from server");
                resultBox.setVisible(false);
            }
        });

        contentArea.getChildren().addAll(titleLabel, formContainer, resultBox, lblError);

        // Main layout
        BorderPane mainLayout = new BorderPane();
        mainLayout.setLeft(sidebar);
        mainLayout.setCenter(contentArea);

        Scene scene = new Scene(mainLayout, 1000, 700);
        stage.setTitle("Banking Application - Check Balance");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Convert error codes to user-friendly messages
     */
    private String getErrorMessage(String errorCode) {
        switch (errorCode) {
            case "notyouraccount": return "This account doesn't belong to you";
            case "notfound": return "Account not found";
            case "notloggedin": return "You are not logged in";
            default: return "Error: " + errorCode;
        }
    }
}