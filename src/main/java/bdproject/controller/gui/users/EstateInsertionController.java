package bdproject.controller.gui.users;

import bdproject.controller.Choice;
import bdproject.controller.ChoiceImpl;
import bdproject.controller.gui.AbstractController;
import bdproject.controller.gui.Controller;
import bdproject.model.Queries;
import bdproject.model.SessionHolder;
import bdproject.model.SubscriptionProcess;
import bdproject.tables.pojos.Immobili;
import bdproject.tables.pojos.TipiImmobile;
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
import java.util.stream.Collectors;

import static bdproject.tables.TipiImmobile.TIPI_IMMOBILE;

public class EstateInsertionController extends AbstractController implements Initializable {

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

    @FXML private ComboBox<Choice<TipiImmobile, String>> typeBox;

    private EstateInsertionController(final Stage stage, final DataSource dataSource, final SessionHolder holder,
            final String fxml, final SubscriptionProcess process) {
        super(stage, dataSource, holder, fxml);
        this.process = process;
    }

    public static Controller create(final Stage stage, final DataSource dataSource, final SessionHolder holder,
            final SubscriptionProcess process) {
        return new EstateInsertionController(stage, dataSource, holder, fxml, process);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        planLabel.setText(process.plan().orElseThrow().getNome());
        utilityLabel.setText(process.plan().orElseThrow().getMateriaprima());
        usageLabel.setText(process.usage().orElseThrow().getNome());
        methodLabel.setText(process.activation().orElseThrow().getNome());

        try (Connection conn = dataSource().getConnection()) {
            DSLContext ctx = DSL.using(conn, SQLDialect.MYSQL);
            final List<Choice<TipiImmobile, String>> types = Queries.fetchAll(ctx, TIPI_IMMOBILE, TipiImmobile.class)
                    .stream()
                    .map(t -> new ChoiceImpl<>(t, t.getNome(), (i, v) -> v))
                    .collect(Collectors.toList());
            typeBox.setItems(FXCollections.observableList(types));
            typeBox.setValue(types.get(0));

            process.estate().ifPresent(p -> {
                typeBox.setValue(types.stream()
                        .filter(c -> c.getItem().getCodice().equals(p.getTipo()))
                        .findFirst()
                        .orElseThrow());
                streetField.setText(p.getVia());
                streetNoField.setText(p.getNumcivico());
                municipalityField.setText(p.getComune());
                postcodeField.setText(p.getCap());
                provinceField.setText(p.getProvincia());
                apartmentNumberField.setText(p.getInterno() == null ? "" : p.getInterno());
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        apartmentNumberField.setDisable(typeBox.getValue().getItem().getHainterno() == 0);
        apartmentNumberField.clear();
    }

    @FXML
    private void goBack() {
        switchTo(ParametersSelectionController.create(stage(), dataSource(), sessionHolder(), process));
    }

    @FXML
    private void nextScreen() {
        if (areFieldsValid()) {
            final Immobili newPremises = new Immobili(
                    0,
                    typeBox.getValue().getItem().getCodice(),
                    streetField.getText(),
                    streetNoField.getText(),
                    apartmentNumberField.getText().equals("") ? null : apartmentNumberField.getText(),
                    municipalityField.getText(),
                    provinceField.getText(),
                    postcodeField.getText());
            try (Connection conn = dataSource().getConnection()) {
                final DSLContext ctx = DSL.using(conn, SQLDialect.MYSQL);
                final Optional<Immobili> existingEstate = Queries.fetchEstateByCandidateKey(
                        newPremises.getComune(),
                        newPremises.getNumcivico(),
                        newPremises.getInterno(),
                        newPremises.getComune(),
                        newPremises.getProvincia(),
                        ctx);

                existingEstate.ifPresentOrElse(process::setEstate, () -> process.setEstate(newPremises));
            } catch (Exception e) {
                e.printStackTrace();
            }
            switchTo(SubscriptionConfirmationController.create(stage(), dataSource(), sessionHolder(), process));
        } else {
            FXUtils.showBlockingWarning("Verifica di aver inserito correttamente i dati.");
        }
    }
}
