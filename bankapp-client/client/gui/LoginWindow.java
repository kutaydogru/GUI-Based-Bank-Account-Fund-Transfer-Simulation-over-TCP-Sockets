package client.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class LoginWindow {
    private final Stage stage;

    public LoginWindow(Stage stage) { this.stage = stage; }

    public void show() {
        Label lblTitle = new Label("Login");
        lblTitle.setStyle("-fx-font-weight: bold; -fx-text-fill: " + Theme.FG + ";");
        Label lblUser = new Label("Username:");       lblUser.setStyle("-fx-text-fill:" + Theme.FG + ";");
        TextField txtUser = new TextField();          txtUser.setStyle(Theme.TXT_FIELD);
        Label lblPass = new Label("Password:");       lblPass.setStyle("-fx-text-fill:" + Theme.FG + ";");
        PasswordField txtPass = new PasswordField();  txtPass.setStyle(Theme.TXT_FIELD);

        Button btnLogin = new Button("Login");        btnLogin.setStyle(Theme.BTN);
        Button btnBack = new Button("Back");          btnBack.setStyle(Theme.BTN);
        Label lblMsg = new Label();

        btnLogin.setOnAction(e -> {
            String username = txtUser.getText().trim();
            String password = txtPass.getText().trim();
            String cmd = "{\"command\":\"LOGIN\",\"username\":\"" + username + "\",\"password\":\"" + password + "\"}";
            MainApp.network.sendLine(cmd);

            String response = null;
            try { response = MainApp.network.readLine(); }
            catch (Exception ex) { lblMsg.setStyle(Theme.ERROR); lblMsg.setText("No server response: " + ex.getMessage()); return; }
            if (response != null && response.contains("\"status\":\"OK\"")) {
                new MainMenuWindow(stage, username).show();
            } else { lblMsg.setStyle(Theme.ERROR); lblMsg.setText("Login failed: " + (response == null ? "" : response)); }
        });

        btnBack.setOnAction(e -> new HomeWindow(stage).show());

        VBox vbox = new VBox(12, lblTitle, lblUser, txtUser, lblPass, txtPass, btnLogin, btnBack, lblMsg);
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(48,48,48,48));

        StackPane root = new StackPane(vbox);
        root.setStyle("-fx-background-color:" + Theme.BG + "; -fx-font-size: 16px;");
        Scene scene = new Scene(root, 400, 420);

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