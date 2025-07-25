package client.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.stage.Stage;

/**
 * Left sidebar menu component used across all application screens.
 * Provides navigation to different pages and logout functionality.
 */
public class SideBarMenu extends VBox {
    private static final String MENU_ITEM_STYLE = "-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 14px; -fx-alignment: center-left; -fx-padding: 10 15;";
    private static final String MENU_ITEM_ACTIVE = "-fx-background-color: #1e3e7d; -fx-text-fill: white; -fx-font-size: 14px; -fx-alignment: center-left; -fx-padding: 10 15;";
    private static final String MENU_HEADER_STYLE = "-fx-text-fill: white; -fx-font-size: 20px; -fx-font-weight: bold;";

    private final Stage stage;
    private final String username;

    /**
     * Creates a new sidebar menu
     *
     * @param stage The application stage
     * @param username The logged-in username
     * @param activeMenu The ID of the currently active menu item
     */
    public SideBarMenu(Stage stage, String username, String activeMenu) {
        super(5);
        this.stage = stage;
        this.username = username;

        setPrefWidth(220);
        setMinWidth(220);
        setStyle("-fx-background-color: " + Theme.SIDEBAR_BG + ";");
        setPadding(new Insets(15, 10, 10, 10));

        // App logo and title
        HBox titleBox = new HBox(10);
        titleBox.setAlignment(Pos.CENTER_LEFT);

        SVGPath bankIcon = new SVGPath();
        bankIcon.setContent("M12,13.5V10L16,7L20,10V13.5H12M21,11.5V10.5L16,6.5L11,10.5V11.5L16,7.5L21,11.5M16,18L21,14.5V18H16M11,14.5L16,18V21H11V14.5M21,13V21H11V13H21M11,3H13V5H11V3M5,3H7V5H5V3M5,7H7V9H5V7M5,11H7V13H5V11M5,15H7V17H5V15M5,19H7V21H5V19Z");
        bankIcon.setFill(Color.WHITE);
        bankIcon.setScaleX(1.5);
        bankIcon.setScaleY(1.5);

        Label appTitle = new Label("Bank App");
        appTitle.setStyle(MENU_HEADER_STYLE);

        titleBox.getChildren().addAll(bankIcon, appTitle);
        titleBox.setPadding(new Insets(0, 0, 15, 5));

        // Welcome message
        Label welcomeLabel = new Label("Welcome, " + username);
        welcomeLabel.setStyle("-fx-text-fill: white; -fx-font-size: 12px; -fx-font-style: italic; -fx-padding: 5 0 15 5;");

        // Menu items with icons
        VBox menuItems = new VBox(3);
        menuItems.getChildren().addAll(
                createMenuItem("Dashboard", "M13,3V9H21V3M13,21H21V11H13M3,21H11V15H3M3,13H11V3H3V13Z", "dashboard", activeMenu, true),
                createMenuItem("My Accounts", "M11.5,1L2,6V8H21V6M16,10V17H19V10M2,22H21V19H2M10,10V17H13V10M4,10V17H7V10H4Z", "accounts", activeMenu),
                createMenuItem("Transfer Money", "M15,14V11H18V9L22,12.5L18,16V14H15M14,7.7V9H2V7.7L8,4L14,7.7M7,10H9V15H7V10M3,10H5V15H3V10M13,10V12.5L11,14.3V10H13M9.1,16L8.5,16.5L10.2,18H2V16H9.1M17,15V18H14V20L10,16.5L14,13V15H17Z", "transfer", activeMenu),
                createMenuItem("Deposit", "M5,6H23V18H5V6M14,9A3,3 0 0,1 17,12A3,3 0 0,1 14,15A3,3 0 0,1 11,12A3,3 0 0,1 14,9M9,8A2,2 0 0,1 7,10V14A2,2 0 0,1 9,16H19A2,2 0 0,1 21,14V10A2,2 0 0,1 19,8H9M1,10H3V20H19V22H1V10Z", "deposit", activeMenu),
                createMenuItem("Withdraw", "M15,14V11H18V9L22,12.5L18,16V14H15M14,7.7V9H2V7.7L8,4L14,7.7M7,10H9V15H7V10M3,10H5V15H3V10M13,10V12.5L11,14.3V10H13M9.1,16L8.5,16.5L10.2,18H2V16H9.1M17,15V18H14V20L10,16.5L14,13V15H17Z", "withdraw", activeMenu),
                createMenuItem("Create Account", "M19,13H13V19H11V13H5V11H11V5H13V11H19V13Z", "create", activeMenu)
        );

        // Spacer to push logout to bottom
        VBox spacer = new VBox();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        // Separator
        Separator separator = new Separator();
        separator.setStyle("-fx-background-color: rgba(255, 255, 255, 0.3);");

        // Logout button
        Button logoutBtn = createMenuButton("Logout", "M17,17.25V14H10V10H17V6.75L22.25,12L17,17.25M13,2A2,2 0 0,1 15,4V8H13V4H4V20H13V16H15V20A2,2 0 0,1 13,22H4A2,2 0 0,1 2,20V4A2,2 0 0,1 4,2H13Z", false);
        logoutBtn.setOnAction(e -> {
            String cmd = "{\"command\":\"LOGOUT\"}";
            MainApp.network.sendLine(cmd);
            try { MainApp.network.readLine(); } catch (Exception ex) {}
            new LoginWindow(stage).show();
        });

        getChildren().addAll(titleBox, welcomeLabel, menuItems, spacer, separator, logoutBtn);
    }

