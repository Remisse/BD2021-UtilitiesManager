package bdproject.controller.gui.users.subscriptionconfirmation;

import bdproject.controller.gui.Controller;
import bdproject.controller.gui.users.parametersselection.ParametersNewActivationController;
import bdproject.model.Queries;
import bdproject.model.SessionHolder;
import bdproject.model.SubscriptionProcess;
import bdproject.tables.pojos.Immobili;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

import javax.sql.DataSource;
import java.sql.Connection;
import java.time.LocalDate;

public class NewActivationConfirmationController extends AbstractSubscriptionConfirmationController {

    @FXML private Label planText;
    @FXML private Label utilityText;
    @FXML private Label useText;
    @FXML private Label activationText;
    @FXML private Label currentClientLabel;
    @FXML private TextFlow premisesFlow;
    @FXML private TextFlow currentClientFlow;

    private NewActivationConfirmationController(final Stage stage, final DataSource dataSource, final SessionHolder holder,
                                                final SubscriptionProcess process) {
        super(stage, dataSource, holder, process);
    }

    public static Controller create(final Stage stage, final DataSource dataSource, final SessionHolder holder,
                                    final SubscriptionProcess process) {
        return new NewActivationConfirmationController(stage, dataSource, holder, process);
    }

    @Override
    protected void setActivationSpecificData() {
        currentClientFlow.getChildren().add(new Text(""));
        currentClientLabel.setVisible(false);
    }

    @Override
    protected void abstractGoBack() {
        switchTo(ParametersNewActivationController.create(stage(), dataSource(), getSessionHolder(), getProcess()));
    }

    @Override
    protected int abstractInsertSubscription() {
        int result = 0;

        try (Connection conn = dataSource().getConnection()) {
            final SubscriptionProcess process = getProcess();
            final Immobili pTemp = process.premise().orElseThrow();
            Queries.insertPremise(
                    pTemp.getTipo(),
                    pTemp.getVia(),
                    pTemp.getNumcivico(),
                    pTemp.getComune(),
                    pTemp.getCap(),
                    pTemp.getProvincia(),
                    pTemp.getInterno(),
                    conn);
            final int premiseId = Queries.fetchLastInsertId(conn);

            result = Queries.insertSubscriptionRequest(
                    LocalDate.now(),
                    process.peopleNo(),
                    process.plan().orElseThrow().getCodofferta(),
                    process.usage().orElseThrow().getCoduso(),
                    process.activation().orElseThrow().getCodattivazione(),
                    premiseId,
                    process.getClientId(),
                    conn);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }
}
