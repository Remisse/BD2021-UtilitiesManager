package bdproject.controller.gui.adminarea;

import bdproject.controller.gui.AbstractViewController;
import bdproject.controller.gui.HomeController;
import bdproject.controller.gui.ViewController;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import javax.sql.DataSource;

public class AdminChooseAreaController extends AbstractViewController {

    private static final String FXML_FILE = "adminChooseArea.fxml";
    @FXML
    private Button subsArea;
    @FXML
    private Button catalogue;
    @FXML
    private Button premises;
    @FXML
    private Button misc;
    @FXML
    private Button back;

    private AdminChooseAreaController(Stage stage, DataSource dataSource) {
        super(stage, dataSource, FXML_FILE);
    }

    public static ViewController create(final Stage stage, final DataSource dataSource) {
        return new AdminChooseAreaController(stage, dataSource);
    }

    @FXML
    private void goToSubsArea() {
        switchTo(AdminSubManagementController.create(getStage(), getDataSource()));
    }

    @FXML
    private void goToCatalogue() {

    }

    @FXML
    private void goToMisc() {

    }

    @FXML
    private void goBack() {
        switchTo(HomeController.create(getStage(), getDataSource()));
    }
}
