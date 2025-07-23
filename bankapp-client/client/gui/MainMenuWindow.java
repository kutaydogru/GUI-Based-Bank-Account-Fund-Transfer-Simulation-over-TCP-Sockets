package client.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class MainMenuWindow {
    private final Stage stage;
    private final String username;

    public MainMenuWindow(Stage stage, String username) {
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

        StackPane logoutPane = new StackPane(btnLogout);
        StackPane.setAlignment(btnLogout, Pos.TOP_RIGHT);
        StackPane.setMargin(btnLogout, new Insets(8, 8, 0, 0));

        Label lblWelcome = new Label("Welcome, " + username + "!");
        lblWelcome.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: " + Theme.FG + ";");

        Button btnCreate = new Button("Create Account");   btnCreate.setStyle(Theme.BTN);
        Button btnAccounts = new Button("List My Accounts"); btnAccounts.setStyle(Theme.BTN);
        Button btnTransfer = new Button("Transfer Money"); btnTransfer.setStyle(Theme.BTN);
        Button btnWithdraw = new Button("Withdraw");       btnWithdraw.setStyle(Theme.BTN);
        Button btnDeposit = new Button("Deposit");         btnDeposit.setStyle(Theme.BTN);
        Button btnBalance = new Button("Check Balance");   btnBalance.setStyle(Theme.BTN);

        btnCreate.setMaxWidth(Double.MAX_VALUE); btnAccounts.setMaxWidth(Double.MAX_VALUE);
        btnTransfer.setMaxWidth(Double.MAX_VALUE); btnWithdraw.setMaxWidth(Double.MAX_VALUE);
        btnDeposit.setMaxWidth(Double.MAX_VALUE); btnBalance.setMaxWidth(Double.MAX_VALUE);

        VBox buttonBox = new VBox(12, btnCreate, btnAccounts, btnTransfer, btnWithdraw, btnDeposit, btnBalance);
        buttonBox.setAlignment(Pos.TOP_CENTER);
        buttonBox.setPadding(new Insets(14, 44, 44, 44));

        VBox centerBox = new VBox(6, lblWelcome, buttonBox);
        centerBox.setAlignment(Pos.TOP_CENTER);
        VBox.setMargin(lblWelcome, new Insets(18,0,8,0));

        BorderPane borderPane = new BorderPane();
        borderPane.setTop(logoutPane);
        borderPane.setCenter(centerBox);

        StackPane rootPane = new StackPane(borderPane);
        rootPane.setStyle("-fx-background-color:" + Theme.BG + ";");
        Scene scene = new Scene(rootPane, 540, 440);

        scene.widthProperty().addListener((obs, oldVal, newVal) -> updateResponsive(centerBox, lblWelcome, scene));
        scene.heightProperty().addListener((obs, oldVal, newVal) -> updateResponsive(centerBox, lblWelcome, scene));
        updateResponsive(centerBox, lblWelcome, scene);

        WindowUtil.keepStageState(stage, scene);

        btnCreate.setOnAction(e -> new CreateAccountWindow(stage, username).show());
        btnAccounts.setOnAction(e -> new ListAccountsWindow(stage, username).show());
        btnTransfer.setOnAction(e -> new TransferMoneyWindow(stage, username).show());
        btnWithdraw.setOnAction(e -> new WithdrawWindow(stage, username).show());
        btnDeposit.setOnAction(e -> new DepositWindow(stage, username).show());
        btnBalance.setOnAction(e -> new CheckBalanceWindow(stage, username).show());
    }

    private void updateResponsive(VBox vbox, Label lblTitle, Scene scene) {
        double h = scene.getHeight();
        int titleFont = (int) Math.max(18, Math.min(36, h * 0.09));
        int spacing = (int) Math.max(10, Math.min(36, h * 0.07));
        int pad = (int) Math.max(12, Math.min(56, h * 0.15));
        lblTitle.setStyle("-fx-font-size: " + titleFont + "px; -fx-font-weight: bold; -fx-text-fill: " + Theme.FG + ";");
        vbox.setSpacing(spacing);
        vbox.setPadding(new Insets(pad, pad, pad, pad));
    }
}