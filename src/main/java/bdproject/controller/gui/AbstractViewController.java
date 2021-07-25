package bdproject.controller.gui;

import bdproject.utils.FXUtils;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import javax.sql.DataSource;
import java.io.IOException;

public abstract class AbstractViewController implements ViewController {

    private final Stage stage;
    private final DataSource dataSource;
    private final String fxml;

    protected AbstractViewController(final Stage stage, final DataSource dataSource, final String fxml) {
        this.stage = stage;
        this.dataSource = dataSource;
        this.fxml = fxml;
    }

    @Override
    public void switchTo(ViewController controller) {
        final FXMLLoader loader = new FXMLLoader(ClassLoader.getSystemResource(controller.getFxml()));
        loader.setController(controller);
        Pane pane = null;
        try {
            pane = loader.load();
        } catch (IOException e) {
            FXUtils.showError(e.getMessage());
        }
        assert pane != null;
        stage.setScene(new Scene(pane));
    }

    protected Stage getStage() {
        return stage;
    }

    protected DataSource getDataSource() {
        return dataSource;
    }

    @Override
    public String getFxml() {
        return fxml;
    }
}