    /**
     * Creates a menu item button without the dashboard flag
     */
    private Button createMenuItem(String text, String svgPath, String menuId, String activeMenu) {
        return createMenuItem(text, svgPath, menuId, activeMenu, false);
    }

    /**
     * Creates a menu item button with icon
     *
     * @param text Text label for the menu item
     * @param svgPath SVG path data for the icon
     * @param menuId Identifier for the menu item
     * @param activeMenu Currently active menu ID
     * @param isDashboard Whether this is the dashboard item
     * @return A styled Button for the menu
     */
    private Button createMenuItem(String text, String svgPath, String menuId, String activeMenu, boolean isDashboard) {
        Button btn = createMenuButton(text, svgPath, menuId.equals(activeMenu));

        btn.setOnAction(e -> {
            System.out.println("Menu item clicked: " + menuId); // For debugging

            switch (menuId) {
                case "dashboard":
                    new MainMenuWindow(stage, username).show();
                    break;
                case "accounts":
                    new ListAccountsWindow(stage, username).show();
                    break;
                case "transfer":
                    new TransferMoneyWindow(stage, username).show();
                    break;
                case "deposit":
                    new DepositWindow(stage, username).show();
                    break;
                case "withdraw":
                    new WithdrawWindow(stage, username).show();
                    break;
                case "create":
                    new CreateAccountWindow(stage, username).show();
                    break;
                default:
                    System.out.println("Unknown menu ID: " + menuId);
                    break;
            }
        });

        return btn;
    }

    /**
     * Creates a styled button for the menu
     *
     * @param text Button text
     * @param svgPath Icon SVG path
     * @param isActive Whether the button is currently active
     * @return A styled Button instance
     */
    private Button createMenuButton(String text, String svgPath, boolean isActive) {
        Button btn = new Button();
        btn.setStyle(isActive ? MENU_ITEM_ACTIVE : MENU_ITEM_STYLE);
        btn.setMaxWidth(Double.MAX_VALUE);

        SVGPath icon = new SVGPath();
        icon.setContent(svgPath);
        icon.setFill(Color.WHITE);

        Label label = new Label(text);
        label.setStyle("-fx-text-fill: white;");
        HBox.setMargin(label, new Insets(0, 0, 0, 10));

        HBox content = new HBox(10);
        content.setAlignment(Pos.CENTER_LEFT);
        content.getChildren().addAll(icon, label);

        btn.setGraphic(content);
        btn.setAlignment(Pos.CENTER_LEFT);

        // Hover effect - enhanced for dark theme
        btn.setOnMouseEntered(e -> {
            if (!isActive) btn.setStyle(MENU_ITEM_STYLE + "-fx-background-color: rgba(255, 255, 255, 0.2);"); // Increased visibility
        });

        btn.setOnMouseExited(e -> {
            if (!isActive) btn.setStyle(MENU_ITEM_STYLE);
        });

        return btn;
    }
}