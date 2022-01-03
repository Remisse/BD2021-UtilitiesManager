package bdproject.controller.gui.users.parametersselection;

import bdproject.controller.gui.AbstractController;
import bdproject.controller.gui.users.CatalogueController;
import bdproject.model.SessionHolder;
import bdproject.model.SubscriptionProcess;
import bdproject.tables.pojos.*;
import bdproject.utils.ViewUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

import javax.sql.DataSource;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Optional;

public abstract class AbstractParametersSelectionController extends AbstractController implements Initializable {

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

    protected AbstractParametersSelectionController(final Stage stage, final DataSource dataSource, final SessionHolder holder,
                                                    final SubscriptionProcess process) {
        super(stage, dataSource, holder, FXML_FILE);
        this.process = process;
    }

    protected SubscriptionProcess getProcess() {
        return process;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        final Optional<Offerte> plan = process.plan();
        final TipiAttivazione activationMethod = process.activation().orElseThrow();

        plan.ifPresentOrElse(p -> {
            planLabel.setText(p.getNome());
            utilityLabel.setText(p.getMateriaprima());
        }, () -> {
            planLabel.setText("Non ancora specificata");
            utilityLabel.setText("Non ancora specificata");
        });
        useLabel.setText(process.usage().orElseThrow().getNome());
        methodLabel.setText(activationMethod.getNome());

        peopleNoLabel.setText("Num. componenti del nucleo familiare");
        peopleNoLabel.setVisible(true);
        peopleNoField.setVisible(true);

        final boolean requiresOtherClient = activationMethod.getRichiedevecchiointestatario() == 1;
        final boolean requiresMeterId = activationMethod.getRichiedematricolacontatore() == 1;
        final boolean requiresMeasurement = activationMethod.getRichiedelettura() == 1;

        otherClientIdLabel.setVisible(requiresOtherClient);
        otherClientIdField.setVisible(requiresOtherClient);
        meterIdField.setVisible(requiresMeterId);
        meterIdLabel.setVisible(requiresMeterId);
        measurementField.setVisible(requiresMeasurement);
        measurementLabel.setVisible(requiresMeasurement);

        process.otherClient().ifPresent(c -> otherClientIdField.setText(c.getIdpersona().toString()));
        process.meter().ifPresent(m -> {
            if (m.getMatricola() != null) {
                meterIdField.setText(m.getMatricola());
            }
        });
        process.measurement().ifPresent(m -> measurementField.setText(m.getConsumi().toString()));
        peopleNoField.setText(String.valueOf(process.peopleNo()));
    }

    protected boolean isValidNumber(String s) {
        return s.codePoints().allMatch(Character::isDigit) && Integer.parseInt(s) > 0 && Integer.parseInt(s) <= 20;
    }

    @FXML
    private void goBack() {
        ViewUtils.showConfirmationDialog(
                "Tornando al catalogo, tutti i dati inseriti verranno persi. Vuoi tornare al catalogo?",
                () -> switchTo(CatalogueController.create(stage(), dataSource(), getSessionHolder())));
    }

    protected abstract boolean areFieldsValid();

    protected abstract void abstractNextScreen();

    @FXML
    private void nextScreen() {
        if (!areFieldsValid()) {
            ViewUtils.showError("Verifica di aver inserito correttamente i dati.");
        } else {
            process.setPeopleNo(Integer.parseInt(peopleNoField.getText()));
            abstractNextScreen();
        }
    }
}
