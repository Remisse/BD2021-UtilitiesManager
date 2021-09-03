package bdproject.controller.gui.operators;

import bdproject.controller.gui.AbstractViewController;
import bdproject.controller.gui.HomeController;
import bdproject.controller.gui.ViewController;
import javafx.fxml.FXML;
import javafx.stage.Stage;

import javax.sql.DataSource;

public class AreaSelectorController extends AbstractViewController {

    private static final String FXML_FILE = "adminChooseArea.fxml";

    private AreaSelectorController(Stage stage, DataSource dataSource) {
        super(stage, dataSource, FXML_FILE);
    }

    public static ViewController create(final Stage stage, final DataSource dataSource) {
        return new AreaSelectorController(stage, dataSource);
    }

    @FXML
    private void goToRequestsArea() {
        switchTo(RequestManagementController.create(getStage(), getDataSource()));
    }

    @FXML
    private void goToSubsArea() {
        switchTo(SubscriptionManagementController.create(getStage(), getDataSource()));
    }

    @FXML
    private void goToCatalogue() {
        switchTo(CatalogueManagementController.create(getStage(), getDataSource()));
    }

    @FXML
    private void goToNewOperatorSignUp() {
        switchTo(OperatorSignUpController.create(getStage(), getDataSource()));
    }

    @FXML
    private void goToMisc() {
        switchTo(MiscController.create(getStage(), getDataSource()));
    }

    @FXML
    private void goToStats() {}

    @FXML
    private void goBack() {
        switchTo(HomeController.create(getStage(), getDataSource()));
    }
}
