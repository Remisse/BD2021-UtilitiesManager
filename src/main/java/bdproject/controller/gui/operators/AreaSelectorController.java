package bdproject.controller.gui.operators;

import bdproject.controller.gui.AbstractController;
import bdproject.controller.gui.HomeController;
import bdproject.controller.gui.Controller;
import bdproject.model.Queries;
import bdproject.model.SessionHolder;
import bdproject.utils.ViewUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import javax.sql.DataSource;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ResourceBundle;

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
    private void goToAssignments() {
        switchTo(AssignmentsController.create(stage(), dataSource(), getSessionHolder()));
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
