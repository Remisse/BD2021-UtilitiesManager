package bdproject.controller.gui.adminarea;

import bdproject.controller.gui.AbstractSubscriptionDetailsController;
import bdproject.controller.gui.ViewController;
import bdproject.tables.Interruzioni;
import bdproject.tables.pojos.Contratti;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

import javax.sql.DataSource;

public class AdminReviewRequestController extends AbstractSubscriptionDetailsController {

    @FXML private Button back;
    @FXML private Button endSubscription;
    @FXML private Label interruptionLabel;
    @FXML private TableView<Interruzioni> interruptionTable;

    protected AdminReviewRequestController(Stage stage, DataSource dataSource, Contratti subscription) {
        super(stage, dataSource, subscription);
    }

    public static ViewController create(final Stage stage, final DataSource dataSource, final Contratti subscription) {
        return new AdminReviewRequestController(stage, dataSource, subscription);
    }

    @Override
    protected void setOther() {
        back.setVisible(false);
        endSubscription.setVisible(false);
        interruptionLabel.setVisible(false);
        interruptionTable.setVisible(false);
    }

    @Override
    protected void abstractDoEndSubscription() {
    }

    @Override
    protected ViewController getBackController() {
        return null;
    }
}
