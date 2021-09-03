package bdproject.controller.gui.operators;

import bdproject.controller.gui.AbstractViewController;
import bdproject.controller.gui.ViewController;
import javafx.fxml.Initializable;
import javafx.stage.Stage;

import javax.sql.DataSource;
import java.net.URL;
import java.util.ResourceBundle;

public class CatalogueManagementController extends AbstractViewController implements Initializable {

    private static final String FXML_FILE = "catalogueManagement.fxml";

    private CatalogueManagementController(Stage stage, DataSource dataSource) {
        super(stage, dataSource, FXML_FILE);
    }

    public static ViewController create(final Stage stage, final DataSource dataSource) {
        return new CatalogueManagementController(stage, dataSource);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
