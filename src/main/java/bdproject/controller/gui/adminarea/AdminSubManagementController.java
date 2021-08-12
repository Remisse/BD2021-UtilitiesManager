package bdproject.controller.gui.adminarea;

import bdproject.controller.Choice;
import bdproject.controller.gui.AbstractViewController;
import bdproject.controller.gui.ViewController;
import bdproject.model.Queries;
import bdproject.tables.pojos.*;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.jooq.Record;
import org.jooq.Result;

import javax.sql.DataSource;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Period;
import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static bdproject.Tables.CONTRATTI;

public class AdminSubManagementController extends AbstractViewController implements Initializable {

    private static final String FXML_FILE = "adminSubManagement.fxml";
    private final Map<String, BiPredicate<Contratti, Connection>> statuses = Map.of(
            "Attivo", (s, c) -> true,
            "Interrotto", (s, c) -> s.getDatacessazione() == null && Queries.hasOngoingInterruption(s, c),
            "Cessato", (s, c) -> s.getDatacessazione() != null
    );
    private List<Contratti> subsList = Collections.emptyList();
    @FXML
    private TableView<Contratti> subsTable;
    @FXML
    private TableColumn<Contratti, Integer> clientIdCol;
    @FXML
    private TableColumn<Contratti, Integer> subIdCol;
    @FXML
    private TableColumn<Contratti, String> statusCol;
    @FXML
    private TableColumn<Contratti, String> zoneCol;
    @FXML
    private TableColumn<Contratti, Integer> planIdCol;
    @FXML
    private TextField clientIdFilter;
    @FXML
    private ComboBox<String> statusFilter;
    @FXML
    private CheckBox lastReportFilter;
    @FXML
    private CheckBox pastDeadlineFilter;
    @FXML
    private TableView<Contratti> requestTable;
    @FXML
    private TableView<Letture> measurementsTable;
    @FXML
    private TableView<Bollette> reportsTable;
    @FXML
    private Button back;
    @FXML
    private Button interruptButton;
    @FXML
    private Button reviewRequest;
    @FXML
    private Button publishReport;


    private AdminSubManagementController(Stage stage, DataSource dataSource) {
        super(stage, dataSource, FXML_FILE);
    }

    public static ViewController create(final Stage stage, final DataSource dataSource) {
        return new AdminSubManagementController(stage, dataSource);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        populateStatusFilter();
    }

    private void populateStatusFilter() {
        statusFilter.setItems(FXCollections.observableList(List.copyOf(statuses.keySet())));
        statusFilter.setValue(statusFilter.getItems().get(0));
        initSubsTable();
        updateSubsTable();
    }

    private void initSubsTable() {
        try (Connection conn = getDataSource().getConnection()) {
            clientIdCol.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getCodicecliente()).asObject());
            subIdCol.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getIdcontratto()).asObject());
            zoneCol.setCellValueFactory(c -> {
                final Zone zone = Queries.getZone(c.getValue(), conn);
                return new SimpleStringProperty(zone.getComune() + " (" + zone.getProvincia() + ")");
            });
            planIdCol.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getCodiceofferta()).asObject());
            statusCol.setCellValueFactory(c -> {
                final Contratti sub = c.getValue();
                String outVal = "";
                for (var entry : statuses.entrySet()) {
                    if (entry.getValue().test(sub, conn)) {
                        outVal = entry.getKey();
                    }
                }
                return new SimpleStringProperty(outVal);
            });
            subsTable.setItems(FXCollections.observableList(subsList));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void updateSubsTable() {
        try (Connection conn = getDataSource().getConnection()) {
            final Map<Contratti, Bollette> subs = Queries.getAllSubscriptionsWithLastReport(conn);
            System.out.println(subs);
            final String statusChoice = statusFilter.getValue();
            final String clientId = clientIdFilter.getText();
            subsList = subs.entrySet()
                    .stream()
                    .filter(e -> e.getKey().getDatainizio() != null)
                    .filter(e -> statuses.get(statusChoice).test(e.getKey(), conn))
                    .filter(e -> clientId.equals("") || e.getKey().getCodicecliente().equals(Integer.parseInt(clientId)))
                    .filter(e -> !lastReportFilter.isSelected()
                            || e.getValue() == null
                            || e.getValue().getDataemissione().isBefore(LocalDate.now().minus(Period.ofMonths(2))))
                    .filter(e -> !pastDeadlineFilter.isSelected()
                            || (e.getValue() != null && e.getValue().getDatascadenza().isBefore(LocalDate.now().minus(Period.ofDays(14)))))
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println(e.getErrorCode());
            System.out.println(e.getSQLState());
        }
    }

    @FXML
    private void goBack() {
        switchTo(AdminChooseAreaController.create(getStage(), getDataSource()));
    }
}
