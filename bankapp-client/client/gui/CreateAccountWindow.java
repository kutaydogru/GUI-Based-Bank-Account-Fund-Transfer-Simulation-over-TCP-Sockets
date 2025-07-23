package client.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class CreateAccountWindow {
    private final Stage stage;
    private final String username;

    public CreateAccountWindow(Stage stage, String username) {
        this.stage = stage;
        this.username = username;
    }

    public void show() {
        Label lblTitle = new Label("Create New Account");
        lblTitle.setStyle("-fx-font-weight: bold; -fx-text-fill: " + Theme.FG + ";");
        Label lblAcc = new Label("Account Number:"); lblAcc.setStyle("-fx-text-fill:" + Theme.FG + ";");
        TextField txtAcc = new TextField(); txtAcc.setStyle(Theme.TXT_FIELD);
        Label lblBal = new Label("Initial Balance:"); lblBal.setStyle("-fx-text-fill:" + Theme.FG + ";");
        TextField txtBal = new TextField(); txtBal.setStyle(Theme.TXT_FIELD);

        Button btnCreate = new Button("Create"); btnCreate.setStyle(Theme.BTN);
        Button btnBack = new Button("Back"); btnBack.setStyle(Theme.BTN);
        Label lblMsg = new Label();

        btnCreate.setOnAction(e -> {
            String accNo = txtAcc.getText().trim();
            String balStr = txtBal.getText().trim();
            double balance;
            try {
                balance = Double.parseDouble(balStr);
                if (balance < 0) { lblMsg.setStyle(Theme.ERROR); lblMsg.setText("Balance cannot be negative."); return; }
            } catch (Exception ex) { lblMsg.setStyle(Theme.ERROR); lblMsg.setText("Invalid balance."); return; }
            String cmd = "{\"command\":\"CREATE\",\"accountNo\":\"" + accNo + "\",\"balance\":" + balance + "}";
            MainApp.network.sendLine(cmd);
            String response = null;
            try { response = MainApp.network.readLine(); } catch (Exception ex) {
                lblMsg.setStyle(Theme.ERROR); lblMsg.setText("No server response: " + ex.getMessage()); return;
            }
            if (response != null && response.contains("\"status\":\"OK\"")) {
                lblMsg.setStyle(Theme.SUCCESS); lblMsg.setText("Account created successfully!");
            } else {
                lblMsg.setStyle(Theme.ERROR); lblMsg.setText("Account creation failed: " + (response == null ? "" : response));
            }
        });

        btnBack.setOnAction(e -> new MainMenuWindow(stage, username).show());

        VBox vbox = new VBox(12, lblTitle, lblAcc, txtAcc, lblBal, txtBal, btnCreate, btnBack, lblMsg);
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(38, 44, 38, 44));

        StackPane root = new StackPane(vbox);
        root.setStyle("-fx-background-color:" + Theme.BG + "; -fx-font-size: 16px;");
        Scene scene = new Scene(root, 440, 380);

        scene.widthProperty().addListener((obs, oldVal, newVal) -> updateResponsive(vbox, lblTitle, scene));
        scene.heightProperty().addListener((obs, oldVal, newVal) -> updateResponsive(vbox, lblTitle, scene));
        updateResponsive(vbox, lblTitle, scene);

        WindowUtil.keepStageState(stage, scene);
    }

    private void updateResponsive(VBox vbox, Label lblTitle, Scene scene) {
        double h = scene.getHeight();
        int titleFont = (int) Math.max(18, Math.min(34, h * 0.09));
        int spacing = (int) Math.max(10, Math.min(32, h * 0.06));
        int pad = (int) Math.max(12, Math.min(64, h * 0.15));
        lblTitle.setStyle("-fx-font-size: " + titleFont + "px; -fx-font-weight: bold; -fx-text-fill: " + Theme.FG + ";");
        vbox.setSpacing(spacing);
        vbox.setPadding(new Insets(pad, pad, pad, pad));
    }
}