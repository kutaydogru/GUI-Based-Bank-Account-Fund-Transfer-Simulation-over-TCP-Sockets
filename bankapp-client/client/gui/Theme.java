package client.gui;

/**
 * Centralized theme class containing all styling constants and color codes
 * for consistent application appearance. This uses a dark theme based on #313335.
 */
public class Theme {
    // Dark theme main colors
    public static final String BG = "#313335";               // Main background color
    public static final String FG = "#f0f0f0";               // Main text color
    public static final String CONTENT_BG = "#2b2b2b";       // Content area background
    public static final String PANEL_BG = "#3c3f41";         // Panel background color
    public static final String INPUT_BG = "#45474a";         // Input fields background
    public static final String BORDER_COLOR = "#555555";     // Border color

    // Sidebar colors - kept blue tones
    public static final String SIDEBAR_BG = "#1a4187";       // Left menu background

    // Button styles
    public static final String PRIMARY_BTN = "-fx-background-color: #1e4b9e; -fx-text-fill: white; -fx-padding: 8 20; -fx-cursor: hand;";
    public static final String SUCCESS_BTN = "-fx-background-color: #2d7d4c; -fx-text-fill: white; -fx-padding: 8 20; -fx-cursor: hand;"; // Darker green
    public static final String DANGER_BTN = "-fx-background-color: #a93226; -fx-text-fill: white; -fx-padding: 8 20; -fx-cursor: hand;";  // Darker red

    // Input field styles
    public static final String TXT_FIELD = "-fx-background-color: " + INPUT_BG + "; -fx-border-color: " + BORDER_COLOR +
            "; -fx-text-fill: " + FG + "; -fx-border-radius: 3; -fx-padding: 8px;";

    // Other styles
    public static final String ERROR = "-fx-text-fill: #ff6b68;";      // Brighter error message
    public static final String SUCCESS = "-fx-text-fill: #6aff81;";    // Brighter success message
    public static final String LOGOUT = "-fx-background-color: transparent; -fx-text-fill: #62aaff; -fx-border-color: #62aaff; -fx-border-radius: 3;";

    // Table styles
    public static final String TABLE_HEADER = "-fx-font-weight: bold; -fx-background-color: #3c3f41; -fx-text-fill: #f0f0f0;";
    public static final String TABLE_ROW = "-fx-background-color: " + PANEL_BG + "; -fx-text-fill: " + FG + ";";
    public static final String TABLE_ROW_ALT = "-fx-background-color: #323537; -fx-text-fill: " + FG + ";";

    // Heading styles
    public static final String PAGE_TITLE = "-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #89CFF0;"; // Light blue title

    // Card/panel style for content areas
    public static final String CARD = "-fx-background-color: " + PANEL_BG + "; -fx-border-color: " + BORDER_COLOR +
            "; -fx-border-radius: 5; -fx-background-radius: 5; -fx-padding: 15;";
}