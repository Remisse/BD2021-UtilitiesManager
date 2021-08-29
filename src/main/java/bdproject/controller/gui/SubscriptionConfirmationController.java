package bdproject.controller.gui;

import bdproject.model.Queries;
import bdproject.model.SubscriptionProcess;
import bdproject.utils.FXUtils;
import bdproject.view.StringUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

import javax.sql.DataSource;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.ResourceBundle;

public class SubscriptionConfirmationController extends AbstractViewController implements Initializable {

    private static final String FXML = "subConfirmation.fxml";
    private final SubscriptionProcess process;
    private final Map<String, Runnable> typeAction;

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
        this.typeAction = Map.of(
                "Voltura", () -> ActivationByChangeController.create(getStage(), getDataSource(), process),
                "Subentro", () -> {}
        );
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
        useText.setText(process.usage().orElseThrow().getNome());
        activationText.setText(process.activation().orElseThrow().getNome());
        premisesFlow.getChildren().add(new Text(StringUtils.premisesToString(process.premises().orElseThrow())));

        try (Connection conn = getDataSource().getConnection()) {
            process.otherClient().ifPresentOrElse(
                    c -> currentClientFlow.getChildren().add(new Text(StringUtils.clientToString(
                            c.getIdentificativo(), conn))),
                    () -> currentClientFlow.getChildren().add(new Text("")));
        } catch (SQLException e) {
            FXUtils.showError(e.getMessage());
        }

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
        typeAction.get(process.activation().orElseThrow().getNome()).run();
    }

    @FXML
    private void insertSubscription() {
        FXUtils.showConfirmationDialog("Stai per stipulare un contratto di fornitura. Vuoi continuare?", () -> {
            final String type = process.activation().orElseThrow().getNome();
            try (Connection conn = getDataSource().getConnection()) {
                switch (type) {
                    case "Voltura":
                        insertIfChange(conn);
                        break;
                    case "Subentro":
                        insertIfSubentro(conn);
                        break;
                    default:
                        FXUtils.showError("Nessun metodo di attivazione trovato. Qualcosa non ha funzionato.");
                        break;
                }
            } catch (SQLException e) {
                e.printStackTrace();
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
        Queries.ceaseSubscription(process.otherSubscription().orElseThrow().getIdcontratto(), conn);
        Queries.insertActivationRequest(process, conn);
    }

    private void insertIfSubentro(Connection conn) {

    }
}
