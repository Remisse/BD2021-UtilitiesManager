package bdproject.controller.gui.adminarea;

import bdproject.controller.gui.AbstractSubscriptionDetailsController;
import bdproject.controller.gui.ViewController;
import bdproject.tables.pojos.ContrattiDettagliati;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import javax.sql.DataSource;

public class AdminSubDetailsController extends AbstractSubscriptionDetailsController {

    @FXML private Button back;
    @FXML private Button insertEndRequestButton;
    @FXML private Button deleteRequestButton;

    protected AdminSubDetailsController(Stage stage, DataSource dataSource, ContrattiDettagliati subscription) {
        super(stage, dataSource, subscription);
    }

    public static ViewController create(final Stage stage, final DataSource dataSource,
            final ContrattiDettagliati subscription) {
        return new AdminSubDetailsController(stage, dataSource, subscription);
    }

    @Override
    protected void setOther() {
        back.setVisible(false);
        insertEndRequestButton.setVisible(false);
        deleteRequestButton.setVisible(false);
    }

    /**
     * Unimplemented
     */
    @Override
    protected void abstractDoDeleteEndRequest() {

    }

    /**
     * Unimplemented
     */
    @Override
    protected void abstractDoInsertEndRequest() {
    }

    @Override
    protected ViewController getBackController() {
        return null;
    }
}
