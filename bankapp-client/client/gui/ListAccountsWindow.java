package client.gui;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.json.*;

/**
 * Screen showing all user accounts with account management options.
 * Updated with dark theme table styles.
 */
public class ListAccountsWindow {
    private final Stage stage;
    private final String username;

    public ListAccountsWindow(Stage stage, String username) {
        this.stage = stage;
        this.username = username;
    }

    public void show() {
        // Create sidebar
        SideBarMenu sidebar = new SideBarMenu(stage, username, "accounts");

        // Main content area
        VBox contentArea = new VBox(20);
        contentArea.setStyle("-fx-background-color: " + Theme.CONTENT_BG + ";");
        contentArea.setPadding(new Insets(30));

        // Page title
        Label titleLabel = new Label("My Accounts");
        titleLabel.setStyle(Theme.PAGE_TITLE);

        // Action buttons
        Button btnCreateAccount = new Button("Create New Account");
        btnCreateAccount.setStyle(Theme.SUCCESS_BTN);
        btnCreateAccount.setOnAction(e -> new CreateAccountWindow(stage, username).show());

        // Toolbar
        HBox toolbar = new HBox(10);
        Label activeAccountsLabel = new Label("Your active accounts:");
        activeAccountsLabel.setStyle("-fx-text-fill: " + Theme.FG + ";");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        toolbar.getChildren().addAll(activeAccountsLabel, spacer, btnCreateAccount);

        // Accounts table with dark theme styling
        TableView<AccountRow> accountsTable = new TableView<>();
        accountsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        accountsTable.setStyle("-fx-background-color: " + Theme.PANEL_BG + "; " +
                "-fx-table-cell-border-color: " + Theme.BORDER_COLOR + ";" +
                "-fx-control-inner-background: " + Theme.PANEL_BG + ";" +
                "-fx-table-header-border-color: " + Theme.BORDER_COLOR + ";" +
                "-fx-text-background-color: " + Theme.FG + ";");

        // Custom row styling for alternating rows
        accountsTable.setRowFactory(tv -> {
            TableRow<AccountRow> row = new TableRow<AccountRow>() {
                @Override
                protected void updateItem(AccountRow item, boolean empty) {
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

        TableColumn<AccountRow, String> colAccNo = new TableColumn<>("Account Number");
        colAccNo.setCellValueFactory(cellData -> cellData.getValue().accountNoProperty());
        colAccNo.setPrefWidth(200);
        colAccNo.setStyle("-fx-background-color: " + Theme.PANEL_BG + "; -fx-text-fill: " + Theme.FG + ";");

        TableColumn<AccountRow, String> colBalance = new TableColumn<>("Balance");
        colBalance.setCellValueFactory(cellData -> cellData.getValue().balanceProperty());
        colBalance.setStyle("-fx-background-color: " + Theme.PANEL_BG + "; -fx-alignment: CENTER-RIGHT; -fx-text-fill: " + Theme.FG + ";");
        colBalance.setPrefWidth(150);

        // Action column with buttons
        TableColumn<AccountRow, Void> colActions = new TableColumn<>("Actions");
        colActions.setPrefWidth(250);
        colActions.setStyle("-fx-background-color: " + Theme.PANEL_BG + ";");
        colActions.setCellFactory(col -> new TableCell<>() {
            private final Button btnCheck = new Button("Check");
            private final Button btnDeposit = new Button("Deposit");
            private final Button btnWithdraw = new Button("Withdraw");
            private final HBox actionPane = new HBox(5, btnCheck, btnDeposit, btnWithdraw);

            {
                btnCheck.setStyle("-fx-background-color: " + Theme.SIDEBAR_BG + "; -fx-text-fill: white;");
                btnDeposit.setStyle("-fx-background-color: #2d7d4c; -fx-text-fill: white;"); // Darker green
                btnWithdraw.setStyle("-fx-background-color: #a93226; -fx-text-fill: white;"); // Darker red

                actionPane.setAlignment(Pos.CENTER);

                btnCheck.setOnAction(event -> {
                    if (getTableRow() != null && getTableRow().getItem() != null) {
                        AccountRow account = getTableRow().getItem();
                        CheckBalanceWindow checkWindow = new CheckBalanceWindow(stage, username);
                        // If setPrefilledAccountNo method exists:
                        try {
                            java.lang.reflect.Method method = checkWindow.getClass().getMethod("setPrefilledAccountNo", String.class);
                            method.invoke(checkWindow, account.getAccountNo());
                        } catch (Exception e) {
                            System.out.println("setPrefilledAccountNo method not available");
                        }
                        checkWindow.show();
                    }
                });

                btnDeposit.setOnAction(event -> {
                    if (getTableRow() != null && getTableRow().getItem() != null) {
                        AccountRow account = getTableRow().getItem();
                        DepositWindow depositWindow = new DepositWindow(stage, username);
                        // If setPrefilledAccountNo method exists:
                        try {
                            java.lang.reflect.Method method = depositWindow.getClass().getMethod("setPrefilledAccountNo", String.class);
                            method.invoke(depositWindow, account.getAccountNo());
                        } catch (Exception e) {
                            System.out.println("setPrefilledAccountNo method not available");
                        }
                        depositWindow.show();
                    }
                });

                btnWithdraw.setOnAction(event -> {
                    if (getTableRow() != null && getTableRow().getItem() != null) {
                        AccountRow account = getTableRow().getItem();
                        WithdrawWindow withdrawWindow = new WithdrawWindow(stage, username);
                        // If setPrefilledAccountNo method exists:
                        try {
                            java.lang.reflect.Method method = withdrawWindow.getClass().getMethod("setPrefilledAccountNo", String.class);
                            method.invoke(withdrawWindow, account.getAccountNo());
                        } catch (Exception e) {
                            System.out.println("setPrefilledAccountNo method not available");
                        }
                        withdrawWindow.show();
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : actionPane);
            }
        });

        accountsTable.getColumns().addAll(colAccNo, colBalance, colActions);

        // Status message
        Label statusLabel = new Label("");
        statusLabel.setStyle("-fx-text-fill: " + Theme.FG + ";");

        // Load accounts
        loadAccounts(accountsTable, statusLabel);

        contentArea.getChildren().addAll(titleLabel, toolbar, accountsTable, statusLabel);
        VBox.setVgrow(accountsTable, Priority.ALWAYS);

        // Main layout
        BorderPane mainLayout = new BorderPane();
        mainLayout.setLeft(sidebar);
        mainLayout.setCenter(contentArea);

        Scene scene = new Scene(mainLayout, 1000, 700);
        stage.setTitle("Banking Application - My Accounts");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Loads account data from server
     */
    private void loadAccounts(TableView<AccountRow> table, Label statusLabel) {
        table.getItems().clear();

        String cmd = "{\"command\":\"LIST\"}";
        System.out.println("Sending LIST command to server");
        MainApp.network.sendLine(cmd);

        String response = null;
        try {
            response = MainApp.network.readLine();
            System.out.println("Server response for LIST: " + response);
        } catch (Exception ex) {
            statusLabel.setStyle(Theme.ERROR);
            statusLabel.setText("Error connecting to server: " + ex.getMessage());
            return;
        }

        if (response != null) {
            try {
                JSONObject json = new JSONObject(response);
                if (json.has("accounts")) {
                    JSONArray accounts = json.getJSONArray("accounts");
                    if (accounts.length() == 0) {
                        statusLabel.setText("You have no accounts. Create one to get started.");
                        return;
                    }

                    for (int i = 0; i < accounts.length(); i++) {
                        JSONObject acc = accounts.getJSONObject(i);
                        String accNo = acc.getString("accountNo");
                        double balance = acc.getDouble("balance");
                        table.getItems().add(new AccountRow(accNo, String.format("%.2f TL", balance)));
                    }
                } else if (json.has("error")) {
                    statusLabel.setStyle(Theme.ERROR);
                    statusLabel.setText("Error: " + json.getString("error"));
                }
            } catch (Exception ex) {
                statusLabel.setStyle(Theme.ERROR);
                statusLabel.setText("Error parsing data: " + ex.getMessage());
            }
        } else {
            statusLabel.setStyle(Theme.ERROR);
            statusLabel.setText("No response from server");
        }
    }

    /**
     * Helper class for table data
     */
    public static class AccountRow {
        private final String accountNo;
        private final String balance;

        public AccountRow(String accountNo, String balance) {
            this.accountNo = accountNo;
            this.balance = balance;
        }

        public String getAccountNo() {
            return accountNo;
        }

        public StringProperty accountNoProperty() {
            return new SimpleStringProperty(accountNo);
        }

        public StringProperty balanceProperty() {
            return new SimpleStringProperty(balance);
        }
    }
}