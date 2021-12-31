package bdproject.controller.gui.users.subscriptionconfirmation;

import bdproject.controller.gui.Controller;
import bdproject.controller.gui.users.parametersselection.ParametersClientChangeController;
import bdproject.model.Queries;
import bdproject.model.SessionHolder;
import bdproject.model.SubscriptionProcess;
import bdproject.view.StringUtils;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

import javax.sql.DataSource;
import java.sql.Connection;
import java.time.LocalDate;

public class ChangeClientConfirmationController extends AbstractSubscriptionConfirmationController {

    private static final String FLOW_CSS = "-fx-font: 16 arial";

    @FXML private Label planText;
    @FXML private Label utilityText;
    @FXML private Label useText;
    @FXML private Label activationText;
    @FXML private Label currentClientLabel;
    @FXML private TextFlow premisesFlow;
    @FXML private TextFlow currentClientFlow;

    private ChangeClientConfirmationController(final Stage stage, final DataSource dataSource, final SessionHolder holder,
                                               final SubscriptionProcess process) {
        super(stage, dataSource, holder, process);
    }

    public static Controller create(final Stage stage, final DataSource dataSource, final SessionHolder holder,
                                    final SubscriptionProcess process) {
        return new ChangeClientConfirmationController(stage, dataSource, holder, process);
    }

    @Override
    protected void setActivationSpecificData() {
        final Text clientText = new Text(StringUtils.clientToString(getProcess().otherClient().orElseThrow()));

        clientText.setStyle(FLOW_CSS);
        currentClientFlow.getChildren().add(clientText);
        currentClientLabel.setVisible(true);
}

    @Override
    protected void abstractGoBack() {
        switchTo(ParametersClientChangeController.create(stage(), dataSource(), getSessionHolder(), getProcess()));
    }

    @Override
    protected int abstractInsertSubscription() {
        int result = 0;

        try (Connection conn = dataSource().getConnection()) {
            final SubscriptionProcess process = getProcess();

            Queries.insertMeasurement(process.measurement().orElseThrow(), conn);
            result = Queries.insertSubscriptionRequest(
                    LocalDate.now(),
                    process.peopleNo(),
                    process.plan().orElseThrow().getCodofferta(),
                    process.usage().orElseThrow().getCoduso(),
                    process.activation().orElseThrow().getCodattivazione(),
                    process.premise().orElseThrow().getIdimmobile(),
                    process.getClientId(),
                    conn);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }
}
