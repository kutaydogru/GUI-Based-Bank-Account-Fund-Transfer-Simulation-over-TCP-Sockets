package client.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class WithdrawWindow {
    private final Stage stage;
    private final String username;

    public WithdrawWindow(Stage stage, String username) {
        this.stage = stage;
        this.username = username;
    }

    public void show() {
        Label lblTitle = new Label("Withdraw");
        lblTitle.setStyle("-fx-font-weight: bold; -fx-text-fill:" + Theme.FG + ";");

        Label lblAccount = new Label("Account No:"); lblAccount.setStyle("-fx-text-fill:" + Theme.FG + ";");
        TextField txtAccount = new TextField(); txtAccount.setStyle(Theme.TXT_FIELD);

        Label lblAmount = new Label("Amount:"); lblAmount.setStyle("-fx-text-fill:" + Theme.FG + ";");
        TextField txtAmount = new TextField(); txtAmount.setStyle(Theme.TXT_FIELD);

        Button btnWithdraw = new Button("Withdraw"); btnWithdraw.setStyle(Theme.BTN);
        Button btnBack = new Button("Back"); btnBack.setStyle(Theme.BTN);
        Label lblMsg = new Label();

        btnWithdraw.setOnAction(e -> {
            String acc = txtAccount.getText().trim();
            String amtt = txtAmount.getText().trim();
            double amt = 0;
            try { amt = Double.parseDouble(amtt); }
            catch (Exception ex) { lblMsg.setStyle(Theme.ERROR); lblMsg.setText("Invalid amount."); return; }
            String cmd = "{\"command\":\"WITHDRAW\",\"accountNo\":\"" + acc + "\",\"amount\":" + amt + "}";
            MainApp.network.sendLine(cmd);
            String response = null;
            try { response = MainApp.network.readLine(); }
            catch (Exception ex) { lblMsg.setStyle(Theme.ERROR); lblMsg.setText("No server response: " + ex.getMessage()); return; }
            if (response != null && response.contains("\"status\":\"OK\"")) {
                lblMsg.setStyle(Theme.SUCCESS); lblMsg.setText("Withdraw successful!");
            } else {
                lblMsg.setStyle(Theme.ERROR); lblMsg.setText("Withdraw failed: " + (response == null ? "" : response));
            }
        });

        btnBack.setOnAction(e -> new MainMenuWindow(stage, username).show());

        VBox vbox = new VBox(16, lblTitle, lblAccount, txtAccount, lblAmount, txtAmount, btnWithdraw, btnBack, lblMsg);
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(30, 40, 30, 40));
        StackPane root = new StackPane(vbox);
        root.setStyle("-fx-background-color: " + Theme.BG + ";");
        Scene scene = new Scene(root, 380, 340);

        scene.widthProperty().addListener((obs, oldVal, newVal) -> updateResponsive(vbox, lblTitle, scene));
        scene.heightProperty().addListener((obs, oldVal, newVal) -> updateResponsive(vbox, lblTitle, scene));
        updateResponsive(vbox, lblTitle, scene);

        WindowUtil.keepStageState(stage, scene);
    }

    private void updateResponsive(VBox vbox, Label lblTitle, Scene scene) {
        double h = scene.getHeight();
        int titleFont = (int) Math.max(18, Math.min(32, h * 0.085));
        int spacing = (int) Math.max(10, Math.min(32, h * 0.06));
        int pad = (int) Math.max(12, Math.min(52, h * 0.10));
        lblTitle.setStyle("-fx-font-size: " + titleFont + "px; -fx-font-weight: bold; -fx-text-fill:" + Theme.FG + "; -fx-font-size: 16px;");
        vbox.setSpacing(spacing);
        vbox.setPadding(new Insets(pad, pad, pad, pad));
    }
}