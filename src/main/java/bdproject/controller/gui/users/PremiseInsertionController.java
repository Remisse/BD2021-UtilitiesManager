package bdproject.controller.gui.users;

import bdproject.controller.Choice;
import bdproject.controller.ChoiceImpl;
import bdproject.controller.gui.AbstractController;
import bdproject.controller.gui.Controller;
import bdproject.controller.gui.users.parametersselection.ParametersClientChangeController;
import bdproject.controller.gui.users.parametersselection.ParametersNewActivationController;
import bdproject.controller.gui.users.parametersselection.ParametersSubentroController;
import bdproject.controller.gui.users.subscriptionconfirmation.NewActivationConfirmationController;
import bdproject.controller.gui.users.subscriptionconfirmation.SubentroConfirmationController;
import bdproject.model.types.ActivationType;
import bdproject.model.types.PremiseType;
import bdproject.model.Queries;
import bdproject.model.SessionHolder;
import bdproject.model.SubscriptionProcess;
import bdproject.tables.pojos.Immobili;
import bdproject.utils.ViewUtils;
import bdproject.view.StringUtils;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import javax.sql.DataSource;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class PremiseInsertionController extends AbstractController implements Initializable {

    private static final int POSTCODE_LENGTH = 5;
    private static final int PROVINCE_LENGTH = 2;
    private static final int STREET_LENGTH = 50;
    private static final int STREET_NO_LENGTH = 10;
    private static final int MUNICIPALITY_LENGTH = 50;
    private static final int AP_LENGTH = 10;

    private static final String fxml = "estateInsertion.fxml";
    private final SubscriptionProcess process;

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

    @FXML private ComboBox<Choice<PremiseType, String>> typeBox;

    private PremiseInsertionController(final Stage stage, final DataSource dataSource, final SessionHolder holder,
                                       final String fxml, final SubscriptionProcess process) {
        super(stage, dataSource, holder, fxml);
        this.process = process;
    }

    public static Controller create(final Stage stage, final DataSource dataSource, final SessionHolder holder,
            final SubscriptionProcess process) {
        return new PremiseInsertionController(stage, dataSource, holder, fxml, process);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        planLabel.setText(process.plan().orElseThrow().getNome());
        utilityLabel.setText(process.plan().orElseThrow().getMateriaprima());
        usageLabel.setText(process.usage().orElseThrow().getNome());
        methodLabel.setText(process.activation().orElseThrow().getNome());

        final List<Choice<PremiseType, String>> types = new ArrayList<>();
        for (PremiseType type : PremiseType.values()) {
            types.add(new ChoiceImpl<>(type, type.toString(), (i, v) -> v));
        }

        typeBox.setItems(FXCollections.observableList(types));
        typeBox.setValue(types.get(0));

        process.premise().ifPresent(p -> {
            typeBox.setValue(types.stream()
                    .filter(c -> c.getValue().equals(p.getTipo()))
                    .findFirst()
                    .orElseThrow());
            streetField.setText(p.getVia());
            streetNoField.setText(p.getNumcivico());
            municipalityField.setText(p.getComune());
            postcodeField.setText(p.getCap());
            provinceField.setText(p.getProvincia());
            apartmentNumberField.setText(p.getInterno());
        });
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
        apartmentNumberField.setDisable(typeBox.getValue().getItem() != PremiseType.FABBRICATO);
        apartmentNumberField.clear();
    }

    @FXML
    private void goBack() {
        final String activation = process.activation().orElseThrow().getNome();

        if (activation.equals(ActivationType.NEW_ACTIVATION.toString())) {
            switchTo(ParametersNewActivationController.create(stage(), dataSource(), getSessionHolder(), process));
        } else if (activation.equals(ActivationType.SUBENTRO.toString())) {
            switchTo(ParametersSubentroController.create(stage(), dataSource(), getSessionHolder(), process));
        } else if (activation.equals(ActivationType.VOLTURA.toString())) {
            switchTo(ParametersClientChangeController.create(stage(), dataSource(), getSessionHolder(), process));
        } else {
            ViewUtils.showError("Metodo di attivazione non gestito!");
        }
    }

    @FXML
    private void nextScreen() {
        if (areFieldsValid()) {
            Optional<Immobili> existingPremise = Optional.empty();

            /*
             * Search for an existing premise with the exact same data
             */
            try (final Connection conn = dataSource().getConnection()) {
                existingPremise = Queries.fetchPremiseByCandidateKey(
                    typeBox.getValue().getValue(),
                    streetField.getText(),
                    streetNoField.getText(),
                    apartmentNumberField.getText(),
                    municipalityField.getText(),
                    postcodeField.getText(),
                    provinceField.getText(),
                    conn);
            } catch (SQLException e) {
                e.printStackTrace();
                ViewUtils.showError(StringUtils.getGenericError());
            }

            existingPremise.ifPresentOrElse(process::setPremise, () -> process.setPremise(new Immobili(
                            0,
                            typeBox.getValue().getValue(),
                            streetField.getText(),
                            streetNoField.getText(),
                            apartmentNumberField.getText(),
                            municipalityField.getText(),
                            provinceField.getText(),
                            postcodeField.getText())
            ));

            final String activation = process.activation().orElseThrow().getNome();
            if (activation.equals(ActivationType.NEW_ACTIVATION.toString())) {
                switchTo(NewActivationConfirmationController.create(stage(), dataSource(), getSessionHolder(), process,
                        existingPremise.isPresent()));
            } else if (activation.equals(ActivationType.SUBENTRO.toString())) {
                switchTo(SubentroConfirmationController.create(stage(), dataSource(), getSessionHolder(), process,
                        existingPremise.isPresent(), false));
            } else if (activation.equals(ActivationType.VOLTURA.toString())) {
                switchTo(ParametersClientChangeController.create(stage(), dataSource(), getSessionHolder(), process));
            } else {
                ViewUtils.showError("Metodo di attivazione non gestito!");
            }
        } else {
            ViewUtils.showBlockingWarning("Verifica di aver inserito correttamente i dati.");
        }
    }
}
