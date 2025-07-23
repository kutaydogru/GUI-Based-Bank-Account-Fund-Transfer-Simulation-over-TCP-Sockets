package client.gui;

import javafx.stage.Stage;
import javafx.scene.Scene;

public class WindowUtil {
    public static void keepStageState(Stage stage, Scene scene) {
        boolean wasFullScreen = stage.isFullScreen();
        boolean wasMaximized = stage.isMaximized();
        double winW = stage.getWidth();
        double winH = stage.getHeight();
        double winX = stage.getX();
        double winY = stage.getY();
        stage.setScene(scene);
        stage.setFullScreen(wasFullScreen);
        stage.setMaximized(wasMaximized);
        if (!wasFullScreen && !wasMaximized) {
            stage.setWidth(winW);
            stage.setHeight(winH);
            stage.setX(winX);
            stage.setY(winY);
        }
        stage.show();
    }
}