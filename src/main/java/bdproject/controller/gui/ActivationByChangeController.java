package bdproject.controller.gui;

import bdproject.controller.Checks;
import bdproject.model.Queries;
import bdproject.model.SubscriptionProcess;
import bdproject.tables.pojos.*;
import bdproject.utils.FXUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javax.sql.DataSource;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Optional;
import java.util.ResourceBundle;

public class ActivationByChangeController extends AbstractViewController implements Initializable {

    private static final String FXML_FILE = "byChange.fxml";
    private final SubscriptionProcess process;

    @FXML private Label planLabel;
    @FXML private Label utilityLabel;
    @FXML private Label useLabel;
    @FXML private Label methodChosen;
    @FXML private Label meterIdLabel;
    @FXML private TextField meterId;
    @FXML private Label clientIdLabel;
    @FXML private TextField clientIdField;
    @FXML private Button next;
    @FXML private Label measurementLabel;
    @FXML private TextField consumption;

    private ActivationByChangeController(final Stage stage, final DataSource dataSource,
            final SubscriptionProcess process) {
        super(stage, dataSource, FXML_FILE);
        this.process = process;
    }

    public static ViewController create(final Stage stage, final DataSource dataSource,
            final SubscriptionProcess process) {
        return new ActivationByChangeController(stage, dataSource, process);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        planLabel.setText(process.plan().orElseThrow().getNome());
        utilityLabel.setText(process.plan().orElseThrow().getMateriaprima());
        useLabel.setText(process.usage().orElseThrow().getNome());
        methodChosen.setText(process.activation().orElseThrow().getNome());
    }

    @FXML
    private void updateElements() {
        next.setDisable(meterId.getText().length() == 0 || clientIdField.getText().length() == 0);
    }

    @FXML
    private void nextScreen() {
        try (Connection conn = getDataSource().getConnection()) {
            Optional<Contatori> meter = Queries.fetchMeterByIdAndUtility(
                    meterId.getText(), process.plan().orElseThrow().getMateriaprima(), conn);
            Optional<ClientiDettagliati> client = Queries.fetchClientById(Integer.parseInt(clientIdField.getText()), conn);

            meter.ifPresentOrElse(m -> {
                client.ifPresentOrElse(c -> {
                    /* Find the subscription on which the new one will be based */
                    Optional<ContrattiDettagliati> subscription = Queries.fetchSubscriptionForChange(
                            m.getNumeroprogressivo(), c.getIdentificativo(), conn);

                    subscription.ifPresentOrElse(s -> {
                        if (Checks.isValidConsumption(consumption.getText())) {
                            Letture measurement = new Letture(
                                    BigDecimal.valueOf(Long.parseLong(consumption.getText())),
                                    m.getNumeroprogressivo(),
                                    LocalDate.now(),
                                    (byte) 0,
                                    c.getIdentificativo()
                            );
                            process.setMeasurement(measurement);

                            Immobili premises = Queries.fetchPremisesById(m.getIdimmobile(), conn).orElseThrow();
                            process.setPremises(premises);
                            process.setMeter(m);
                            process.setOtherClient(c);
                            process.setOtherSubscription(s);

                            switchTo(SubscriptionConfirmationController.create(getStage(), getDataSource(), process));
                        } else {
                            FXUtils.showError("Verifica di aver inserito correttamente la lettura.");
                        }
                    }, () -> FXUtils.showError("Nessun contratto trovato. Verifica che i dati inseriti siano corretti."));
                }, () -> FXUtils.showError("Nessun cliente trovato. Verifica che il codice inserito sia corretto."));
            }, () -> FXUtils.showError("Nessun contatore trovato. Verifica che la matricola inserita sia corretta."));
        } catch (SQLException e) {
            FXUtils.showError(e.getMessage());
        }
    }

    @FXML
    private void goBack() {
        switchTo(ParametersSelectionController.create(getStage(), getDataSource(), process));
    }
}
