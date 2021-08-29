package bdproject.controller.gui;

import bdproject.controller.Choice;
import bdproject.controller.ChoiceImpl;
import bdproject.model.Queries;
import bdproject.model.SessionHolder;
import bdproject.model.SubscriptionProcess;
import bdproject.model.SubscriptionProcessImpl;
import bdproject.tables.pojos.Offerte;
import bdproject.tables.pojos.TipologieUso;
import bdproject.utils.FXUtils;
import bdproject.utils.LocaleUtils;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import javax.sql.DataSource;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import static bdproject.Tables.*;

public class CatalogueController extends AbstractViewController implements Initializable {

    private static final String FXML_FILE = "catalogue.fxml";
    private SubscriptionProcess process;
    private final Map<String, String> measurementUnit = LocaleUtils.getItUtilitiesUnits();

    @FXML private Button back;
    @FXML private Button activate;
    @FXML private ComboBox<Choice<TipologieUso, String>> uses;
    @FXML private ComboBox<String> utilities;
    @FXML private TableView<Offerte> table;
    @FXML private TableColumn<Offerte, String> nameColumn;
    @FXML private TableColumn<Offerte, String> descColumn;
    @FXML private TableColumn<Offerte, String> costColumn;

    private CatalogueController(Stage stage, DataSource dataSource) {
        super(stage, dataSource, FXML_FILE);
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
        nameColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNome()));
        descColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDescrizione()));
        costColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getCostomateriaprima()
                        + " "
                        + measurementUnit.get(cellData.getValue().getMateriaprima()))
        );
        try (Connection conn = getDataSource().getConnection()) {
            populateComboBoxes(conn);
            populatePlanTable(conn);
        } catch (SQLException e) {
            FXUtils.showError(e.getMessage());
        }
    }

    private void populatePlanTable(Connection conn) {
        DSLContext query = DSL.using(conn, SQLDialect.MYSQL);

        var plans = query.select(OFFERTE.asterisk())
                .from(OFFERTE, COMPATIBILITÀ)
                .where(OFFERTE.MATERIAPRIMA.eq(utilities.getValue()))
                .and(OFFERTE.ATTIVA.eq((byte) 1))
                .and(COMPATIBILITÀ.USO.eq(uses.getValue().getItem().getCodice()))
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

        List<Choice<TipologieUso, String>> useRecords = Queries.fetchAllUsages(conn)
                .stream()
                .map(t -> new ChoiceImpl<>(t, t.getNome(), (i, n) -> n))
                .collect(Collectors.toList());
        uses.setItems(FXCollections.observableList(useRecords));
        uses.setValue(useRecords.get(0));
    }

    @FXML
    private void triggerPopulate(ActionEvent e) {
        try (Connection conn = getDataSource().getConnection()) {
            populatePlanTable(conn);
        } catch (SQLException ex) {
            FXUtils.showError(ex.getMessage());
        }
    }

    @FXML
    private void startSubscriptionProcess(ActionEvent e) {
        if (table.getSelectionModel().getSelectedItem() == null) {
            FXUtils.showBlockingWarning("Non hai selezionato un'offerta.");
        } else if (SessionHolder.getSession().isEmpty()) {
            FXUtils.showBlockingWarning("Per poter richiedere l'attivazione di un'offerta, devi prima effettuare l'accesso.");
        } else {
            if (process == null) {
                process = new SubscriptionProcessImpl();
            }
            process.setClientId(SessionHolder.getSession().orElseThrow().getUserId());
            process.setPlan(table.getSelectionModel().getSelectedItem());
            process.setUse(uses.getValue().getItem());
            switchTo(ParametersSelectionController.create(getStage(), getDataSource(), process));
        }
    }
}
