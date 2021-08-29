package bdproject.controller.gui;

import bdproject.model.SubscriptionProcess;
import bdproject.tables.pojos.TipiAttivazione;
import bdproject.utils.FXUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import javax.sql.DataSource;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ResourceBundle;

import static bdproject.tables.TipiAttivazione.TIPI_ATTIVAZIONE;

public class ParametersSelectionController extends AbstractViewController implements Initializable {

    private static final String FXML_FILE = "meterAndActivation.fxml";
    private final SubscriptionProcess process;

    @FXML private Label planLabel;
    @FXML private Label utilityLabel;
    @FXML private Label useLabel;
    @FXML private Label methodChosen;
    @FXML private Label methodLabel;
    @FXML private Button back;
    @FXML private Button next;
    @FXML private RadioButton meterYes;
    @FXML private RadioButton meterNo;
    @FXML private Label changeLabel;
    @FXML private RadioButton changeYes;
    @FXML private RadioButton changeNo;
    @FXML private Label peopleNoLabel;
    @FXML private TextField peopleNoField;

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

        if (process.usage().orElseThrow().getNome().equals("Commerciale")) {
            peopleNoLabel.setText("Numero di dipendenti");
        } else if (process.usage().orElseThrow().getNome().equals("Abitativo residenziale")) {
            peopleNoLabel.setText("N. componenti del nucleo familiare");
        } else {
            peopleNoLabel.setVisible(false);
            peopleNoField.setVisible(false);
        }
        peopleNoField.setText(String.valueOf(process.peopleNo()));

        updateElements();
    }

    private boolean isNewActivation() {
        return meterNo.isSelected();
    }

    private boolean isSubentro() {
        return meterYes.isSelected() && changeNo.isSelected();
    }

    private boolean isChange() {
        return changeYes.isSelected() && meterYes.isSelected();
    }

    private boolean requiresPeopleNo() {
        final String use = process.usage().orElseThrow().getNome();
        return use.equals("Commerciale") || use.equals("Abitativo residenziale");
    }

    private boolean allRelevantChoicesMade() {
        return isNewActivation() || isSubentro() || isChange();
    }

    @FXML
    private void updateElements() {
        final boolean canChooseChange = meterYes.isSelected();
        changeYes.setVisible(canChooseChange);
        changeNo.setVisible(canChooseChange);
        changeLabel.setVisible(canChooseChange);

        methodLabel.setVisible(allRelevantChoicesMade());
        methodChosen.setVisible(allRelevantChoicesMade());
        methodChosen.setText(isNewActivation() ? "Nuova attivazione"
                             : isSubentro() ? "Subentro"
                             : isChange() ? "Voltura"
                             : "Non ancora selezionato");

        next.setDisable(!allRelevantChoicesMade());
    }

    private void updateProcess() {
        process.setPeopleNo(requiresPeopleNo() ? Integer.parseInt(peopleNoField.getText()) : 1);

        final String method = isNewActivation() ? "Nuova attivazione"
                                                : isSubentro() ? "Subentro"
                                                               : isChange() ? "Voltura"
                                                                            : null;
        if (method != null) {
            try (Connection conn = getDataSource().getConnection()) {
                var selectedActivation = DSL.using(conn, SQLDialect.MYSQL)
                        .select()
                        .from(TIPI_ATTIVAZIONE)
                        .where(TIPI_ATTIVAZIONE.NOME.eq(method))
                        .fetchOneInto(TipiAttivazione.class);
                process.setActivationMethod(selectedActivation);
            } catch (SQLException ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR, ex.getMessage());
                alert.showAndWait();
            }
        } else {
            process.setActivationMethod(null);
        }
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
        if (requiresPeopleNo() && !isValidNumber(peopleNoField.getText())) {
            FXUtils.showError("Verifica che il numero di persone inserito sia corretto.");
        } else {
            updateProcess();
            if (isChange()) {
                switchTo(ActivationByChangeController.create(getStage(), getDataSource(), process));
            }
        }
    }
}
