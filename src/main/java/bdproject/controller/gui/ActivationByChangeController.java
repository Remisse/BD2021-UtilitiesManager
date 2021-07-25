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
import java.util.Optional;
import java.util.ResourceBundle;

import static bdproject.tables.Contatori.CONTATORI;
import static bdproject.tables.Contratti.CONTRATTI;
import static bdproject.tables.Immobili.IMMOBILI;
import static bdproject.tables.PersoneFisiche.PERSONE_FISICHE;
import static bdproject.tables.PersoneGiuridiche.PERSONE_GIURIDICHE;

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
    private TextField f1;
    @FXML
    private TextField f2;
    @FXML
    private TextField f3;

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
        planLabel.setText(process.getPlan().orElseThrow().getNome());
        utilityLabel.setText(process.getPlan().orElseThrow().getMateriaprima());
        useLabel.setText(process.getUse().orElseThrow());
        methodChosen.setText(process.getActivationMethod().orElseThrow().getNome());

        if (!process.getPlan().orElseThrow().getMateriaprima().equals("Luce")) {
            f2.setVisible(false);
            f3.setVisible(false);
        }
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
                    .and(CONTATORI.MATERIAPRIMA.eq(process.getPlan().orElseThrow().getMateriaprima()))
                    .fetchOptionalInto(Contatori.class);
            var client = query.select()
                    .from(PERSONE_FISICHE)
                    .where(PERSONE_FISICHE.CODICECLIENTE.eq(Integer.parseInt(clientIdField.getText())))
                    .fetchOptionalInto(PersoneFisiche.class);

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
                        if (Checks.isValidMeasurement(f1, f2, f3)) {
                            var measurement = new Letture(
                                    BigDecimal.valueOf(Long.parseLong(f1.getText())),
                                    f2.isVisible() ? BigDecimal.valueOf(Long.parseLong(f2.getText())) : BigDecimal.ZERO,
                                    f3.isVisible() ? BigDecimal.valueOf(Long.parseLong(f3.getText())) : BigDecimal.ZERO,
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
                            Optional<PersoneGiuridiche> firm = query.select()
                                    .from(PERSONE_GIURIDICHE)
                                    .where(PERSONE_GIURIDICHE.CODICEAZIENDA.eq(s.getCodiceazienda()))
                                    .fetchOptionalInto(PersoneGiuridiche.class);
                            firm.ifPresent(process::setFirm);

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
