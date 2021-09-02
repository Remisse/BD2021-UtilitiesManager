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
import java.time.LocalDate;
import java.util.Optional;
import java.util.ResourceBundle;

public class ParametersSelectionController extends AbstractViewController implements Initializable {

    private static final String FXML_FILE = "meterAndActivation.fxml";
    private final SubscriptionProcess process;

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

    private ParametersSelectionController(final Stage stage, final DataSource dataSource,
            final SubscriptionProcess process) {
        super(stage, dataSource, FXML_FILE);
        this.process = process;
    }

    public static ViewController create(final Stage stage, final DataSource dataSource,
            final SubscriptionProcess process) {
        return new ParametersSelectionController(stage, dataSource, process);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        planLabel.setText(process.plan().orElseThrow().getNome());
        utilityLabel.setText(process.plan().orElseThrow().getMateriaprima());
        useLabel.setText(process.usage().orElseThrow().getNome());
        methodLabel.setText(process.usage().orElseThrow().getNome());

        if (requiresPeopleNo()) {
            peopleNoLabel.setText("N. componenti del nucleo familiare");
            peopleNoLabel.setVisible(true);
            peopleNoField.setVisible(true);
        } else {
            peopleNoLabel.setVisible(false);
            peopleNoField.setVisible(false);
        }
        peopleNoField.setText(String.valueOf(process.peopleNo()));

        switch (process.activation().orElseThrow().getCodice()) {
            case 1:
                otherClientIdLabel.setVisible(false);
                otherClientIdField.setVisible(false);
                meterIdField.setVisible(false);
                meterIdLabel.setVisible(false);
                measurementField.setVisible(false);
                measurementLabel.setVisible(false);
                break;
            case 2:
                otherClientIdField.setVisible(false);
                otherClientIdLabel.setVisible(false);
                meterIdField.setVisible(true);
                meterIdLabel.setVisible(true);
                measurementField.setVisible(false);
                measurementLabel.setVisible(false);
                break;
            case 3:
                otherClientIdField.setVisible(true);
                otherClientIdLabel.setVisible(true);
                meterIdField.setVisible(true);
                meterIdLabel.setVisible(true);
                measurementField.setVisible(true);
                measurementLabel.setVisible(true);
                break;
        }
    }

    private boolean requiresPeopleNo() {
        final String use = process.usage().orElseThrow().getNome();
        return use.equals("Commerciale") || use.equals("Abitativo residenziale");
    }

    private boolean areFieldsValid() {
        boolean outcome = false;
        switch (process.activation().orElseThrow().getCodice()) {
            case 1:
                outcome = isValidNumber(peopleNoField.getText());
                break;
            case 2:
                outcome = isValidNumber(peopleNoField.getText()) && meterIdField.getText().length() > 0;
                break;
            case 3:
                outcome = isValidNumber(peopleNoField.getText()) &&
                        meterIdField.getText().length() > 0 &&
                        Checks.isNumber(otherClientIdField.getText()) &&
                        Checks.isValidConsumption(measurementField.getText());
                break;
        }
        return outcome;
    }

    private boolean isValidNumber(String s) {
        return s.codePoints().allMatch(Character::isDigit) && Integer.parseInt(s) > 0 && Integer.parseInt(s) <= 20;
    }

    @FXML
    private void goBack() {
        FXUtils.showConfirmationDialog(
                "Tornando al catalogo, tutti i dati inseriti verranno persi. Vuoi tornare al catalogo?",
                () -> switchTo(CatalogueController.create(getStage(), getDataSource())));
    }

    @FXML
    private void nextScreen() {
        if (!areFieldsValid()) {
            FXUtils.showError("Verifica di aver inserito correttamente i dati.");
        } else {
            try (Connection conn = getDataSource().getConnection()) {
                process.setPeopleNo(requiresPeopleNo() ? Integer.parseInt(peopleNoField.getText()) : 1);
                switch (process.activation().orElseThrow().getCodice()) {
                    case 1:
                        switchTo(PremisesInsertionController.create(getStage(), getDataSource(), process));
                        break;
                    case 2:
                        bySubentro(conn);
                        break;
                    case 3:
                        byChange(conn);
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void bySubentro(final Connection conn) {
        final Optional<Contatori> existingMeter = Queries.fetchMeterByIdAndUtility(
                meterIdField.getText(),
                process.plan().orElseThrow().getMateriaprima(),
                conn);
        existingMeter.ifPresentOrElse(m -> {
            process.setMeter(m);

            final Immobili existingPremises = Queries.fetchPremisesFromMeterId(m.getMatricola(), getDataSource());
            process.setPremises(existingPremises);

            switchTo(SubscriptionConfirmationController.create(getStage(), getDataSource(), process));
        }, () -> {
            /*
             * Using a placeholder id for meter and premises, since it's going to be
             * added at the next screen.
             */
            process.setMeter(new Contatori(
                    0,
                    meterIdField.getText(),
                    process.plan().orElseThrow().getMateriaprima(),
                    0));
            switchTo(PremisesInsertionController.create(getStage(), getDataSource(), process));
        });
    }

    private void byChange(final Connection conn) {
        Optional<Contatori> meter = Queries.fetchMeterByIdAndUtility(
                meterIdField.getText(), process.plan().orElseThrow().getMateriaprima(), conn);

        meter.ifPresentOrElse(m -> {
            Optional<ClientiDettagliati> client = Queries.fetchClientById(
                    Integer.parseInt(otherClientIdField.getText()), conn);

            client.ifPresentOrElse(c -> {
                /* Find the subscription on which the new one will be based */
                Optional<ContrattiDettagliati> subscription = Queries.fetchSubscriptionForChange(
                        m.getMatricola(), c.getIdentificativo(), conn);

                subscription.ifPresentOrElse(s -> {
                    if (Checks.isValidConsumption(measurementField.getText())) {
                        Letture measurement = new Letture(
                                BigDecimal.valueOf(Long.parseLong(measurementField.getText())),
                                m.getProgressivo(),
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
    }
}
