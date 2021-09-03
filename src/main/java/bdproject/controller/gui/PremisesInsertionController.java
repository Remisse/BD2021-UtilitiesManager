package bdproject.controller.gui;

import bdproject.controller.Choice;
import bdproject.controller.ChoiceImpl;
import bdproject.model.Queries;
import bdproject.model.SubscriptionProcess;
import bdproject.tables.pojos.Contatori;
import bdproject.tables.pojos.Immobili;
import bdproject.utils.FXUtils;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import javax.sql.DataSource;
import java.net.URL;
import java.sql.Connection;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class PremisesInsertionController extends AbstractViewController implements Initializable {

    private static final int POSTCODE_LENGTH = 5;
    private static final int PROVINCE_LENGTH = 2;
    private static final int STREET_LENGTH = 50;
    private static final int STREET_NO_LENGTH = 10;
    private static final int MUNICIPALITY_LENGTH = 50;
    private static final int AP_LENGTH = 10;

    private static final String fxml = "premises.fxml";
    private final SubscriptionProcess process;
    private final List<Choice<String, String>> typeList = List.of(
            new ChoiceImpl<>("Fabbricato", "F", (i, v) -> i),
            new ChoiceImpl<>("Terreno", "T", (i, v) -> i));

    @FXML private Label planLabel;
    @FXML private Label utilityLabel;
    @FXML private Label usageLabel;
    @FXML private Label methodLabel;

    @FXML private TextField streetField;
    @FXML private TextField municipalityField;
    @FXML private TextField streetNoField;
    @FXML private TextField postcodeField;
    @FXML private TextField apartmentNumberField;
    @FXML private TextField provinceField;

    @FXML private ComboBox<Choice<String, String>> typeBox;

    private PremisesInsertionController(final Stage stage, final DataSource dataSource, final String fxml,
            final SubscriptionProcess process) {
        super(stage, dataSource, fxml);
        this.process = process;
    }

    public static ViewController create(final Stage stage, final DataSource dataSource,
            final SubscriptionProcess process) {
        return new PremisesInsertionController(stage, dataSource, fxml, process);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        planLabel.setText(process.plan().orElseThrow().getNome());
        utilityLabel.setText(process.plan().orElseThrow().getMateriaprima());
        usageLabel.setText(process.usage().orElseThrow().getNome());
        methodLabel.setText(process.activation().orElseThrow().getNome());

        typeBox.setItems(FXCollections.observableList(typeList));
        typeBox.setValue(typeList.get(0));
    }

    private boolean areFieldsValid() {
        return streetField.getText().length() > 0 && streetField.getText().length() <= STREET_LENGTH &&
                streetNoField.getText().length() > 0 && streetNoField.getText().length() <= STREET_NO_LENGTH &&
                municipalityField.getText().length() > 0 && municipalityField.getText().length() <= MUNICIPALITY_LENGTH &&
                postcodeField.getText().length() == POSTCODE_LENGTH &&
                provinceField.getText().length() == PROVINCE_LENGTH &&
                apartmentNumberField.getText().length() <= AP_LENGTH;
    }

    @FXML
    private void toggleApartmentNumberField() {
        apartmentNumberField.setDisable(typeBox.getValue().getValue().equals("T"));
        apartmentNumberField.clear();
    }

    @FXML
    private void goBack() {
        switchTo(ParametersSelectionController.create(getStage(), getDataSource(), process));
    }

    @FXML
    private void nextScreen() {
        if (areFieldsValid()) {
            final Immobili newPremises = new Immobili(
                    0,
                    typeBox.getValue().getValue(),
                    streetField.getText(),
                    streetNoField.getText(),
                    apartmentNumberField.getText().equals("") ? null : apartmentNumberField.getText(),
                    municipalityField.getText(),
                    provinceField.getText(),
                    postcodeField.getText());
            try (Connection conn = getDataSource().getConnection()) {
                final DSLContext ctx = DSL.using(conn, SQLDialect.MYSQL);
                final Optional<Immobili> existingPremises = Queries.fetchPremisesByCandidateKey(
                        newPremises.getComune(),
                        newPremises.getNumcivico(),
                        newPremises.getInterno(),
                        newPremises.getComune(),
                        newPremises.getProvincia(),
                        ctx);

                existingPremises.ifPresentOrElse(process::setPremises, () -> process.setPremises(newPremises));
            } catch (Exception e) {
                e.printStackTrace();
            }
            switchTo(SubscriptionConfirmationController.create(getStage(), getDataSource(), process));
        } else {
            FXUtils.showBlockingWarning("Verifica di aver inserito correttamente i dati.");
        }
    }
}
