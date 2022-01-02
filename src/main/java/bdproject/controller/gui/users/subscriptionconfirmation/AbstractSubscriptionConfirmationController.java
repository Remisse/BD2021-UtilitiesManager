package bdproject.controller.gui.users.subscriptionconfirmation;

import bdproject.controller.gui.AbstractController;
import bdproject.controller.gui.HomeController;
import bdproject.model.SessionHolder;
import bdproject.model.SubscriptionProcess;
import bdproject.utils.ViewUtils;
import bdproject.view.StringUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

import javax.sql.DataSource;
import java.net.URL;
import java.util.ResourceBundle;

import static org.jooq.impl.DSL.field;

public abstract class AbstractSubscriptionConfirmationController extends AbstractController implements Initializable {

    private static final String FXML = "subConfirmation.fxml";
    private static final String FLOW_CSS = "-fx-font: 16 arial";

    private final SubscriptionProcess process;

    @FXML private Label planText;
    @FXML private Label utilityText;
    @FXML private Label useText;
    @FXML private Label activationText;
    @FXML private Label currentClientLabel;
    @FXML private TextFlow premisesFlow;
    @FXML private TextFlow currentClientFlow;

    protected AbstractSubscriptionConfirmationController(final Stage stage, final DataSource dataSource, final SessionHolder holder,
                                                       final SubscriptionProcess process) {
        super(stage, dataSource, holder, FXML);
        this.process = process;
    }

    protected SubscriptionProcess getProcess() {
        return process;
    }

    protected abstract void setActivationSpecificData();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        planText.setText(process.plan().orElseThrow().getNome());
        utilityText.setText(process.plan().orElseThrow().getMateriaprima());
        useText.setText(process.usage().orElseThrow().getNome());
        activationText.setText(process.activation().orElseThrow().getNome());

        setPremisesText();

        setActivationSpecificData();
    }

    private void setPremisesText() {
        final Text premisesText = new Text(StringUtils.premiseToString(process.premise().orElseThrow()));
        premisesText.setStyle(FLOW_CSS);
        premisesFlow.getChildren().add(premisesText);
    }

    protected abstract void abstractGoBack();

    @FXML
    private void goBack() {
        abstractGoBack();
    }

    protected abstract int abstractInsertSubscription();

    @FXML
    private void insertSubscription() {
        ViewUtils.showConfirmationDialog(
            "Stai per richiedere l'attivazione di un contratto di fornitura. Vuoi continuare?", () -> {
            final int result = abstractInsertSubscription();
            if (result != 0) {
                ViewUtils.showBlockingWarning("Richiesta inserita con successo.");
                switchTo(HomeController.create(stage(), dataSource(), getSessionHolder()));
            } else {
                ViewUtils.showBlockingWarning("Errore nell'inserimento della richiesta!");
            }
        });
    }
}
