package bdproject.controller.gui.operators;

import bdproject.controller.gui.AbstractActivationRequestDetailsController;
import bdproject.controller.gui.Controller;
import bdproject.model.SessionHolder;
import bdproject.tables.pojos.RichiesteAttivazione;
import javafx.stage.Stage;

import javax.sql.DataSource;

public class OperatorActivationRequestDetailsController extends AbstractActivationRequestDetailsController {

    protected OperatorActivationRequestDetailsController(final Stage stage, final DataSource dataSource,
            final SessionHolder holder, final RichiesteAttivazione request) {
        super(stage, dataSource, holder, request);
    }

    public static Controller create(final Stage stage, final DataSource dataSource, final SessionHolder holder,
            final RichiesteAttivazione request) {
        return new OperatorActivationRequestDetailsController(stage, dataSource, holder, request);
    }
}
