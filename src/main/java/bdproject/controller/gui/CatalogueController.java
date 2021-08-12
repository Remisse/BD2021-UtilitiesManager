package bdproject.controller.gui;

import bdproject.model.SessionHolder;
import bdproject.model.SubscriptionProcess;
import bdproject.model.SubscriptionProcessImpl;
import bdproject.tables.pojos.Offerte;
import bdproject.utils.FXUtils;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import javax.sql.DataSource;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import static bdproject.Tables.*;

public class CatalogueController extends AbstractViewController implements Initializable {

    private static final String fxml = "catalogue.fxml";

    @FXML
    private Button back;
    @FXML
    private Button activate;
    @FXML
    private ComboBox<String> uses;
    @FXML
    private ComboBox<String> utilities;
    @FXML
    private TableView<Offerte> table;
    @FXML
    private TableColumn<Offerte, String> nameColumn;
    @FXML
    private TableColumn<Offerte, String> descColumn;
    @FXML
    private TableColumn<Offerte, String> costColumn;

    private SubscriptionProcess process;
    private final Map<String, String> measurementUnit = Map.of(
            "Gas", "€/Smc",
            "Acqua", "€/mc"
    );

    private CatalogueController(Stage stage, DataSource dataSource) {
        super(stage, dataSource, fxml);
    }

    public static ViewController create(final Stage stage, final DataSource dataSource) {
        return new CatalogueController(stage, dataSource);
    }

    @FXML
    private void backToHome(ActionEvent e) {
        switchTo(HomeController.create(getStage(), getDataSource()));
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializePlanTable();
    }

    private void initializePlanTable() {
        nameColumn.setCellValueFactory(new PropertyValueFactory<Offerte, String>("nome"));
        descColumn.setCellValueFactory(new PropertyValueFactory<Offerte, String>("descrizione"));
        costColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getCostomateriaprima()
                        + " "
                        + measurementUnit.get(cellData.getValue().getMateriaprima()))
        );
        try (Connection conn = getDataSource().getConnection()) {
            populateComboBoxes(conn);
            populatePlanTable(conn);
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage());
            alert.showAndWait();
        }
    }

    private void populatePlanTable(Connection conn) {
        DSLContext query = DSL.using(conn, SQLDialect.MYSQL);

        var plans = query.select(OFFERTE.asterisk())
                .from(OFFERTE, COMPATIBILITÀ)
                .where(OFFERTE.MATERIAPRIMA.eq(utilities.getValue()))
                .and(OFFERTE.ATTIVA.eq((byte) 1))
                .and(COMPATIBILITÀ.TIPOUSO.eq(uses.getValue()))
                .and(OFFERTE.CODICE.eq(COMPATIBILITÀ.CODICEOFFERTA))
                .fetchInto(Offerte.class);

        table.setItems(FXCollections.observableList(plans));
    }

    private void populateComboBoxes(Connection conn) {
        DSLContext create = DSL.using(conn, SQLDialect.MYSQL);

        var utilityRecords = create.select(MATERIE_PRIME.NOME)
                .from(MATERIE_PRIME)
                .orderBy(MATERIE_PRIME.NOME.desc())
                .fetch();
        utilities.setItems(FXCollections.observableList(utilityRecords.getValues(MATERIE_PRIME.NOME)));
        utilities.setValue(utilityRecords.getValue(0, MATERIE_PRIME.NOME));

        var useRecords = create.select(TIPOLOGIE_USO.NOME)
                .from(TIPOLOGIE_USO)
                .fetch();
        uses.setItems(FXCollections.observableList(useRecords.getValues(TIPOLOGIE_USO.NOME)));
        uses.setValue(useRecords.getValue(0, TIPOLOGIE_USO.NOME));
    }

    @FXML
    private void triggerPopulate(ActionEvent e) {
        try (Connection conn = getDataSource().getConnection()) {
            populatePlanTable(conn);
        } catch (SQLException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR, ex.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    private void startSubscriptionProcess(ActionEvent e) {
        if (table.getSelectionModel().getSelectedItem() == null) {
            FXUtils.showBlockingWarning("Non hai selezionato un'offerta.");
        } else if (SessionHolder.get().isEmpty()) {
            FXUtils.showBlockingWarning("Per poter sottoscrivere un'offerta, devi prima effettuare l'accesso.");
        } else {
            if (process == null) {
                process = new SubscriptionProcessImpl();
            }
            process.setClientId(SessionHolder.get().orElseThrow().getUserId());
            process.setPlan(table.getSelectionModel().getSelectedItem());
            process.setUse(uses.getValue());
            switchTo(ParametersSelectionController.create(getStage(), getDataSource(), process));
        }
    }
}
