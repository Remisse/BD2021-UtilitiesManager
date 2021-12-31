package bdproject.controller.gui.users.parametersselection;

import bdproject.controller.gui.Controller;
import bdproject.controller.gui.users.PremiseInsertionController;
import bdproject.model.SessionHolder;
import bdproject.model.SubscriptionProcess;
import bdproject.tables.pojos.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import javax.sql.DataSource;

public class ParametersNewActivationController extends AbstractParametersSelectionController {

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

    private ParametersNewActivationController(final Stage stage, final DataSource dataSource, final SessionHolder holder,
                                              final SubscriptionProcess process) {
        super(stage, dataSource, holder, process);
    }

    public static Controller create(final Stage stage, final DataSource dataSource, final SessionHolder holder,
                                    final SubscriptionProcess process) {
        return new ParametersNewActivationController(stage, dataSource, holder, process);
    }

    @Override
    protected boolean areFieldsValid() {
        return isValidNumber(peopleNoField.getText());
    }

    @Override
    protected void abstractNextScreen() {
        final SubscriptionProcess process = getProcess();

        /*
         * Placeholder
         */
        process.setMeter(new Contatori(
                "",
                process.plan().orElseThrow().getMateriaprima(),
                0));
        switchTo(PremiseInsertionController.create(stage(), dataSource(), getSessionHolder(), process));
    }
}
