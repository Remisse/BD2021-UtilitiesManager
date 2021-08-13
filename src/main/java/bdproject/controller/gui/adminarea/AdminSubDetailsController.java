package bdproject.controller.gui.adminarea;

import bdproject.controller.gui.AbstractSubscriptionDetailsController;
import bdproject.controller.gui.ViewController;
import bdproject.tables.pojos.Contratti;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.stage.Stage;

import javax.sql.DataSource;

public class AdminSubDetailsController extends AbstractSubscriptionDetailsController implements Initializable {

    protected AdminSubDetailsController(Stage stage, DataSource dataSource, Contratti subscription) {
        super(stage, dataSource, subscription);
    }

    @Override
    protected void setOther() {
        getEndSubscription().setVisible(false);
    }

    @Override
    protected ViewController getBackController() {
        return AdminSubManagementController.create(getStage(), getDataSource());
    }

    public static ViewController create(final Stage stage, final DataSource dataSource, final Contratti subscription) {
        return new AdminSubDetailsController(stage, dataSource, subscription);
    }

    @FXML
    private void doEndSubscription() {
    }
}
