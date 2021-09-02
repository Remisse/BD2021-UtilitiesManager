package bdproject.controller.gui.users;

import bdproject.controller.gui.AbstractActivationRequestDetailsController;
import bdproject.controller.gui.ViewController;
import bdproject.tables.pojos.RichiesteAttivazione;
import javafx.stage.Stage;

import javax.sql.DataSource;

public class UserActivationRequestDetailsController extends AbstractActivationRequestDetailsController {

    protected UserActivationRequestDetailsController(Stage stage, DataSource dataSource, RichiesteAttivazione request) {
        super(stage, dataSource, request);
    }

    public static ViewController create(final Stage stage, final DataSource dataSource, final RichiesteAttivazione request) {
        return new UserActivationRequestDetailsController(stage, dataSource, request);
    }
}
