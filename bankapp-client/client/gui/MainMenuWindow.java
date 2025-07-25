package client.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Main dashboard screen showing account overview
 * and providing quick access to common functions.
 * Updated with dark theme styling for tables and buttons.
 */
public class MainMenuWindow {
    private final Stage stage;
    private final String username;

    public MainMenuWindow(Stage stage, String username) {
        this.stage = stage;
        this.username = username;
    }

    public void show() {
        // Create sidebar
        SideBarMenu sidebar = new SideBarMenu(stage, username, "dashboard");

        // Main content area
        VBox contentArea = new VBox(20);
        contentArea.setStyle("-fx-background-color: " + Theme.CONTENT_BG + ";");
        contentArea.setPadding(new Insets(30));

        // Page title
        Label titleLabel = new Label("Dashboard");
        titleLabel.setStyle(Theme.PAGE_TITLE);

        // Welcome message
        Label welcomeMsg = new Label("Welcome to the Bank Application");
        welcomeMsg.setStyle("-fx-font-size: 18px; -fx-text-fill: " + Theme.FG + ";");

        // Quick actions section
        Label quickActionsLabel = new Label("Quick Actions");
        quickActionsLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + Theme.FG + ";");

        // Action buttons with dark theme styling
        Button btnViewAccounts = createDashboardButton("View All Accounts", "See your account balances");
        Button btnTransfer = createDashboardButton("Transfer Money", "Move funds between accounts");
        Button btnDeposit = createDashboardButton("Deposit Money", "Add funds to your account");
        Button btnWithdraw = createDashboardButton("Withdraw Money", "Take money from your account");

        // Button actions
        btnViewAccounts.setOnAction(e -> new ListAccountsWindow(stage, username).show());
        btnTransfer.setOnAction(e -> new TransferMoneyWindow(stage, username).show());
        btnDeposit.setOnAction(e -> new DepositWindow(stage, username).show());
        btnWithdraw.setOnAction(e -> new WithdrawWindow(stage, username).show());

        // Dashboard grid
        GridPane actionGrid = new GridPane();
        actionGrid.setHgap(20);
        actionGrid.setVgap(20);
        actionGrid.add(btnViewAccounts, 0, 0);
        actionGrid.add(btnTransfer, 1, 0);
        actionGrid.add(btnDeposit, 0, 1);
        actionGrid.add(btnWithdraw, 1, 1);

        // Account summary section
        Label accountSummaryLabel = new Label("Account Summary");
        accountSummaryLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + Theme.FG + ";");

        // Account summary table with dark theme styling
        TableView<ListAccountsWindow.AccountRow> accountTable = new TableView<>();
        accountTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        accountTable.setMaxHeight(200);
        accountTable.setStyle("-fx-background-color: " + Theme.PANEL_BG + "; " +
                "-fx-table-cell-border-color: " + Theme.BORDER_COLOR + ";" +
                "-fx-control-inner-background: " + Theme.PANEL_BG + ";" +
                "-fx-table-header-border-color: " + Theme.BORDER_COLOR + ";" +
                "-fx-text-background-color: " + Theme.FG + ";");

        // Custom row styling for alternating rows
        accountTable.setRowFactory(tv -> {
            TableRow<ListAccountsWindow.AccountRow> row = new TableRow<ListAccountsWindow.AccountRow>() {
                @Override
                protected void updateItem(ListAccountsWindow.AccountRow item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setStyle("");
                    } else {
                        if (getIndex() % 2 == 0) {
                            setStyle("-fx-background-color: " + Theme.PANEL_BG + ";");
                        } else {
                            setStyle("-fx-background-color: " + Theme.BG + ";");
                        }
                    }
                }
            };
            return row;
        });

        TableColumn<ListAccountsWindow.AccountRow, String> colAccNo = new TableColumn<>("Account Number");
        colAccNo.setCellValueFactory(cellData -> cellData.getValue().accountNoProperty());
        colAccNo.setStyle("-fx-background-color: " + Theme.PANEL_BG + "; -fx-text-fill: " + Theme.FG + ";");

        TableColumn<ListAccountsWindow.AccountRow, String> colBalance = new TableColumn<>("Balance");
        colBalance.setCellValueFactory(cellData -> cellData.getValue().balanceProperty());
        colBalance.setStyle("-fx-background-color: " + Theme.PANEL_BG + "; -fx-alignment: CENTER-RIGHT; -fx-text-fill: " + Theme.FG + ";");

        accountTable.getColumns().addAll(colAccNo, colBalance);

        // Load accounts
        loadAccounts(accountTable);

        // Separator with custom style
        Separator separator1 = new Separator();
        separator1.setStyle("-fx-background-color: " + Theme.BORDER_COLOR + ";");
        Separator separator2 = new Separator();
        separator2.setStyle("-fx-background-color: " + Theme.BORDER_COLOR + ";");

        contentArea.getChildren().addAll(
                titleLabel,
                welcomeMsg,
                separator1,
                quickActionsLabel,
                actionGrid,
                separator2,
                accountSummaryLabel,
                accountTable
        );

        // Main layout
        BorderPane mainLayout = new BorderPane();
        mainLayout.setLeft(sidebar);
        mainLayout.setCenter(contentArea);

        Scene scene = new Scene(mainLayout, 1000, 700);
        stage.setTitle("Banking Application - Dashboard");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Creates a dashboard button with title and description
     * Using dark theme styling
     */
    private Button createDashboardButton(String title, String description) {
        Button btn = new Button();
        btn.setStyle("-fx-background-color: " + Theme.PANEL_BG + "; -fx-border-color: " + Theme.BORDER_COLOR +
                "; -fx-border-radius: 3; -fx-cursor: hand;");

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: " + Theme.FG + ";");

        Label descLabel = new Label(description);
        descLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #888;");

        VBox content = new VBox(5, titleLabel, descLabel);
        content.setPadding(new Insets(15));
        content.setPrefWidth(200);
        content.setPrefHeight(100);

        btn.setGraphic(content);

        // Hover effect with dark theme colors
        btn.setOnMouseEntered(e ->
                btn.setStyle("-fx-background-color: #343638; -fx-border-color: " + Theme.SIDEBAR_BG + "; -fx-cursor: hand; -fx-border-radius: 3;")
        );

        btn.setOnMouseExited(e ->
                btn.setStyle("-fx-background-color: " + Theme.PANEL_BG + "; -fx-border-color: " + Theme.BORDER_COLOR + "; -fx-cursor: hand; -fx-border-radius: 3;")
        );

        return btn;
    }

    /**
     * Loads account data from server
     */
    private void loadAccounts(TableView<ListAccountsWindow.AccountRow> table) {
        table.getItems().clear();

        String cmd = "{\"command\":\"LIST\"}";
        MainApp.network.sendLine(cmd);

        String response = null;
        try {
            response = MainApp.network.readLine();
        } catch (Exception ex) {
            System.err.println("Error loading accounts: " + ex.getMessage());
            return;
        }

        if (response != null) {
            try {
                JSONObject json = new JSONObject(response);
                if (json.has("accounts")) {
                    JSONArray accounts = json.getJSONArray("accounts");
                    for (int i = 0; i < accounts.length() && i < 3; i++) { // Show only first 3 accounts
                        JSONObject acc = accounts.getJSONObject(i);
                        String accNo = acc.getString("accountNo");
                        double balance = acc.getDouble("balance");
                        table.getItems().add(new ListAccountsWindow.AccountRow(accNo, String.format("%.2f TL", balance)));
                    }
                }
            } catch (Exception ex) {
                System.err.println("Parse error: " + ex.getMessage());
            }
        }
    }
}