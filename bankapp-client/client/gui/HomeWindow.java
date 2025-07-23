package client.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class HomeWindow {
    private final Stage stage;

    public HomeWindow(Stage stage) { this.stage = stage; }

    public void show() {
        Label lblTitle = new Label("Welcome to BankApp");
        Button btnLogin = new Button("Login");
        Button btnRegister = new Button("Register");
        btnLogin.setStyle(Theme.BTN); btnRegister.setStyle(Theme.BTN);
        btnLogin.setMaxWidth(Double.MAX_VALUE); btnRegister.setMaxWidth(Double.MAX_VALUE);

        VBox vbox = new VBox(24, lblTitle, btnLogin, btnRegister);
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(60, 60, 60, 60));

        StackPane root = new StackPane(vbox);
        root.setStyle("-fx-background-color:" + Theme.BG + "; -fx-font-size: 16px;");
        Scene scene = new Scene(root, 400, 350);

        scene.widthProperty().addListener((obs, oldVal, newVal) -> updateResponsive(vbox, lblTitle, scene));
        scene.heightProperty().addListener((obs, oldVal, newVal) -> updateResponsive(vbox, lblTitle, scene));
        updateResponsive(vbox, lblTitle, scene);

        WindowUtil.keepStageState(stage, scene);

        btnLogin.setOnAction(e -> new LoginWindow(stage).show());
        btnRegister.setOnAction(e -> new RegisterWindow(stage).show());
    }

    private void updateResponsive(VBox vbox, Label lblTitle, Scene scene) {
        double h = scene.getHeight();
        int titleFont = (int) Math.max(20, Math.min(38, h * 0.09));
        int spacing = (int) Math.max(16, Math.min(40, h * 0.07));
        int pad = (int) Math.max(16, Math.min(80, h * 0.15));
        lblTitle.setStyle("-fx-font-size: " + titleFont + "px; -fx-font-weight: bold; -fx-text-fill: " + Theme.FG + ";");
        vbox.setSpacing(spacing);
        vbox.setPadding(new Insets(pad, pad, pad, pad));
    }
}