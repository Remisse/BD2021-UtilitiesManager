package bdproject.controller.gui.adminarea;

import bdproject.controller.gui.AbstractViewController;
import bdproject.controller.gui.ViewController;
import bdproject.tables.pojos.Bollette;
import bdproject.tables.pojos.Contratti;
import bdproject.tables.pojos.Letture;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

import javax.sql.DataSource;
import java.net.URL;
import java.util.ResourceBundle;

public class AdminSubManagementController extends AbstractViewController implements Initializable {

    private static final String FXML_FILE = "adminSubManagement.fxml";
    @FXML
    private TableView<Contratti> subsTable;
    @FXML
    private ComboBox<String> statusFilter;
    @FXML
    private TableView<Contratti> requestTable;
    @FXML
    private TableView<Letture> measurementsTable;
    @FXML
    private Button back;
    @FXML
    private Button interruptButton;
    @FXML
    private Button reviewRequest;
    @FXML
    private Button publishReport;
    @FXML
    private TableView<Bollette> reportsTable;


    private AdminSubManagementController(Stage stage, DataSource dataSource) {
        super(stage, dataSource, FXML_FILE);
    }

    public static ViewController create(final Stage stage, final DataSource dataSource) {
        return new AdminSubManagementController(stage, dataSource);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @FXML
    private void updateSubsTable() {

    }
}
