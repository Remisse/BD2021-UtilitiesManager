package bdproject.controller.gui.users;

import bdproject.controller.gui.AbstractActivationRequestDetailsController;
import bdproject.controller.gui.Controller;
import bdproject.model.SessionHolder;
import bdproject.tables.pojos.RichiesteContratto;
import javafx.stage.Stage;

import javax.sql.DataSource;

public class UserActivationRequestDetailsController extends AbstractActivationRequestDetailsController {

    protected UserActivationRequestDetailsController(final Stage stage, final DataSource dataSource,
            final SessionHolder holder, final RichiesteContratto request) {
        super(stage, dataSource, holder, request);
    }

    public static Controller create(final Stage stage, final DataSource dataSource, final SessionHolder holder,
            final RichiesteContratto request) {
        return new UserActivationRequestDetailsController(stage, dataSource, holder, request);
    }
}
