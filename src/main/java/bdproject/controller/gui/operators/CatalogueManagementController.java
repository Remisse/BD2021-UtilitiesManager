package bdproject.controller.gui.operators;

import bdproject.controller.Checks;
import bdproject.controller.gui.AbstractViewController;
import bdproject.controller.gui.ViewController;
import bdproject.model.Queries;
import bdproject.tables.pojos.MateriePrime;
import bdproject.tables.pojos.Offerte;
import bdproject.utils.FXUtils;
import bdproject.utils.LocaleUtils;
import bdproject.view.StringUtils;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.exception.DataAccessException;
import org.jooq.impl.DSL;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Connection;
import java.text.DecimalFormat;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import static bdproject.tables.MateriePrime.MATERIE_PRIME;
import static bdproject.tables.Offerte.OFFERTE;

public class CatalogueManagementController extends AbstractViewController implements Initializable {

    private static final String FXML_FILE = "catalogueManagement.fxml";
    private final Map<String, String> mUnit = LocaleUtils.getItUtilitiesUnits();

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

    private CatalogueManagementController(Stage stage, DataSource dataSource) {
        super(stage, dataSource, FXML_FILE);
    }

    public static ViewController create(final Stage stage, final DataSource dataSource) {
        return new CatalogueManagementController(stage, dataSource);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try (Connection conn = getDataSource().getConnection()) {
            final DSLContext ctx = DSL.using(conn, SQLDialect.MYSQL);
            utilityBox.setItems(FXCollections.observableList(
                    Queries.fetchAll(ctx, MATERIE_PRIME, MateriePrime.class)
                            .stream()
                            .map(MateriePrime::getNome)
                            .collect(Collectors.toList())));
        } catch (Exception e) {
            e.printStackTrace();
        }
        descriptionArea.setWrapText(true);
        initTable();
        refreshTable();
    }

    private void initTable() {
        nameCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNome()));
        idCol.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getCodice()).asObject());
        descriptionCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDescrizione()));
        utilityCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getMateriaprima()));
        costCol.setCellValueFactory(c -> new SimpleStringProperty(
                DecimalFormat.getInstance().format(c.getValue().getCostomateriaprima()) +
                        mUnit.get(c.getValue().getMateriaprima())));
        activeCol.setCellValueFactory(c -> new SimpleStringProperty(StringUtils.byteToYesNo(c.getValue().getAttiva())));
    }

    private void refreshTable() {
        try (Connection conn = getDataSource().getConnection()) {
            final DSLContext ctx = DSL.using(conn, SQLDialect.MYSQL);
            planTable.setItems(FXCollections.observableList(Queries.fetchAll(ctx, OFFERTE, Offerte.class)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean areFieldsValid() {
        return nameField.getText().length() > 0 &&
                descriptionArea.getText().length() > 0 &&
                Checks.isBigDecimal(costField.getText());
    }

    @FXML
    private void doAdd() {
        if (areFieldsValid()) {
            try (Connection conn = getDataSource().getConnection()) {
                final DSLContext ctx = DSL.using(conn, SQLDialect.MYSQL);
                final int result = Queries.insertPlan(
                        nameField.getText(),
                        descriptionArea.getText(),
                        utilityBox.getValue(),
                        new BigDecimal(costField.getText()),
                        activeYes.isSelected(),
                        ctx
                        );
                if (result == 1) {
                    FXUtils.showBlockingWarning("Offerta inserita.");
                    refreshTable();
                } else {
                    FXUtils.showBlockingWarning(StringUtils.getGenericError());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            FXUtils.showBlockingWarning("Controlla i dati immessi.");
        }
    }

    @FXML
    private void doEdit() {
        final Offerte selectedPlan = planTable.getSelectionModel().getSelectedItem();
        if (selectedPlan != null && areFieldsValid()) {
            FXUtils.showConfirmationDialog("Verranno modificati nome, descrizione e stato di attivazione. Vuoi continuare?", () -> {
                try (Connection conn = getDataSource().getConnection()) {
                    final DSLContext ctx = DSL.using(conn, SQLDialect.MYSQL);
                    final int result = Queries.updatePlan(
                            selectedPlan.getCodice(),
                            nameField.getText(),
                            descriptionArea.getText(),
                            activeYes.isSelected(),
                            ctx
                    );
                    if (result == 1) {
                        FXUtils.showBlockingWarning("Offerta aggiornata.");
                        refreshTable();
                    } else {
                        FXUtils.showBlockingWarning(StringUtils.getGenericError());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } else {
            if (selectedPlan == null) {
                FXUtils.showBlockingWarning("Seleziona un'offerta da modificare.");
            } else {
                FXUtils.showBlockingWarning("Controlla i dati immessi.");
            }
        }
    }

    @FXML
    private void doDelete() {
        final Offerte selectedPlan = planTable.getSelectionModel().getSelectedItem();
        if (selectedPlan != null) {
            try (Connection conn = getDataSource().getConnection()) {
                final DSLContext ctx = DSL.using(conn, SQLDialect.MYSQL);
                final int result = Queries.deleteGeneric(OFFERTE, OFFERTE.CODICE, selectedPlan.getCodice(), ctx);
                if (result == 1) {
                    FXUtils.showBlockingWarning("Offerta eliminata.");
                    refreshTable();
                } else {
                    FXUtils.showBlockingWarning(StringUtils.getGenericError());
                }
            } catch (DataAccessException e1) {
                FXUtils.showBlockingWarning("L'offerta è associata a richieste e/o contratti. Impossibile eliminare.");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            FXUtils.showBlockingWarning("Seleziona un'offerta da eliminare.");
        }
    }

    @FXML
    private void doPasteIntoFields() {
        final Offerte selectedPlan = planTable.getSelectionModel().getSelectedItem();
        if (selectedPlan != null) {
            nameField.setText(selectedPlan.getNome());
            descriptionArea.setText(selectedPlan.getDescrizione());
            utilityBox.setValue(selectedPlan.getMateriaprima());
            costField.setText(selectedPlan.getCostomateriaprima().toString());
            if (selectedPlan.getAttiva() == 1) {
                activeYes.setSelected(true);
            } else {
                activeNo.setSelected(true);
            }
        }
    }

    @FXML
    private void goBack() {
        switchTo(AreaSelectorController.create(getStage(), getDataSource()));
    }
}
