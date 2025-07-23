package client.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.json.*;

public class ListAccountsWindow {
    private final String username;
    private final Stage stage;

    public ListAccountsWindow(Stage stage, String username) {
        this.stage = stage;
        this.username = username;
    }

    public void show() {
        Label lblTitle = new Label("Your Accounts");
        lblTitle.setStyle("-fx-font-weight: bold; -fx-text-fill:" + Theme.FG + ";");

        VBox accBox = new VBox(10);
        accBox.setAlignment(Pos.TOP_CENTER);

        String cmd = "{\"command\":\"LIST\"}";
        MainApp.network.sendLine(cmd);
        String response = null;
        try { response = MainApp.network.readLine(); }
        catch (Exception ex) {
            accBox.getChildren().add(new Label("Server error: " + ex.getMessage()));
        }

        if (response != null && response.contains("\"accounts\"")) {
            try {
                JSONObject json = new JSONObject(response);
                JSONArray arr = json.getJSONArray("accounts");
                if (arr.length() == 0) {
                    Label lbl = new Label("You have no accounts.");
                    lbl.setStyle("-fx-text-fill:" + Theme.FG + ";");
                    accBox.getChildren().add(lbl);
                } else {
                    for (int i = 0; i < arr.length(); ++i) {
                        JSONObject ob = arr.getJSONObject(i);
                        String accNo = ob.getString("accountNo");
                        double balance = ob.getDouble("balance");
                        Label lbl = new Label("Account: " + accNo + " | Balance: " + balance + " TL");
                        lbl.setStyle("-fx-text-fill: " + Theme.FG + ";");
                        accBox.getChildren().add(lbl);
                    }
                }
            } catch (Exception ex) {
                accBox.getChildren().add(new Label("JSON parse error: " + ex.getMessage()));
            }
        } else if (response != null) {
            accBox.getChildren().add(new Label("Account info could not be loaded:\n" + response));
        }

        Button btnBack = new Button("Back");
        btnBack.setStyle(Theme.BTN);
        btnBack.setOnAction(e -> new MainMenuWindow(stage, username).show());

        VBox vbox = new VBox(18, lblTitle, accBox, btnBack);
        vbox.setAlignment(Pos.TOP_CENTER);
        vbox.setPadding(new Insets(32,32,32,32));

        StackPane root = new StackPane(vbox);
        root.setStyle("-fx-background-color:" + Theme.BG + "; -fx-font-size: 16px;");
        Scene scene = new Scene(root, 440, 320);

        scene.widthProperty().addListener((obs, oldVal, newVal) -> updateResponsive(vbox, lblTitle, scene));
        scene.heightProperty().addListener((obs, oldVal, newVal) -> updateResponsive(vbox, lblTitle, scene));
        updateResponsive(vbox, lblTitle, scene);

        WindowUtil.keepStageState(stage, scene);
    }

    private void updateResponsive(VBox vbox, Label lblTitle, Scene scene) {
        double h = scene.getHeight();
        int titleFont = (int) Math.max(18, Math.min(32, h * 0.08));
        int spacing = (int) Math.max(10, Math.min(28, h * 0.05));
        int pad = (int) Math.max(12, Math.min(44, h * 0.12));
        lblTitle.setStyle("-fx-font-size: " + titleFont + "px; -fx-font-weight: bold; -fx-text-fill:" + Theme.FG + ";");
        vbox.setSpacing(spacing);
        vbox.setPadding(new Insets(pad, pad, pad, pad));
    }
}