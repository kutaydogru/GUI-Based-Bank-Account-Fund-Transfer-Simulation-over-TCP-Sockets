package client.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.json.JSONObject;

public class CheckBalanceWindow {
    private final Stage stage;
    private final String username;

    public CheckBalanceWindow(Stage stage, String username) {
        this.stage = stage;
        this.username = username;
    }

    public void show() {
        Button btnLogout = new Button("Logout");
        btnLogout.setStyle(Theme.LOGOUT);
        btnLogout.setFocusTraversable(false);
        btnLogout.setOnAction(e -> {
            String cmd = "{\"command\":\"LOGOUT\"}";
            MainApp.network.sendLine(cmd);
            try { MainApp.network.readLine(); } catch (Exception ex) {}
            new HomeWindow(stage).show();
        });
        StackPane spLogout = new StackPane(btnLogout);
        StackPane.setAlignment(btnLogout, Pos.TOP_RIGHT);
        StackPane.setMargin(btnLogout, new Insets(8, 8, 0, 0));

        Label lblTitle = new Label("Check Balance");
        lblTitle.setStyle("-fx-font-weight: bold; -fx-text-fill: " + Theme.FG + ";");

        Label lblAccount = new Label("Account No:"); lblAccount.setStyle("-fx-text-fill: " + Theme.FG + ";");
        TextField txtAccount = new TextField(); txtAccount.setStyle(Theme.TXT_FIELD);

        Button btnCheck = new Button("Check"); btnCheck.setStyle(Theme.BTN);
        Button btnBack = new Button("Back"); btnBack.setStyle(Theme.BTN);
        Label lblMsg = new Label(); lblMsg.setStyle("-fx-font-size: 14px;");

        btnCheck.setOnAction(e -> {
            String acc = txtAccount.getText().trim();
            if (acc.isEmpty()) {
                lblMsg.setStyle(Theme.ERROR);
                lblMsg.setText("Please enter an account number");
                return;
            }

            String cmd = "{\"command\":\"BALANCE\",\"accountNo\":\"" + acc + "\"}";
            MainApp.network.sendLine(cmd);
            String response = null;
            try { response = MainApp.network.readLine(); }
            catch (Exception ex) { lblMsg.setStyle(Theme.ERROR); lblMsg.setText("No server response: " + ex.getMessage()); return; }

            if (response != null) {
                try {
                    JSONObject json = new JSONObject(response);
                    if (json.has("balance")) {
                        // Success case
                        double bal = json.getDouble("balance");
                        lblMsg.setStyle(Theme.SUCCESS);
                        lblMsg.setText("Balance: " + bal + " TL");
                    } else if ("ERROR".equals(json.optString("status"))) {
                        // Error handling with better messages
                        lblMsg.setStyle(Theme.ERROR);
                        String errorCode = json.optString("error");

                        switch(errorCode) {
                            case "notyouraccount":
                                lblMsg.setText("This account doesn't belong to you.");
                                break;
                            case "notfound":
                                lblMsg.setText("Account number doesn't exist.");
                                break;
                            case "notloggedin":
                                lblMsg.setText("You must be logged in to check account balance.");
                                break;
                            default:
                                lblMsg.setText("Balance check failed: " + errorCode);
                                break;
                        }
                    } else {
                        lblMsg.setStyle(Theme.ERROR);
                        lblMsg.setText("Unknown response: " + response);
                    }
                } catch (Exception ex) {
                    lblMsg.setStyle(Theme.ERROR);
                    lblMsg.setText("Parse error: " + ex.getMessage());
                }
            } else {
                lblMsg.setStyle(Theme.ERROR);
                lblMsg.setText("Balance failed: empty response");
            }
        });

        btnBack.setOnAction(e -> new MainMenuWindow(stage, username).show());

        VBox form = new VBox(10, lblAccount, txtAccount, btnCheck, btnBack, lblMsg);
        form.setMaxWidth(300);
        form.setAlignment(Pos.TOP_CENTER);
        form.setPadding(new Insets(16, 0, 0, 0));

        VBox mainContent = new VBox(15, lblTitle, form);
        mainContent.setAlignment(Pos.TOP_CENTER);
        VBox.setMargin(lblTitle, new Insets(24, 0, 0, 0));

        BorderPane content = new BorderPane();
        content.setTop(spLogout);
        content.setCenter(mainContent);

        StackPane root = new StackPane(content);
        root.setStyle("-fx-background-color: " + Theme.BG + "; -fx-font-size: 16px;");
        Scene scene = new Scene(root, 440, 320);

        scene.widthProperty().addListener((obs, oldVal, newVal) -> updateResponsive(mainContent, lblTitle, scene));
        scene.heightProperty().addListener((obs, oldVal, newVal) -> updateResponsive(mainContent, lblTitle, scene));
        updateResponsive(mainContent, lblTitle, scene);

        WindowUtil.keepStageState(stage, scene);
    }

    private void updateResponsive(VBox vbox, Label lblTitle, Scene scene) {
        double h = scene.getHeight();
        int titleFont = (int) Math.max(18, Math.min(32, h * 0.085));
        int spacing = (int) Math.max(12, Math.min(30, h * 0.07));
        int pad = (int) Math.max(10, Math.min(38, h * 0.11));
        lblTitle.setStyle("-fx-font-size: " + titleFont + "px; -fx-font-weight: bold; -fx-text-fill:" + Theme.FG + ";");
        vbox.setSpacing(spacing);
        vbox.setPadding(new Insets(pad, pad, pad, pad));
    }
}