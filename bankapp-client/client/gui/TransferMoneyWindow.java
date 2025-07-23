package client.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class TransferMoneyWindow {
    private final Stage stage;
    private final String username;

    public TransferMoneyWindow(Stage stage, String username) {
        this.stage = stage;
        this.username = username;
    }

    public void show() {
        Label lblTitle = new Label("Transfer Money");
        lblTitle.setStyle("-fx-font-weight: bold; -fx-text-fill:" + Theme.FG + ";");

        Label lblFrom = new Label("From Account:"); lblFrom.setStyle("-fx-text-fill:" + Theme.FG + ";");
        TextField txtFrom = new TextField(); txtFrom.setStyle(Theme.TXT_FIELD);

        Label lblTo = new Label("To Account:"); lblTo.setStyle("-fx-text-fill:" + Theme.FG + ";");
        TextField txtTo = new TextField(); txtTo.setStyle(Theme.TXT_FIELD);

        Label lblAmount = new Label("Amount:"); lblAmount.setStyle("-fx-text-fill:" + Theme.FG + ";");
        TextField txtAmount = new TextField(); txtAmount.setStyle(Theme.TXT_FIELD);

        Button btnTransfer = new Button("Transfer"); btnTransfer.setStyle(Theme.BTN);
        Button btnBack = new Button("Back"); btnBack.setStyle(Theme.BTN);
        Label lblMsg = new Label();

        btnTransfer.setOnAction(e -> {
            String fromAcc = txtFrom.getText().trim();
            String toAcc = txtTo.getText().trim();
            String amtStr = txtAmount.getText().trim();
            double amt;
            try { amt = Double.parseDouble(amtStr); if (amt <= 0) { lblMsg.setStyle(Theme.ERROR); lblMsg.setText("Amount must be positive."); return;} }
            catch (Exception ex) { lblMsg.setStyle(Theme.ERROR); lblMsg.setText("Invalid amount."); return; }
            String cmd = "{\"command\":\"TRANSFER\",\"fromAccount\":\"" + fromAcc +
                    "\",\"toAccount\":\"" + toAcc + "\",\"amount\":" + amt + "}";
            MainApp.network.sendLine(cmd);
            String response = null;
            try { response = MainApp.network.readLine(); }
            catch (Exception ex) { lblMsg.setStyle(Theme.ERROR); lblMsg.setText("No server response: " + ex.getMessage()); return; }
            if (response != null && response.contains("\"status\":\"OK\"")) {
                lblMsg.setStyle(Theme.SUCCESS); lblMsg.setText("Transfer successful!");
            } else {
                lblMsg.setStyle(Theme.ERROR); lblMsg.setText("Transfer failed: " + (response == null ? "" : response));
            }
        });

        btnBack.setOnAction(e -> new MainMenuWindow(stage, username).show());

        VBox vbox = new VBox(16, lblTitle, lblFrom, txtFrom, lblTo, txtTo, lblAmount, txtAmount, btnTransfer, btnBack, lblMsg);
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(30, 40, 30, 40));
        StackPane root = new StackPane(vbox);
        root.setStyle("-fx-background-color: " + Theme.BG + "; -fx-font-size: 14px;");
        Scene scene = new Scene(root, 420, 400);

        scene.widthProperty().addListener((obs, oldVal, newVal) -> updateResponsive(vbox, lblTitle, scene));
        scene.heightProperty().addListener((obs, oldVal, newVal) -> updateResponsive(vbox, lblTitle, scene));
        updateResponsive(vbox, lblTitle, scene);

        WindowUtil.keepStageState(stage, scene);
    }

    private void updateResponsive(VBox vbox, Label lblTitle, Scene scene) {
        double h = scene.getHeight();
        int titleFont = (int) Math.max(18, Math.min(32, h * 0.08));
        int spacing = (int) Math.max(10, Math.min(32, h * 0.055));
        int pad = (int) Math.max(12, Math.min(60, h * 0.13));
        lblTitle.setStyle("-fx-font-size: " + titleFont + "px; -fx-font-weight: bold; -fx-text-fill:" + Theme.FG + ";");
        vbox.setSpacing(spacing);
        vbox.setPadding(new Insets(pad, pad, pad, pad));
    }
}