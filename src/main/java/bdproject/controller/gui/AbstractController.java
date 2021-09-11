package bdproject.controller.gui;

import bdproject.model.Session;
import bdproject.model.SessionHolder;
import bdproject.utils.FXUtils;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import javax.sql.DataSource;
import java.io.IOException;

public abstract class AbstractController implements Controller {

    private final Stage stage;
    private final DataSource dataSource;
    private final SessionHolder sessionHolder;
    private final String fxml;

    protected AbstractController(final Stage stage, final DataSource dataSource, final SessionHolder holder,
            final String fxml) {
        this.stage = stage;
        this.dataSource = dataSource;
        this.sessionHolder = holder;
        this.fxml = fxml;
    }

    private Scene createNewScene(Controller controller) {
        final FXMLLoader loader = new FXMLLoader(ClassLoader.getSystemResource(controller.fxml()));
        loader.setController(controller);
        Pane pane = null;
        try {
            pane = loader.load();
        } catch (IOException e) {
            FXUtils.showError(e.getMessage());
        }
        if (pane == null) {
            throw new IllegalStateException("Pane should not be null!");
        }
        return new Scene(pane);
    }

    @Override
    public void switchTo(Controller controller) {
        stage.setScene(createNewScene(controller));
        stage.show();
    }

    @Override
    public void createSubWindow(Controller controller) {
        Stage subStage = new Stage();
        subStage.setScene(createNewScene(controller));
        subStage.show();
    }

    @Override
    public SessionHolder sessionHolder() {
        return sessionHolder;
    }

    protected Stage stage() {
        return stage;
    }

    protected DataSource dataSource() {
        return dataSource;
    }

    @Override
    public String fxml() {
        return fxml;
    }
}
