package bdproject.utils;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public class FXUtils {

    private FXUtils() {}

    private static void showBlocking(Alert.AlertType type, String message) {
        Alert alert = new Alert(type, message, ButtonType.CLOSE);
        alert.showAndWait();
    }

    private static void showNonblocking(Alert.AlertType type, String message) {
        Alert alert = new Alert(type, message, ButtonType.CLOSE);
        alert.show();
    }

    public static void showBlockingWarning(String message) {
        showBlocking(Alert.AlertType.NONE, message);
    }

    public static void showNonblockingWarning(String message) {
        showNonblocking(Alert.AlertType.NONE, message);
    }

    public static void showError(String message) {
        showBlocking(Alert.AlertType.ERROR, message);
    }
}
