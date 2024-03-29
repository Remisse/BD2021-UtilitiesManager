package bdproject.controller.gui.operators;

import bdproject.controller.Choice;
import bdproject.controller.ChoiceImpl;
import bdproject.controller.gui.AbstractController;
import bdproject.controller.gui.Controller;
import bdproject.model.types.PremiseType;
import bdproject.model.Queries;
import bdproject.model.SessionHolder;
import bdproject.model.types.UtilityType;
import bdproject.tables.pojos.Contatori;
import bdproject.tables.pojos.Immobili;
import bdproject.utils.ViewUtils;
import bdproject.view.StringUtils;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.jooq.DSLContext;

import javax.sql.DataSource;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

import static bdproject.tables.Immobili.IMMOBILI;
import static bdproject.tables.Contatori.CONTATORI;

public class PremiseManagementController extends AbstractController implements Initializable {

    private static final String FXML_FILE = "adminPremiseManagement.fxml";
    private static final int POSTCODE_LENGTH = 5;
    private static final int PROVINCE_LENGTH = 2;
    private static final int STREET_LENGTH = 50;
    private static final int STREET_NO_LENGTH = 10;
    private static final int MUNICIPALITY_LENGTH = 50;
    private static final int AP_LENGTH = 10;

    @FXML private TableView<Immobili> premiseTable;
    @FXML private TableColumn<Immobili, String> premiseIdCol;
    @FXML private TableColumn<Immobili, String> premiseStreetAndCivicCol;
    @FXML private TableColumn<Immobili, String> premiseUnitCol;
    @FXML private TableColumn<Immobili, String> premiseMunicipCol;
    @FXML private TableColumn<Immobili, String> premisePostcodeCol;
    @FXML private TableColumn<Immobili, String> premiseProvinceCol;
    @FXML private TableColumn<Immobili, String> premiseTypeCol;

    @FXML private TableView<Contatori> meterTable;
    @FXML private TableColumn<Contatori, String> meterIdCol;
    @FXML private TableColumn<Contatori, String> meterUtilityCol;
    @FXML private TableColumn<Contatori, String> meterPremiseCol;
    @FXML private ComboBox<String> findMeterUtilityBox;
    @FXML private TextField meterIdField;
    @FXML private ComboBox<String> setMeterUtilityBox;

    @FXML private TextField streetField;
    @FXML private TextField municipalityField;
    @FXML private TextField streetNoField;
    @FXML private TextField postcodeField;
    @FXML private TextField apartmentNumberField;
    @FXML private TextField provinceField;
    @FXML private ComboBox<Choice<PremiseType, String>> typeBox;



    private PremiseManagementController(final Stage stage, final DataSource dataSource, final SessionHolder holder) {
        super(stage, dataSource, holder, FXML_FILE);
    }

