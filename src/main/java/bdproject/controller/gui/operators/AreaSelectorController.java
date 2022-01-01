package bdproject.controller.gui.operators;

import bdproject.controller.gui.AbstractController;
import bdproject.controller.gui.HomeController;
import bdproject.controller.gui.Controller;
import bdproject.model.SessionHolder;
import javafx.fxml.FXML;
import javafx.stage.Stage;

import javax.sql.DataSource;

public class AreaSelectorController extends AbstractController {

    private static final String FXML_FILE = "adminChooseArea.fxml";

    private AreaSelectorController(final Stage stage, final DataSource dataSource, final SessionHolder holder) {
        super(stage, dataSource, holder, FXML_FILE);
    }

    public static Controller create(final Stage stage, final DataSource dataSource, final SessionHolder holder) {
        return new AreaSelectorController(stage, dataSource, holder);
    }

    @FXML
    private void goToRequestsArea() {
        switchTo(RequestManagementController.create(stage(), dataSource(), getSessionHolder()));
    }

    @FXML
    private void goToSubsArea() {
        switchTo(SubscriptionManagementController.create(stage(), dataSource(), getSessionHolder()));
    }

    @FXML
    private void goToCatalogue() {
        switchTo(CatalogueManagementController.create(stage(), dataSource(), getSessionHolder()));
    }

    @FXML
    private void goToNewOperatorSignUp() {
        switchTo(OperatorSignUpController.create(stage(), dataSource(), getSessionHolder()));
    }

    @FXML
    private void goToMisc() {
        switchTo(MiscController.create(stage(), dataSource(), getSessionHolder()));
    }

    @FXML
    private void goToPremisesAndMeters() {
        switchTo(PremiseManagementController.create(stage(), dataSource(), getSessionHolder()));
    }

    @FXML
    private void goToStats() {
        switchTo(OperatorStatsController.create(stage(), dataSource(), getSessionHolder()));
    }

    @FXML
    private void goBack() {
        switchTo(HomeController.create(stage(), dataSource(), getSessionHolder()));
    }
}
