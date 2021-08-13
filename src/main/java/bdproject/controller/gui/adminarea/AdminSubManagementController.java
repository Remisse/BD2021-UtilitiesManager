package bdproject.controller.gui.adminarea;

import bdproject.controller.gui.AbstractViewController;
import bdproject.controller.gui.ViewController;
import bdproject.model.Queries;
import bdproject.tables.pojos.*;
import bdproject.utils.FXUtils;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

import javax.sql.DataSource;
import java.net.URL;
import java.sql.Connection;
import java.time.LocalDate;
import java.time.Period;
import java.util.*;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;

public class AdminSubManagementController extends AbstractViewController implements Initializable {

    private static final String FXML_FILE = "adminSubManagement.fxml";

    private static final int DEADLINE_DAYS = 14;
    private static final int REPORT_PERIOD_MONTHS = 2;
    private final Map<String, BiPredicate<Contratti, Connection>> statuses = Map.of(
            "Attivo", (s, c) -> true,
            "Interrotto", (s, c) -> s.getDatacessazione() == null && Queries.hasOngoingInterruption(s, c),
            "Cessato", (s, c) -> s.getDatacessazione() != null
    );
    private List<Contratti> subsList = Collections.emptyList();
    @FXML private TableView<Contratti> subsTable;
    @FXML private TableColumn<Contratti, Integer> clientIdCol;
    @FXML private TableColumn<Contratti, Integer> subIdCol;
    @FXML private TableColumn<Contratti, String> zoneCol;
    @FXML private TableColumn<Contratti, Integer> planIdCol;
    @FXML private TextField clientIdFilter;
    @FXML private ComboBox<String> statusFilter;
    @FXML private CheckBox lastReportFilter;
    @FXML private CheckBox pastDeadlineFilter;
    @FXML private TableView<Contratti> requestTable;
    @FXML private TableView<Letture> measurementsTable;
    @FXML private TableView<Bollette> reportsTable;
    @FXML private Button back;
    @FXML private Button interruptButton;
    @FXML private Button reviewRequest;
    @FXML private Button publishReport;


    private AdminSubManagementController(Stage stage, DataSource dataSource) {
        super(stage, dataSource, FXML_FILE);
    }

    public static ViewController create(final Stage stage, final DataSource dataSource) {
        return new AdminSubManagementController(stage, dataSource);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        populateStatusFilter();
        initSubsTable();
        updateSubsTable();
    }

    private void populateStatusFilter() {
        final List<String> choices = statuses.keySet().stream().sorted().collect(Collectors.toList());
        statusFilter.setItems(FXCollections.observableList(choices));
        statusFilter.setValue(statusFilter.getItems().get(0));
    }

    private void initSubsTable() {
        subsTable.setItems(FXCollections.observableList(subsList));

        clientIdCol.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getCodicecliente()).asObject());
        subIdCol.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getIdcontratto()).asObject());
        planIdCol.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getCodiceofferta()).asObject());

        zoneCol.setCellValueFactory(c -> {
            SimpleStringProperty outVal = new SimpleStringProperty("");
            if (c.getValue() != null) {
                final Zone zone = Queries.getZone(c.getValue(), getDataSource()).orElseThrow();
                outVal = new SimpleStringProperty(zone.getComune() + " (" + zone.getProvincia() + ")");
            }
            return outVal;
        });
    }

    @FXML
    private void updateSubsTable() {
        final int clientId = clientIdFilter.getText().equals("") ? -1 : Integer.parseInt(clientIdFilter.getText());
        final String statusChoice = statusFilter.getValue();

        try (Connection conn = getDataSource().getConnection()) {
            Map<Contratti, Bollette> subs = Queries.getAllSubscriptionsAndLastReport(conn);
            subsList = subs.entrySet().stream()
                    .filter(p -> p.getKey().getDatainizio() != null)
                    .filter(p -> statuses.get(statusChoice).test(p.getKey(), conn))
                    .filter(p -> clientId == -1 || p.getKey().getCodicecliente().equals(clientId))
                    .filter(p -> !lastReportFilter.isSelected()
                            || p.getValue() == null
                            || p.getValue().getDataemissione().isBefore(LocalDate.now().minus(Period.ofMonths(REPORT_PERIOD_MONTHS))))
                    .filter(p -> !pastDeadlineFilter.isSelected()
                            || (p.getValue() != null
                                && p.getValue().getDatapagamento() == null
                                && p.getValue().getDatascadenza().isBefore(LocalDate.now().minus(Period.ofDays(DEADLINE_DAYS)))))
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
        }
        subsTable.setItems(FXCollections.observableList(subsList));
    }

    @FXML
    private void goBack() {
        switchTo(AdminChooseAreaController.create(getStage(), getDataSource()));
    }

    @FXML
    private void showSubDetails() {
        final Contratti selected = subsTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            switchTo(AdminSubDetailsController.create(getStage(), getDataSource(), selected));
        } else {
            FXUtils.showBlockingWarning("Seleziona un contratto.");
        }
    }
}
