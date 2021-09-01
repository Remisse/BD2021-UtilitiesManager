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
    @FXML
    private Button back;
    @FXML
    private Button confirm;

    private SubscriptionConfirmationController(final Stage stage, final DataSource dataSource,
            final SubscriptionProcess process) {
        super(stage, dataSource, FXML);
        this.process = process;
        this.typeBackAction = Map.of(
                3, () -> ParametersSelectionController.create(getStage(), getDataSource(), process),
                2, () -> PremisesInsertionController.create(getStage(), getDataSource(), process),
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
        typeBackAction.get(process.activation().orElseThrow().getCodice()).run();
    }

    @FXML
    private void insertSubscription() {
        FXUtils.showConfirmationDialog(
                "Stai per richiedere l'attivazione di un contratto di fornitura. Vuoi continuare?",
                () -> {
                    final int type = process.activation().orElseThrow().getCodice();
                    try (Connection conn = getDataSource().getConnection()) {
                        process.premises().ifPresent(p -> Queries.insertPremises(p.getTipo(), p.getVia(), p.getNumcivico(),
                                p.getComune(), p.getCap(), p.getProvincia(), p.getInterno(), conn));
                        process.measurement().ifPresent(m -> Queries.insertMeasurement(m, conn));

                        final int result = Queries.insertActivationRequest(process, conn);
                        if (result != 0) {
                            FXUtils.showBlockingWarning("Richiesta inserita con successo.");
                            switchTo(HomeController.create(getStage(), getDataSource()));
                        } else {
                            FXUtils.showBlockingWarning(StringUtils.getGenericError());
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
        });
    }
}
