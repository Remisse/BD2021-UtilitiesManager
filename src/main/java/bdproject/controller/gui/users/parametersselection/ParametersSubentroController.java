package bdproject.controller.gui.users.parametersselection;

import bdproject.controller.gui.Controller;
import bdproject.controller.gui.users.PremiseInsertionController;
import bdproject.controller.gui.users.subscriptionconfirmation.SubentroConfirmationController;
import bdproject.model.Queries;
import bdproject.model.SessionHolder;
import bdproject.model.SubscriptionProcess;
import bdproject.tables.pojos.*;
import bdproject.utils.ViewUtils;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

public class ParametersSubentroController extends AbstractParametersSelectionController {

    private static final String FXML_FILE = "meterAndActivation.fxml";

    @FXML private Label planLabel;
    @FXML private Label utilityLabel;
    @FXML private Label useLabel;
    @FXML private Label methodLabel;
    @FXML private Label peopleNoLabel;
    @FXML private Label otherClientIdLabel;
    @FXML private Label meterIdLabel;
    @FXML private Label measurementLabel;

    @FXML private TextField otherClientIdField;
    @FXML private TextField meterIdField;
    @FXML private TextField peopleNoField;
    @FXML private TextField measurementField;

    @FXML private Button back;
    @FXML private Button next;

    private ParametersSubentroController(final Stage stage, final DataSource dataSource, final SessionHolder holder,
                                         final SubscriptionProcess process) {
        super(stage, dataSource, holder, process);
    }

    public static Controller create(final Stage stage, final DataSource dataSource, final SessionHolder holder,
                                    final SubscriptionProcess process) {
        return new ParametersSubentroController(stage, dataSource, holder, process);
    }

    @Override
    protected boolean areFieldsValid() {
        return isValidNumber(peopleNoField.getText()) && meterIdField.getText().length() > 0;
    }

    @Override
    protected void abstractNextScreen() {
        final SubscriptionProcess process = getProcess();

        try (final Connection conn = dataSource().getConnection()) {
            final Optional<Contatori> existingMeter = Queries.fetchMeterById(meterIdField.getText(), conn);
            existingMeter.ifPresentOrElse(m -> {
                final Immobili existingEstate = Queries.fetchPremiseFromMeterId(m.getMatricola(), dataSource());
                process.setPremise(existingEstate);
                process.setMeter(m);

                switchTo(SubentroConfirmationController.create(stage(), dataSource(), getSessionHolder(), process, true, true));
            }, () -> {
                /*
                 * Using a placeholder id for premise since its data is going to be
                 * added at the next screen.
                 */
                process.setMeter(new Contatori(
                        meterIdField.getText(),
                        process.plan().orElseThrow().getMateriaprima(),
                        0));
                switchTo(PremiseInsertionController.create(stage(), dataSource(), getSessionHolder(), process));
            });
        } catch (SQLException e) {
            e.printStackTrace();
            ViewUtils.showError(e.getMessage());
        }
    }
}
