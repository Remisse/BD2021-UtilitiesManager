package bdproject.controller.gui;

import bdproject.controller.Checks;
import bdproject.model.SubscriptionProcess;
import bdproject.tables.pojos.*;
import bdproject.utils.FXUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;

import static bdproject.Tables.PERSONE;
import static bdproject.tables.Contatori.CONTATORI;
import static bdproject.tables.Contratti.CONTRATTI;
import static bdproject.tables.Immobili.IMMOBILI;

public class ActivationByChangeController extends AbstractViewController implements Initializable {

    private static final String fxml = "byChange.fxml";
    private final SubscriptionProcess process;

    @FXML
    private Label planLabel;
    @FXML
    private Label utilityLabel;
    @FXML
    private Label useLabel;
    @FXML
    private Label methodChosen;
    @FXML
    private Label meterIdLabel;
    @FXML
    private TextField meterId;
    @FXML
    private Label clientIdLabel;
    @FXML
    private TextField clientIdField;
    @FXML
    private Button next;
    @FXML
    private Label measurementLabel;
    @FXML
    private TextField consumption;

    private ActivationByChangeController(final Stage stage, final DataSource dataSource,
            final SubscriptionProcess process) {
        super(stage, dataSource, fxml);
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
        useLabel.setText(process.usage().orElseThrow());
        methodChosen.setText(process.activation().orElseThrow().getNome());
    }

    @FXML
    private void updateElements() {
        next.setDisable(meterId.getText().length() == 0 || clientIdField.getText().length() == 0);
    }

    @FXML
    private void nextScreen() {
        try (Connection conn = getDataSource().getConnection()) {
            DSLContext query = DSL.using(conn, SQLDialect.MYSQL);
            var meter = query.select()
                    .from(CONTATORI)
                    .where(CONTATORI.MATRICOLA.eq(meterId.getText()))
                    .and(CONTATORI.MATERIAPRIMA.eq(process.plan().orElseThrow().getMateriaprima()))
                    .fetchOptionalInto(Contatori.class);
            var client = query.select()
                    .from(PERSONE)
                    .where(PERSONE.CODICECLIENTE.eq(Integer.parseInt(clientIdField.getText())))
                    .fetchOptionalInto(Persone.class);

            meter.ifPresentOrElse(m -> {
                client.ifPresentOrElse(c -> {
                    /* Check if there's an active subscription */
                    var subscription = query.select()
                            .from(CONTRATTI)
                            .where(CONTRATTI.CONTATORE.eq(m.getNumeroprogressivo()))
                            .and(CONTRATTI.CODICECLIENTE.eq(c.getCodicecliente()))
                            .and(CONTRATTI.DATAINIZIO.isNotNull())
                            .and(CONTRATTI.DATACESSAZIONE.isNull())
                            .fetchOptionalInto(Contratti.class);

                    subscription.ifPresentOrElse(s -> {
                        if (Checks.isValidMeasurement(consumption.getText())) {
                            var measurement = new Letture(
                                    BigDecimal.valueOf(Long.parseLong(consumption.getText())),
                                    m.getNumeroprogressivo(),
                                    LocalDate.now(),
                                    (byte) 0
                            );
                            process.setMeasurement(measurement);
                            Immobili premises = query.select()
                                    .from(IMMOBILI)
                                    .where(IMMOBILI.IDIMMOBILE.eq(m.getIdimmobile()))
                                    .fetchOneInto(Immobili.class);
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
