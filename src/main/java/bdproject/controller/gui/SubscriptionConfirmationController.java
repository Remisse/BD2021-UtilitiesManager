package bdproject.controller.gui;

import bdproject.model.Queries;
import bdproject.model.SubscriptionProcess;
import bdproject.tables.pojos.Immobili;
import bdproject.tables.pojos.Zone;
import bdproject.utils.FXUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import javax.sql.DataSource;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ResourceBundle;

import static bdproject.tables.Immobili.IMMOBILI;
import static bdproject.tables.Zone.ZONE;
import static org.jooq.impl.DSL.defaultValue;

public class SubscriptionConfirmationController extends AbstractViewController implements Initializable {

    private static final String FXML = "subConfirmation.fxml";
    private final SubscriptionProcess process;

    @FXML
    private Label planText;
    @FXML
    private Label utilityText;
    @FXML
    private Label useText;
    @FXML
    private Label activationText;
    @FXML
    private Label currentClientLabel;
    @FXML
    private TextFlow premisesFlow;
    @FXML
    private TextFlow currentClientFlow;
    @FXML
    private Button back;
    @FXML
    private Button confirm;

    private SubscriptionConfirmationController(final Stage stage, final DataSource dataSource,
            final SubscriptionProcess process) {
        super(stage, dataSource, FXML);
        this.process = process;
    }

    public static ViewController create(final Stage stage, final DataSource dataSource,
            final SubscriptionProcess process) {
        return new SubscriptionConfirmationController(stage, dataSource, process);
    }

    private void initDomestic() {

    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        planText.setText(process.plan().orElseThrow().getNome());
        utilityText.setText(process.plan().orElseThrow().getMateriaprima());
        useText.setText(process.usage().orElseThrow());
        activationText.setText(process.activation().orElseThrow().getNome());

        final Immobili premises = process.premises().orElseThrow();
        Zone zone = null;
        try (Connection conn = getDataSource().getConnection()) {
            DSLContext query = DSL.using(conn, SQLDialect.MYSQL);
            zone = query.select(ZONE.asterisk())
                    .from(ZONE, IMMOBILI)
                    .where(ZONE.IDZONA.eq(IMMOBILI.IDZONA))
                    .fetchOneInto(Zone.class);
        } catch (SQLException e) {
            FXUtils.showError(e.getMessage());
        }

        assert zone != null;
        premisesFlow.getChildren().add(new Text("Tipo: " + premises.getTipo()
                + "\nVia: " + premises.getVia()
                + "\nNumero civico: " + premises.getNumcivico()
                + (premises.getInterno() == null ? "" : "\nInterno: " + premises.getInterno())
                + "\nComune: " + zone.getComune()));

        process.otherClient().ifPresentOrElse(c ->  {
            currentClientFlow.getChildren().add(new Text("Codice cliente: " + c.getCodicecliente()
                    + "\nNome: " + c.getNome()
                    + "\nCognome: " + c.getCognome()));
        }, () -> currentClientFlow.getChildren().add(new Text("")));

        toggleElements();
    }

    private void toggleElements() {
        process.otherClient().ifPresentOrElse(c ->  {
            currentClientLabel.setVisible(true);
            currentClientFlow.setVisible(true);
        }, () -> {
            currentClientLabel.setVisible(false);
            currentClientFlow.setVisible(false);
        });
    }

    @FXML
    private void goBack() {
        final String activ = process.activation().orElseThrow().getNome();
        if (activ.equals("Voltura")) {
            switchTo(ActivationByChangeController.create(getStage(), getDataSource(), process));
        } else if (activ.equals("Subentro")) {

        } else {

        }
    }

    @FXML
    private void insertSubscription() {
        Alert alert = new Alert(
                Alert.AlertType.CONFIRMATION,
                "Stai per stipulare un contratto di fornitura. Vuoi continuare?",
                ButtonType.YES, ButtonType.NO);
        alert.showAndWait().ifPresent(b -> {
            if (b == ButtonType.YES) {
                final String activ = process.activation().orElseThrow().getNome();
                try (Connection conn = getDataSource().getConnection()) {
                    switch (activ) {
                        case "Voltura":
                            insertIfChange(conn);
                            break;
                        case "Subentro":
                            insertIfSubentro(conn);
                            break;
                        case "Nuova attivazione":
                            insertIfNewActivation(conn);
                            break;
                        default:
                            FXUtils.showError("Nessun metodo di attivazione trovato. Qualcosa non ha funzionato.");
                            break;
                    }
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        });
    }

    /**
     * Proper order is fundamental:
     * 1) insert measurement
     * 2) cease existing subscription
     * 3) create new subscription
     */
    private void insertIfChange(Connection conn) {
        Queries.insertMeasurement(process.measurement().orElseThrow(), conn);
        Queries.ceaseSubscription(process.otherSubscription().orElseThrow(), conn);
        Queries.insertSubscription(process, conn);
    }

    private void insertIfSubentro(Connection conn) {

    }

    private void insertIfNewActivation(Connection conn) {

    }
}
