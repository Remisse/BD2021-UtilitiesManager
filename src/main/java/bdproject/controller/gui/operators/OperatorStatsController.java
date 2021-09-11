package bdproject.controller.gui.operators;

import bdproject.controller.gui.AbstractController;
import bdproject.controller.gui.Controller;
import bdproject.model.SessionHolder;
import javafx.fxml.Initializable;
import javafx.stage.Stage;

import javax.sql.DataSource;
import java.net.URL;
import java.util.ResourceBundle;

public class OperatorStatsController extends AbstractController implements Initializable {

    private static final String FXML_FILE = "operatorStats.fxml";

    private OperatorStatsController(Stage stage, DataSource dataSource, SessionHolder holder) {
        super(stage, dataSource, holder, FXML_FILE);
    }

    public static Controller create(final Stage stage, final DataSource dataSource, final SessionHolder holder) {
        return new OperatorStatsController(stage, dataSource, holder);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
