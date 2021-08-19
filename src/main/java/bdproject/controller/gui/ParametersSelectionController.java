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

    private static final String FXML = "meterAndActivation.fxml";
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
    private Label methodLabel;
    @FXML
    private Button back;
    @FXML
    private Button next;
    @FXML
    private RadioButton meterYes;
    @FXML
    private RadioButton meterNo;
    @FXML
    private Label changeLabel;
    @FXML
    private RadioButton changeYes;
    @FXML
    private RadioButton changeNo;
    @FXML
    private Label peopleNoLabel;
    @FXML
    private TextField peopleNoField;
    @FXML
    private Label powerLabel;
    @FXML
    private Slider powerSlider;
    @FXML
    private Label powerChosen;

    private ParametersSelectionController(final Stage stage, final DataSource dataSource,
            final SubscriptionProcess process) {
        super(stage, dataSource, FXML);
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
        useLabel.setText(process.usage().orElseThrow());

        if (process.usage().orElseThrow().equals("Commerciale")) {
            peopleNoLabel.setText("Numero di dipendenti");
        } else if (process.usage().orElseThrow().equals("Abitativo residenziale")) {
            peopleNoLabel.setText("N. componenti del nucleo familiare");
        } else {
            peopleNoLabel.setVisible(false);
            peopleNoField.setVisible(false);
        }
        peopleNoField.setText(String.valueOf(process.peopleNo()));

        if (!requiresPower()) {
            powerLabel.setVisible(false);
            powerSlider.setVisible(false);
            powerChosen.setVisible(false);
        } else {
            powerSlider.setValue(process.powerRequested());
            updatePowerChosen();
        }

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
        final String use = process.usage().orElseThrow();
        return use.equals("Commerciale") || use.equals("Abitativo residenziale");
    }

    private boolean requiresPower() {
        final String utility = process.plan().orElseThrow().getMateriaprima();
        return utility.equals("Luce");
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

    @FXML
    private void updatePowerChosen() {
        powerChosen.setText(String.valueOf(powerSlider.getValue()));
    }

    private void updateProcess() {
        process.setPeopleNo(requiresPeopleNo() ? Integer.parseInt(peopleNoField.getText()) : 1);
        process.setPowerRequested(requiresPower() ? powerSlider.getValue() : 0.0);

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
        final ButtonType toMenu = new ButtonType("Torna al menu", ButtonBar.ButtonData.OK_DONE);
        final ButtonType stay = new ButtonType("Resta qui", ButtonBar.ButtonData.CANCEL_CLOSE);
        Alert alert = new Alert(
                Alert.AlertType.WARNING,
                "È in corso la creazione di un nuovo contratto. Tornando al catalogo, " +
                        "tutti i dati inseriti verranno persi.",
                toMenu, stay);
        alert.showAndWait().ifPresent(response -> {
            if (response == toMenu) {
                switchTo(CatalogueController.create(getStage(), getDataSource()));
            } else {
                alert.close();
            }
        });
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