    public static Controller create(final Stage stage, final DataSource dataSource, final SessionHolder holder) {
        return new PremiseManagementController(stage, dataSource, holder);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        final List<String> utilities = Arrays.stream(UtilityType.values())
                .map(UtilityType::toString)
                .collect(Collectors.toList());
        final List<Choice<PremiseType, String>> types = new ArrayList<>();
        Arrays.stream(PremiseType.values())
                .forEach(t -> types.add(new ChoiceImpl<>(t, t.toString(), (i, v) -> v)));

        initializePremiseTable();
        initializeMeterTable();

        Platform.runLater(() -> {
            findMeterUtilityBox.setItems(FXCollections.observableList(utilities));
            setMeterUtilityBox.setItems(FXCollections.observableList(utilities));
            typeBox.setItems(FXCollections.observableList(types));
            typeBox.setValue(types.get(0));
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

    private void initializePremiseTable() {
        premiseIdCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getIdimmobile().toString()));
        premiseMunicipCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getComune()));
        premisePostcodeCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCap()));
        premiseProvinceCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getProvincia()));
        premiseTypeCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTipo()));
        premiseUnitCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getInterno()));
        premiseStreetAndCivicCol.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getVia() + ", " + c.getValue().getNumcivico()));
        updatePremiseTable();
    }

    private void updatePremiseTable() {
        try (final Connection conn = dataSource().getConnection()) {
            final DSLContext ctx = Queries.createContext(conn);
            final List<Immobili> premises = Queries.fetchAll(ctx, IMMOBILI, Immobili.class);
            premiseTable.setItems(FXCollections.observableList(premises));
        } catch (SQLException e) {
            e.printStackTrace();
            ViewUtils.showError(StringUtils.getGenericError());
        }
    }

    private void initializeMeterTable() {
        meterIdCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getMatricola()));
        meterUtilityCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getMateriaprima()));
        meterPremiseCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getIdimmobile().toString()));
        updateMeterTable();
    }

    private void updateMeterTable() {
        try (final Connection conn = dataSource().getConnection()) {
            final DSLContext ctx = Queries.createContext(conn);
            final List<Contatori> meters = Queries.fetchAll(ctx, CONTATORI, Contatori.class);
            meterTable.setItems(FXCollections.observableList(meters));
        } catch (SQLException e) {
            e.printStackTrace();
            ViewUtils.showError(e.getMessage());
        }
    }

    @FXML
    private void copyRowIntoFields() {
        final List<Choice<PremiseType, String>> types = typeBox.getItems();

        final Immobili selected = premiseTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            streetField.setText(selected.getVia());
            streetNoField.setText(selected.getNumcivico());
            apartmentNumberField.setText(selected.getInterno());
            municipalityField.setText(selected.getComune());
            postcodeField.setText(selected.getCap());
            provinceField.setText(selected.getProvincia());
            typeBox.setValue(types.stream()
                    .filter(c -> c.getValue()
                            .equals(selected.getTipo()))
                    .findFirst()
                    .orElseThrow());
        }
    }

    @FXML
    private void doEditPremise() {
        ViewUtils.showConfirmationDialog("Vuoi davvero modificare i dati dell'immobile?", () -> {
            final Immobili selected = premiseTable.getSelectionModel().getSelectedItem();

            if (selected != null) {
                if (areFieldsValid()) {
                    try (final Connection conn = dataSource().getConnection()) {
                        final int result = Queries.updatePremise(
                                selected.getIdimmobile(),
                                typeBox.getSelectionModel().getSelectedItem().getValue(),
                                streetField.getText(),
                                streetNoField.getText(),
                                apartmentNumberField.getText(),
                                municipalityField.getText(),
                                postcodeField.getText(),
                                provinceField.getText(),
                                conn);
                        if (result == 1) {
                            updatePremiseTable();
                            ViewUtils.showBlockingWarning("Dati dell'immobile modificati.");
                        } else {
                            ViewUtils.showError("Impossibile modificati i dati.");
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                        ViewUtils.showError(e.getMessage());
                    }
                } else {
                    ViewUtils.showBlockingWarning("Verifica di aver inserito correttamente i dati.");
                }
            } else {
                ViewUtils.showBlockingWarning("Seleziona un immobile.");
            }
        });
    }

    @FXML
    private void findMeter() {
        final Immobili selectedPremise = premiseTable.getSelectionModel().getSelectedItem();

        if (selectedPremise != null) {
            final String selectedUtility = findMeterUtilityBox.getSelectionModel().getSelectedItem();

            if (selectedUtility != null) {
                try (final Connection conn = dataSource().getConnection()) {
                    final Optional<Contatori> meter = Queries.fetchMeterByPremiseIdAndUtility(
                            selectedPremise.getIdimmobile(), selectedUtility, conn);

                    meter.ifPresentOrElse(m -> {
                        final Contatori toSelect = meterTable.getItems()
                                        .stream()
                                        .filter(m1 -> m1.getMatricola().equals(m.getMatricola()))
                                        .findFirst()
                                        .orElseThrow();
                        Platform.runLater(() -> {
                            meterTable.requestFocus();
                            meterTable.getSelectionModel().select(toSelect);
                            meterTable.scrollTo(m);
                        });
                    }, () -> ViewUtils.showBlockingWarning("A questo immobile non è associato un contatore per " +
                                    "la misurazione della materia prima selezionata."));
                } catch (SQLException e) {
                    e.printStackTrace();
                    ViewUtils.showError(e.getMessage());
                }
            } else {
                ViewUtils.showBlockingWarning("Seleziona una materia prima dal menu a tendina.");
            }
        } else {
            ViewUtils.showBlockingWarning("Seleziona un immobile.");
        }
    }

    @FXML
    private void doSetMeter() {
        final Immobili selectedPremise = premiseTable.getSelectionModel().getSelectedItem();

        if (selectedPremise != null) {
            final String utility = setMeterUtilityBox.getSelectionModel().getSelectedItem();

            if (utility != null) {
                if (meterIdField.getText().length() > 0) {
                    ViewUtils.showConfirmationDialog("Stai per inserire o modificare un contatore. Vuoi davvero " +
                            "continuare?", () -> {
                        try (final Connection conn = dataSource().getConnection()) {
                            final int result = Queries.insertMeter(meterIdField.getText(), utility,
                                    selectedPremise.getIdimmobile(), conn);
                            if (result == 1) {
                                updateMeterTable();
                                ViewUtils.showBlockingWarning("Matricola inserita.");
                            } else {
                                ViewUtils.showBlockingWarning("Impossibile inserire la matricola.");
                            }
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    });
                } else {
                    ViewUtils.showBlockingWarning("Verifica che la matricola sia stata scritta correttamente.");
                }
            } else {
                ViewUtils.showBlockingWarning("Seleziona una materia prima dal menu a tendina.");
            }
        } else {
            ViewUtils.showBlockingWarning("Seleziona una richiesta di attivazione.");
        }
    }

    @FXML
    private void goBack() {
        switchTo(AreaSelectorController.create(stage(), dataSource(), getSessionHolder()));
    }
}
