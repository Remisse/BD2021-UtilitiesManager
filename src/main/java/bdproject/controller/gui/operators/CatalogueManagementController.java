package bdproject.controller.gui.operators;

import bdproject.controller.Checks;
import bdproject.controller.gui.AbstractController;
import bdproject.controller.gui.Controller;
import bdproject.model.Queries;
import bdproject.model.SessionHolder;
import bdproject.model.types.UtilityType;
import bdproject.tables.pojos.Offerte;
import bdproject.tables.pojos.TipologieUso;
import bdproject.utils.ViewUtils;
import bdproject.utils.LocaleUtils;
import bdproject.view.StringUtils;
import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.jooq.DSLContext;
import org.jooq.Record4;
import org.jooq.exception.DataAccessException;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Connection;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static bdproject.tables.Offerte.OFFERTE;
import static bdproject.tables.TipologieUso.TIPOLOGIE_USO;

public class CatalogueManagementController extends AbstractController implements Initializable {

    private static final String FXML_FILE = "catalogueManagement.fxml";
    private static final int USE_NAME_LIMIT = 30;

    private final Map<String, String> mUnit = LocaleUtils.getItPriceUnits();

    @FXML private TableView<Offerte> planTable;
    @FXML private TableColumn<Offerte, String> nameCol;
    @FXML private TableColumn<Offerte, Integer> idCol;
    @FXML private TableColumn<Offerte, String> descriptionCol;
    @FXML private TableColumn<Offerte, String> utilityCol;
    @FXML private TableColumn<Offerte, String> costCol;
    @FXML private TableColumn<Offerte, String> activeCol;

    @FXML private TextField nameField;
    @FXML private TextArea descriptionArea;
    @FXML private ComboBox<String> utilityBox;
    @FXML private TextField costField;
    @FXML private RadioButton activeYes;
    @FXML private RadioButton activeNo;

    @FXML private TableView<TipologieUso> useTable;
    @FXML private TableColumn<TipologieUso, Integer> useIdCol;
    @FXML private TableColumn<TipologieUso, String> useNameCol;
    @FXML private TableColumn<TipologieUso, String> useEstimateCol;
    @FXML private TableColumn<TipologieUso, String> useDiscountCol;

    @FXML private TextField useNameField;
    @FXML private TextField useEstimateField;
    @FXML private CheckBox discountApplicable;

    @FXML private TableView<Record4<Integer, String, Integer, String>> compatibilityTable;
    @FXML private TableColumn<Record4<Integer, String, Integer, String>, String> compatUseCol;
    @FXML private TableColumn<Record4<Integer, String, Integer, String>, String> compatPlanCol;

    private CatalogueManagementController(final Stage stage, final DataSource dataSource, final SessionHolder holder) {
        super(stage, dataSource, holder, FXML_FILE);
    }

    public static Controller create(final Stage stage, final DataSource dataSource, final SessionHolder holder) {
        return new CatalogueManagementController(stage, dataSource, holder);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        utilityBox.setItems(FXCollections.observableList(Arrays.stream(UtilityType.values())
                .map(UtilityType::toString)
                .collect(Collectors.toList())));
        descriptionArea.setWrapText(true);
        initPlanTable();
        refreshPlanTable();
        initUseTable();
        refreshUseTable();
        initCompatibilityTable();
        refreshCompatibilityTable();
    }

