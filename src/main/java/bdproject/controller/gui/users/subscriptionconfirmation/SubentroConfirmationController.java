package bdproject.controller.gui.users.subscriptionconfirmation;

import bdproject.controller.gui.Controller;
import bdproject.controller.gui.users.parametersselection.ParametersSubentroController;
import bdproject.model.Queries;
import bdproject.model.SessionHolder;
import bdproject.model.SubscriptionProcess;
import bdproject.tables.pojos.Contatori;
import bdproject.tables.pojos.Immobili;
import bdproject.utils.ViewUtils;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;

public class SubentroConfirmationController extends AbstractSubscriptionConfirmationController {

    private final boolean isPremiseAlreadyPresent;
    private final boolean isMeterAlreadyPresent;

    @FXML private Label planText;
    @FXML private Label utilityText;
    @FXML private Label useText;
    @FXML private Label activationText;
    @FXML private Label currentClientLabel;
    @FXML private TextFlow premisesFlow;
    @FXML private TextFlow currentClientFlow;

    private SubentroConfirmationController(final Stage stage, final DataSource dataSource, final SessionHolder holder,
                                           final SubscriptionProcess process, final boolean isPremiseAlreadyPresent,
                                           final boolean isMeterAlreadyPresent) {
        super(stage, dataSource, holder, process);
        this.isMeterAlreadyPresent = isMeterAlreadyPresent;
        this.isPremiseAlreadyPresent = isPremiseAlreadyPresent;
    }

    public static Controller create(final Stage stage, final DataSource dataSource, final SessionHolder holder,
                                    final SubscriptionProcess process, final boolean isPremiseAlreadyPresent,
                                    final boolean isMeterAlreadyPresent) {
        return new SubentroConfirmationController(
                stage, dataSource, holder, process, isPremiseAlreadyPresent, isMeterAlreadyPresent);
    }

    @Override
    protected void setActivationSpecificData() {
        currentClientFlow.getChildren().add(new Text(""));
        currentClientLabel.setVisible(false);
    }

    @Override
    protected void abstractGoBack() {
        switchTo(ParametersSubentroController.create(stage(), dataSource(), getSessionHolder(), getProcess()));
    }

    @Override
    protected int abstractInsertSubscription() {
        int result = 0;

        try (Connection conn = dataSource().getConnection()) {
            final SubscriptionProcess process = getProcess();

            /*
             * Insert premise if not already present in DB
             */
            int premiseId;
            if (!isPremiseAlreadyPresent) {
                final Immobili pTemp = process.premise().orElseThrow();
                Queries.insertPremise(
                        pTemp.getTipo(),
                        pTemp.getVia(),
                        pTemp.getNumcivico(),
                        pTemp.getInterno(),
                        pTemp.getComune(),
                        pTemp.getCap(),
                        pTemp.getProvincia(),
                        conn);
                premiseId = Queries.fetchLastInsertId(conn);
            } else {
                premiseId = process.premise().orElseThrow().getIdimmobile();
            }

            /*
             * Insert meter if not already present in DB
             */
            if (!isMeterAlreadyPresent) {
                final Contatori m = process.meter().orElseThrow();
                Queries.insertMeter(m.getMatricola(), m.getMateriaprima(), premiseId, conn);
                process.setMeter(new Contatori(m.getMatricola(), m.getMateriaprima(), premiseId));
            }

            result = Queries.insertSubscriptionRequest(
                    LocalDate.now(),
                    process.peopleNo(),
                    process.plan().orElseThrow().getCodofferta(),
                    process.usage().orElseThrow().getCoduso(),
                    process.activation().orElseThrow().getCodattivazione(),
                    premiseId,
                    process.getClientId(),
                    conn);
        } catch (SQLException e) {
            ViewUtils.showError(e.getMessage());
        }

        return result;
    }
}
