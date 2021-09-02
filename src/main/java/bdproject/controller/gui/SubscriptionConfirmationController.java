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

public class SubscriptionConfirmationController extends AbstractViewController implements Initializable {

    private static final String FXML = "subConfirmation.fxml";
    private static final String FLOW_CSS = "-fx-font: 16 arial";

    private final SubscriptionProcess process;
    private final Map<Integer, Runnable> typeBackAction;

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
                    final Immobili pTemp = process.premises().orElseThrow();
                    final int premisesId = Queries.insertPremisesReturningId(
                            pTemp.getTipo(),
                            pTemp.getVia(),
                            pTemp.getNumcivico(),
                            pTemp.getComune(),
                            pTemp.getCap(),
                            pTemp.getProvincia(),
                            pTemp.getInterno(),
                            conn);
                    if (premisesId == 0) {
                        FXUtils.showBlockingWarning("Immobile non inserito!");
                    }

                    process.meter().ifPresent(m -> {
                        final int finalPremisesId = premisesId != 0 ? premisesId : m.getIdimmobile();
                        final int lastInsertMeter = Queries.insertMeterReturningId(
                                    m.getMatricola(),
                                    m.getMateriaprima(),
                                    finalPremisesId,
                                    conn);
                        process.setMeter(new Contatori(
                                    lastInsertMeter != 0 ? lastInsertMeter : m.getProgressivo(),
                                    m.getMatricola(),
                                    m.getMateriaprima(),
                                    finalPremisesId));
                    });

                    process.measurement().ifPresent(m -> Queries.insertMeasurement(process.measurement().orElseThrow(), conn));

                    final int result = Queries.insertActivationRequest(
                            process.getClientId(),
                            LocalDate.now(),
                            process.plan().orElseThrow().getCodice(),
                            process.meter().orElseThrow().getProgressivo(),
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
