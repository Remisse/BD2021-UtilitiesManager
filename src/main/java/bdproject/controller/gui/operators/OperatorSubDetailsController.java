package bdproject.controller.gui.operators;

import bdproject.controller.gui.AbstractSubscriptionDetailsController;
import bdproject.controller.gui.Controller;
import bdproject.model.SessionHolder;
import bdproject.tables.pojos.Contratti;
import bdproject.tables.pojos.ContrattiAttivi;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import javax.sql.DataSource;

public class OperatorSubDetailsController extends AbstractSubscriptionDetailsController {

    @FXML private Button back;
    @FXML private Button insertEndRequestButton;
    @FXML private Button deleteRequestButton;

    protected OperatorSubDetailsController(final Stage stage, final DataSource dataSource,
            final SessionHolder holder, final ContrattiAttivi subscription) {
        super(stage, dataSource, holder, subscription);
    }

    public static Controller create(final Stage stage, final DataSource dataSource, final SessionHolder holder,
            final ContrattiAttivi subscription) {
        return new OperatorSubDetailsController(stage, dataSource, holder, subscription);
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
    protected Controller getBackController() {
        return null;
    }
}