    private void initPlanTable() {
        Platform.runLater(() -> {
            nameCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNome()));
            idCol.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getCodofferta()).asObject());
            descriptionCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDescrizione()));
            utilityCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getMateriaprima()));
            costCol.setCellValueFactory(c -> new SimpleStringProperty(
                    DecimalFormat.getInstance().format(c.getValue().getCostomateriaprima()) + " " +
                            mUnit.get(c.getValue().getMateriaprima())));
            activeCol.setCellValueFactory(c -> new SimpleStringProperty(StringUtils.byteToYesNo(c.getValue().getAttiva())));
        });
    }

    private void refreshPlanTable() {
        List<Offerte> plans = Collections.emptyList();

        try (Connection conn = dataSource().getConnection()) {
            final DSLContext ctx = Queries.createContext(conn);
            plans = Queries.fetchAll(ctx, OFFERTE, Offerte.class);
        } catch (Exception e) {
            e.printStackTrace();
        }

        final List<Offerte> finalPlans = plans;
        Platform.runLater(() -> planTable.setItems(FXCollections.observableList(finalPlans)));
    }

    private boolean arePlanFieldsValid() {
        return nameField.getText().length() > 0 &&
                descriptionArea.getText().length() > 0 &&
                Checks.isBigDecimal(costField.getText());
    }

    @FXML
    private void doAdd() {
        if (arePlanFieldsValid()) {
            try (Connection conn = dataSource().getConnection()) {
                final DSLContext ctx = Queries.createContext(conn);
                final int result = Queries.insertPlan(
                        nameField.getText(),
                        descriptionArea.getText(),
                        utilityBox.getValue(),
                        new BigDecimal(costField.getText()),
                        activeYes.isSelected(),
                        ctx
                        );
                if (result == 1) {
                    ViewUtils.showBlockingWarning("Offerta inserita.");
                    refreshPlanTable();
                } else {
                    ViewUtils.showBlockingWarning(StringUtils.getGenericError());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            ViewUtils.showBlockingWarning("Controlla i dati immessi.");
        }
    }

    @FXML
    private void doEdit() {
        final Offerte selectedPlan = planTable.getSelectionModel().getSelectedItem();
        if (selectedPlan != null && arePlanFieldsValid()) {
            ViewUtils.showConfirmationDialog("Verranno modificati nome, descrizione e stato di attivazione. Vuoi continuare?", () -> {
                try (Connection conn = dataSource().getConnection()) {
                    final DSLContext ctx = Queries.createContext(conn);
                    final int result = Queries.updatePlan(
                            selectedPlan.getCodofferta(),
                            nameField.getText(),
                            descriptionArea.getText(),
                            activeYes.isSelected(),
                            ctx
                    );
                    if (result == 1) {
                        ViewUtils.showBlockingWarning("Offerta aggiornata.");
                        refreshPlanTable();
                    } else {
                        ViewUtils.showBlockingWarning(StringUtils.getGenericError());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } else {
            if (selectedPlan == null) {
                ViewUtils.showBlockingWarning("Seleziona un'offerta da modificare.");
            } else {
                ViewUtils.showBlockingWarning("Controlla i dati immessi.");
            }
        }
    }

    @FXML
    private void doDelete() {
        final Offerte selectedPlan = planTable.getSelectionModel().getSelectedItem();
        if (selectedPlan != null) {
            try (Connection conn = dataSource().getConnection()) {
                final DSLContext ctx = Queries.createContext(conn);
                final int result = Queries.deleteGeneric(OFFERTE, OFFERTE.CODOFFERTA, selectedPlan.getCodofferta(), ctx);
                if (result == 1) {
                    ViewUtils.showBlockingWarning("Offerta eliminata.");
                    refreshPlanTable();
                } else {
                    ViewUtils.showBlockingWarning(StringUtils.getGenericError());
                }
            } catch (DataAccessException e1) {
                ViewUtils.showBlockingWarning("L'offerta è associata a richieste, contratti e/o compatibilità. " +
                        "Impossibile eliminare.");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            ViewUtils.showBlockingWarning("Seleziona un'offerta da eliminare.");
        }
    }

    @FXML
    private void doPasteIntoFields() {
        final Offerte selectedPlan = planTable.getSelectionModel().getSelectedItem();
        if (selectedPlan != null) {
            Platform.runLater(() -> {
                nameField.setText(selectedPlan.getNome());
                descriptionArea.setText(selectedPlan.getDescrizione());
                utilityBox.setValue(selectedPlan.getMateriaprima());
                costField.setText(selectedPlan.getCostomateriaprima().toString());
                if (selectedPlan.getAttiva() == 1) {
                    activeYes.setSelected(true);
                } else {
                    activeNo.setSelected(true);
                }
            });
        }
    }

    private void initUseTable() {
        Platform.runLater(() -> {
            useIdCol.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getCoduso()).asObject());
            useNameCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNome()));
            useEstimateCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStimaperpersona().toString()));
            useDiscountCol.setCellValueFactory(c -> new SimpleStringProperty(StringUtils.byteToYesNo(c.getValue()
                    .getScontoreddito())));
        });
    }

    private void refreshUseTable() {
        try (Connection conn = dataSource().getConnection()) {
            final DSLContext ctx = Queries.createContext(conn);
            final List<TipologieUso> uses = Queries.fetchAll(ctx, TIPOLOGIE_USO, TipologieUso.class);

            Platform.runLater(() ->useTable.setItems(FXCollections.observableList(uses)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void doAddUse() {
        if (areUseFieldsValid()) {
            try (Connection conn = dataSource().getConnection())  {
                final DSLContext ctx = Queries.createContext(conn);
                final int result = Queries.insertUse(
                        useNameField.getText(),
                        new BigDecimal(useEstimateField.getText()),
                        discountApplicable.isSelected() ? (byte) 1 : (byte) 0,
                        ctx);
                if (result == 1) {
                    ViewUtils.showBlockingWarning("Uso inserito.");
                    refreshUseTable();
                } else {
                    ViewUtils.showBlockingWarning(StringUtils.getGenericError());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            ViewUtils.showBlockingWarning("Verifica di aver inserito correttamente i dati.");
        }
    }

    private boolean areUseFieldsValid() {
        return useNameField.getText().length() > 0 && useNameField.getText().length() <= USE_NAME_LIMIT &&
                Checks.isBigDecimal(useEstimateField.getText());
    }

    @FXML
    private void doEditUse() {
        final TipologieUso selectedUse = useTable.getSelectionModel().getSelectedItem();
        if (selectedUse != null) {
            if (areUseFieldsValid()) {
                try (Connection conn = dataSource().getConnection())  {
                    final DSLContext ctx = Queries.createContext(conn);
                    final int result = Queries.updateUse(
                            selectedUse.getCoduso(),
                            useNameField.getText(),
                            new BigDecimal(useEstimateField.getText()),
                            discountApplicable.isSelected() ? (byte) 1 : (byte) 0,
                            ctx);
                    if (result == 1) {
                        ViewUtils.showBlockingWarning("Tipologia d'uso aggiornata.");
                        refreshUseTable();
                    } else {
                        ViewUtils.showBlockingWarning(StringUtils.getGenericError());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                ViewUtils.showBlockingWarning("Verifica di aver inserito correttamente i dati.");
            }
        } else {
            ViewUtils.showBlockingWarning("Seleziona una tipologia d'uso.");
        }
    }

    @FXML
    private void doDeleteUse() {
        final TipologieUso selectedUse = useTable.getSelectionModel().getSelectedItem();
        if (selectedUse != null) {
            try (Connection conn = dataSource().getConnection())  {
                final DSLContext ctx = Queries.createContext(conn);
                final int result = Queries.deleteGeneric(TIPOLOGIE_USO, TIPOLOGIE_USO.CODUSO, selectedUse.getCoduso(), ctx);
                if (result == 1) {
                    ViewUtils.showBlockingWarning("Tipologia d'uso eliminata.");
                    refreshUseTable();
                } else {
                    ViewUtils.showBlockingWarning(StringUtils.getGenericError());
                }
            } catch (DataAccessException e1) {
                ViewUtils.showBlockingWarning("L'uso è associato a richieste e/o compatibilità. Impossibile eliminare.");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            ViewUtils.showBlockingWarning("Seleziona una tipologia d'uso.");
        }
    }

    private void initCompatibilityTable() {
        Platform.runLater(() -> {
            compatUseCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().value2()));
            compatPlanCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().value4()));
        });
    }

    private void refreshCompatibilityTable() {
        try (Connection conn = dataSource().getConnection()) {
            final DSLContext ctx = Queries.createContext(conn);
            final var compats = Queries.fetchCompatibilities(ctx);

            Platform.runLater(() -> compatibilityTable.setItems(FXCollections.observableList(compats)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void doAddCompatibility() {
        final Offerte selectedPlan = planTable.getSelectionModel().getSelectedItem();
        final TipologieUso selectedUse = useTable.getSelectionModel().getSelectedItem();

        if (selectedPlan != null && selectedUse != null) {
            try (Connection conn = dataSource().getConnection()) {
                final DSLContext ctx = Queries.createContext(conn);
                final int result = Queries.insertCompatibility(selectedUse.getCoduso(), selectedPlan.getCodofferta(), ctx);
                if (result == 1) {
                    ViewUtils.showBlockingWarning("Compatibilità aggiunta.");
                    refreshCompatibilityTable();
                } else {
                    ViewUtils.showBlockingWarning(StringUtils.getGenericError());
                }
            } catch (DataAccessException e1) {
                ViewUtils.showBlockingWarning("Probabile duplicato.");
            }catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            ViewUtils.showBlockingWarning("Seleziona un'offerta ed una tipologia d'uso.");
        }
    }

    @FXML
    private void doDeleteCompatibility() {
        final Record4<Integer, String, Integer, String> selectedComp = compatibilityTable.getSelectionModel().getSelectedItem();
        if (selectedComp != null) {
            try (Connection conn = dataSource().getConnection()) {
                final DSLContext ctx = Queries.createContext(conn);
                final int result = Queries.deleteCompatibility(selectedComp.component1(), selectedComp.component3(), ctx);
                if (result == 1) {
                    ViewUtils.showBlockingWarning("Compatibilità eliminata.");
                    refreshCompatibilityTable();
                } else {
                    ViewUtils.showBlockingWarning(StringUtils.getGenericError());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void goBack() {
        switchTo(AreaSelectorController.create(stage(), dataSource(), getSessionHolder()));
    }
}
