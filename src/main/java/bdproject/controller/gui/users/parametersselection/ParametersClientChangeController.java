package bdproject.controller.gui.users.parametersselection;

import bdproject.controller.Checks;
import bdproject.controller.gui.Controller;
import bdproject.controller.gui.users.subscriptionconfirmation.ChangeClientConfirmationController;
import bdproject.model.types.StatusType;
import bdproject.model.Queries;
import bdproject.model.SessionHolder;
import bdproject.model.SubscriptionProcess;
import bdproject.tables.pojos.*;
import bdproject.utils.ViewUtils;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Optional;

public class ParametersClientChangeController extends AbstractParametersSelectionController {

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

    private ParametersClientChangeController(final Stage stage, final DataSource dataSource, final SessionHolder holder,
                                             final SubscriptionProcess process) {
        super(stage, dataSource, holder, process);
    }

    public static Controller create(final Stage stage, final DataSource dataSource, final SessionHolder holder,
                                    final SubscriptionProcess process) {
        return new ParametersClientChangeController(stage, dataSource, holder, process);
    }

    @Override
    protected boolean areFieldsValid() {
        return isValidNumber(peopleNoField.getText()) &&
                meterIdField.getText().length() > 0 &&
                Checks.isIntegerNumber(otherClientIdField.getText()) &&
                Checks.isValidConsumption(measurementField.getText());
    }

    @Override
    protected void abstractNextScreen() {
        final SubscriptionProcess process = getProcess();

        try (final Connection conn = dataSource().getConnection()) {
            final Optional<Contatori> meter = Queries.fetchMeterById(meterIdField.getText(), conn);

            meter.ifPresentOrElse(m -> {
                final Optional<ClientiDettagliati> client = Queries.fetchClientById(
                        Integer.parseInt(otherClientIdField.getText()), conn);

                client.ifPresentOrElse(c -> {
                    /*
                     * Find the subscription on which the new one will be based
                     */
                    final Optional<Contratti> subscription = Queries.fetchSubscriptionForChange(
                            m.getMatricola(), c.getIdpersona(), conn);

                    subscription.ifPresentOrElse(s -> {
                        final Optional<Offerte> plan = Queries.fetchPlanById(s.getOfferta(), dataSource());

                        plan.ifPresentOrElse(p -> {
                            if (Checks.isValidConsumption(measurementField.getText())) {
                                Letture measurement = new Letture(
                                        0,
                                        m.getMatricola(),
                                        LocalDate.now(),
                                        BigDecimal.valueOf(Long.parseLong(measurementField.getText())),
                                        StatusType.REVIEWING.toString(),
                                        c.getIdpersona()
                                );
                                process.setMeasurement(measurement);

                                Immobili premise = Queries.fetchPremiseById(m.getIdimmobile(), conn).orElseThrow();
                                process.setPremise(premise);
                                process.setMeter(m);
                                process.setOtherClient(c);
                                process.setPlan(p);

                                switchTo(ChangeClientConfirmationController.create(stage(), dataSource(), getSessionHolder(), process));
                            } else {
                                ViewUtils.showError("Verifica di aver inserito correttamente la lettura.");
                            }
                        }, () -> ViewUtils.showError("Offerta non trovata. Ma è davvero possibile?"));
                    }, () -> ViewUtils.showError("Nessun contratto trovato. Verifica che i dati inseriti siano corretti."));
                }, () -> ViewUtils.showError("Nessun cliente trovato. Verifica che il codice inserito sia corretto."));
            }, () -> ViewUtils.showError("Nessun contatore trovato. Verifica che la matricola inserita sia corretta."));
        } catch (SQLException e) {
            e.printStackTrace();
            ViewUtils.showError(e.getMessage());
        }
    }
}
