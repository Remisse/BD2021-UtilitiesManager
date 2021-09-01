package bdproject.controller.gui;

import bdproject.model.Queries;
import bdproject.model.SubscriptionProcess;
import bdproject.tables.pojos.Contatori;
import bdproject.tables.pojos.Immobili;
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
import java.time.LocalDate;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.function.Consumer;

public class SubscriptionConfirmationController extends AbstractViewController implements Initializable {

    private static final String FXML = "subConfirmation.fxml";
    private static final String FLOW_CSS = "-fx-font: 16 arial";

    private final SubscriptionProcess process;
    private final Map<Integer, Runnable> typeBackAction;
    private final Map<Integer, Consumer<Connection>> typeConfirmationAction;

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

    private SubscriptionConfirmationController(final Stage stage, final DataSource dataSource,
            final SubscriptionProcess process) {
        super(stage, dataSource, FXML);
        this.process = process;
        this.typeBackAction = Map.of(
                3, () -> ParametersSelectionController.create(getStage(), getDataSource(), process),
                2, () -> ParametersSelectionController.create(getStage(), getDataSource(), process),
                1, () -> PremisesInsertionController.create(getStage(), getDataSource(), process)
        );
        this.typeConfirmationAction = Map.of(
                3, conn -> Queries.insertMeasurement(process.measurement().orElseThrow(), conn),

                2, conn -> {
                    int premisesId = 0;
                    if (process.premises().isPresent()) {
                        final Immobili p = process.premises().orElseThrow();
                        premisesId = Queries.insertPremisesReturningId(
                                p.getTipo(),
                                p.getVia(),
                                p.getNumcivico(),
                                p.getComune(),
                                p.getCap(),
                                p.getProvincia(),
                                p.getInterno(),
                                conn);
                        if (premisesId == 0) {
                            FXUtils.showBlockingWarning("Immobile non inserito!");
                        }
                    }
                    if (process.meter().isPresent()) {
                        final Contatori m = process.meter().get();
                        int result;

                        if (premisesId == 0) {
                            result = Queries.insertMeter(m.getMatricola(), m.getMateriaprima(), m.getIdimmobile(), conn);
                        } else {
                            result = Queries.insertMeter(m.getMatricola(), m.getMateriaprima(), premisesId, conn);
                        }
                        if (result == 0) {
                            FXUtils.showBlockingWarning("Contatore non inserito!");
                        }
                    }
                },

                1, conn -> {
                    final Immobili p = process.premises().orElseThrow();
                    final int premisesId = Queries.insertPremisesReturningId(
                            p.getTipo(),
                            p.getVia(),
                            p.getNumcivico(),
                            p.getComune(),
                            p.getCap(),
                            p.getProvincia(),
                            p.getInterno(),
                            conn);
                    if (premisesId == 0) {
                        FXUtils.showBlockingWarning("Immobile non inserito!");
                    }
                    process.setMeter(null);
                }
        );
    }

    public static ViewController create(final Stage stage, final DataSource dataSource,
            final SubscriptionProcess process) {
        return new SubscriptionConfirmationController(stage, dataSource, process);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        planText.setText(process.plan().orElseThrow().getNome());
        utilityText.setText(process.plan().orElseThrow().getMateriaprima());
        useText.setText(process.usage().orElseThrow().getNome());
        activationText.setText(process.activation().orElseThrow().getNome());

        final Text premisesText = new Text(StringUtils.premisesToString(process.premises().orElseThrow()));
        premisesText.setStyle(FLOW_CSS);
        premisesFlow.getChildren().add(premisesText);

        try (Connection conn = getDataSource().getConnection()) {
            process.otherClient().ifPresentOrElse(
                    c -> {
                        final Text clientText = new Text(StringUtils.clientToString(c.getIdentificativo(), conn));
                        clientText.setStyle(FLOW_CSS);
                        currentClientFlow.getChildren().add(clientText);
                        currentClientLabel.setVisible(true);
                    },
                    () -> {
                        currentClientFlow.getChildren().add(new Text(""));
                        currentClientLabel.setVisible(false);
                    });
        } catch (SQLException e) {
            FXUtils.showError(e.getMessage());
        }
    }

    @FXML
    private void goBack() {
        typeBackAction.get(process.activation().orElseThrow().getCodice()).run();
    }

    @FXML
    private void insertSubscription() {
        FXUtils.showConfirmationDialog(
                "Stai per richiedere l'attivazione di un contratto di fornitura. Vuoi continuare?",
                () -> {
                    try (Connection conn = getDataSource().getConnection()) {
                        typeConfirmationAction.get(process.activation().orElseThrow().getCodice()).accept(conn);
                        final int result = Queries.insertActivationRequest(
                                process.getClientId(),
                                LocalDate.now(),
                                process.plan().orElseThrow().getCodice(),
                                process.meter().isPresent() ? process.meter().orElseThrow().getMatricola() : null,
                                process.usage().orElseThrow().getCodice(),
                                process.activation().orElseThrow().getCodice(),
                                process.peopleNo(),
                                conn);
                        if (result != 0) {
                            FXUtils.showBlockingWarning("Richiesta inserita con successo.");
                            switchTo(HomeController.create(getStage(), getDataSource()));
                        } else {
                            FXUtils.showBlockingWarning("Richiesta non inserita!");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
        });
    }
}
