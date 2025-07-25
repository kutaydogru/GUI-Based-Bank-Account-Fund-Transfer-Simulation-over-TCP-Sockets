package client.gui;

import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

/**
 * Utility class providing common window-related helper functions
 * used across multiple screens.
 */
public class WindowUtil {

    /**
     * Creates a styled error dialog with the given title and content
     *
     * @param title Dialog title
     * @param content Error message
     */
    public static void showErrorDialog(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);

        // Apply dark theme
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setStyle("-fx-background-color: " + Theme.PANEL_BG + ";" +
                "-fx-text-fill: " + Theme.FG + ";");

        // Style all labels in the dialog
        dialogPane.lookupAll(".label").forEach(label ->
                ((Label)label).setStyle("-fx-text-fill: " + Theme.FG + ";")
        );

        alert.showAndWait();
    }

    /**
     * Creates a styled information dialog with the given title and content
     *
     * @param title Dialog title
     * @param content Information message
     */
    public static void showInfoDialog(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);

        // Apply dark theme
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setStyle("-fx-background-color: " + Theme.PANEL_BG + ";" +
                "-fx-text-fill: " + Theme.FG + ";");

        // Style all labels in the dialog
        dialogPane.lookupAll(".label").forEach(label ->
                ((Label)label).setStyle("-fx-text-fill: " + Theme.FG + ";")
        );

        alert.showAndWait();
    }

    /**
     * Shows a detailed exception dialog with stack trace
     *
     * @param title Dialog title
     * @param header Header text
     * @param content Error message
     * @param ex The exception to display
     */
    public static void showExceptionDialog(String title, String header, String content, Exception ex) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);

        // Create expandable Exception section
        if (ex != null) {
            StringBuffer sb = new StringBuffer();
            sb.append(ex.toString()).append("\n\n");
            for (StackTraceElement ste : ex.getStackTrace()) {
                sb.append(ste.toString()).append("\n");
            }

            TextArea textArea = new TextArea(sb.toString());
            textArea.setEditable(false);
            textArea.setWrapText(true);
            textArea.setStyle("-fx-background-color: " + Theme.INPUT_BG + ";" +
                    "-fx-text-fill: " + Theme.FG + ";");

            GridPane expContent = new GridPane();
            expContent.setMaxWidth(Double.MAX_VALUE);
            expContent.add(textArea, 0, 0);
            GridPane.setVgrow(textArea, Priority.ALWAYS);
            GridPane.setHgrow(textArea, Priority.ALWAYS);

            // Apply dark theme
            Region region = (Region) textArea.lookup(".content");
            region.setStyle("-fx-background-color: " + Theme.INPUT_BG + ";");

            alert.getDialogPane().setExpandableContent(expContent);
        }

        // Apply dark theme to the dialog
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setStyle("-fx-background-color: " + Theme.PANEL_BG + ";" +
                "-fx-text-fill: " + Theme.FG + ";");

        // Style all labels in the dialog
        dialogPane.lookupAll(".label").forEach(label ->
                ((Label)label).setStyle("-fx-text-fill: " + Theme.FG + ";")
        );

        alert.showAndWait();
    }
}