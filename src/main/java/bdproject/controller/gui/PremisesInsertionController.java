package bdproject.controller.gui;

import bdproject.model.SubscriptionProcess;
import javafx.fxml.Initializable;
import javafx.stage.Stage;

import javax.sql.DataSource;
import java.net.URL;
import java.util.ResourceBundle;

public class PremisesInsertionController extends AbstractViewController implements Initializable {

    private static final String fxml = "premises.fxml";
    private final SubscriptionProcess process;

    private PremisesInsertionController(final Stage stage, final DataSource dataSource, final String fxml,
            final SubscriptionProcess process) {
        super(stage, dataSource, fxml);
        this.process = process;
    }

    public static ViewController create(final Stage stage, final DataSource dataSource,
            final SubscriptionProcess process) {
        return new PremisesInsertionController(stage, dataSource, fxml, process);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
