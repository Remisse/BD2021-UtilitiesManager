package bdproject.utils;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public class ViewUtils {

    private ViewUtils() {}

    private static void showBlocking(final Alert.AlertType type, final String message) {
        Alert alert = new Alert(type, message, ButtonType.CLOSE);
        Platform.runLater(alert::showAndWait);
    }

    private static void showNonblocking(final Alert.AlertType type, final String message) {
        Alert alert = new Alert(type, message, ButtonType.CLOSE);
        Platform.runLater(alert::show);
    }

    public static void showBlockingWarning(final String message) {
        showBlocking(Alert.AlertType.NONE, message);
    }

    public static void showNonblockingWarning(final String message) {
        showNonblocking(Alert.AlertType.NONE, message);
    }

    public static void showError(final String message) {
        showBlocking(Alert.AlertType.ERROR, message);
    }

    public static void showConfirmationDialog(final String text, final Runnable yesAction) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, text, ButtonType.YES, ButtonType.NO);
        Platform.runLater(() -> alert.showAndWait().ifPresent(b -> {
            if (b == ButtonType.YES) {
                yesAction.run();
            }
        }));
    }
}
