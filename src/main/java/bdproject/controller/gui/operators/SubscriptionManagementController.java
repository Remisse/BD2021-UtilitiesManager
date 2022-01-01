package bdproject.controller.gui.operators;

import bdproject.controller.Checks;
import bdproject.controller.gui.AbstractController;
import bdproject.controller.gui.Controller;
import bdproject.model.types.StatusType;
import bdproject.model.Queries;
import bdproject.model.SessionHolder;
import bdproject.tables.pojos.*;
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
import org.jooq.impl.DSL;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;

import static bdproject.tables.TipologieUso.TIPOLOGIE_USO;

public class SubscriptionManagementController extends AbstractController implements Initializable {

    private static final String FXML_FILE = "adminSubManagement.fxml";
    private static final int DEADLINE_DAYS = 14;
    private static final int REPORT_PERIOD_MONTHS = 2;
    private static final int NO_CLIENT = -1;

    private List<ContrattiApprovati> subsList = Collections.emptyList();
    private List<TipologieUso> useTypes;
    private final Map<String, BiPredicate<ContrattiApprovati, Connection>> statuses = Map.of(
            "Attivo", (s, c) -> Checks.isSubscriptionActive(s),
            "Cessato", (s, c) -> s.getDatacessazione() != null
    );
    private byte[] reportFile = null;

    private final DateTimeFormatter dateIt = LocaleUtils.getItDateFormatter();

    @FXML private Button back;
    @FXML private TableView<ContrattiApprovati> subsTable;
    @FXML private TableColumn<ContrattiApprovati, Integer> clientIdCol;
    @FXML private TableColumn<ContrattiApprovati, Integer> subIdCol;
    @FXML private TableColumn<ContrattiApprovati, String> zoneCol;
    @FXML private TableColumn<ContrattiApprovati, Integer> planIdCol;
    @FXML private TableColumn<ContrattiApprovati, String> incomeDiscountCol;
    @FXML private TextField clientIdFilter;
    @FXML private ComboBox<String> statusFilter;
    @FXML private CheckBox lastReportFilter;
    @FXML private CheckBox pastDeadlineFilter;

    @FXML private TableView<Letture> measurementsTable;
    @FXML private TableColumn<Letture, String> measPublishDateCol;
    @FXML private TableColumn<Letture, String> measConsumptionCol;
    @FXML private TableColumn<Letture, String> measConfirmedCol;
    @FXML private Button confirmMeasurement;

    @FXML private TableView<Bollette> reportsTable;
    @FXML private TableColumn<Bollette, String> repPublishDateCol;
    @FXML private TableColumn<Bollette, String> repDeadlineCol;
    @FXML private TableColumn<Bollette, String> repPaidDateCol;
    @FXML private TableColumn<Bollette, String> repCostCol;
    @FXML private TableColumn<Bollette, String> repEstimatedCol;

    @FXML private DatePicker intervalStartDatePicker;
    @FXML private DatePicker intervalEndDatePicker;
    @FXML private CheckBox estimatedCheckbox;
    @FXML private TextField finalCostField;
    @FXML private TextField consumptionField;
    @FXML private Label reportFileStatus;

    private SubscriptionManagementController(final Stage stage, final DataSource dataSource, final SessionHolder holder) {
        super(stage, dataSource, holder, FXML_FILE);
    }

    public static Controller create(final Stage stage, final DataSource dataSource, final SessionHolder holder) {
        return new SubscriptionManagementController(stage, dataSource, holder);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try (Connection conn = dataSource().getConnection()) {
            final DSLContext ctx = DSL.using(conn, SQLDialect.MYSQL);
            useTypes = Queries.fetchAll(ctx, TIPOLOGIE_USO, TipologieUso.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        populateStatusFilter();
        initSubsTable();
        initMeasurementsTable();
        initReportsTable();
        refreshAll();
    }

    private void populateStatusFilter() {
        final List<String> choices = statuses.keySet()
                .stream()
                .sorted()
                .collect(Collectors.toList());
        statusFilter.setItems(FXCollections.observableList(choices));
        statusFilter.setValue(statusFilter.getItems().get(0));
    }

    private void initSubsTable() {
        clientIdCol.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getIdcliente()).asObject());
        subIdCol.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getIdcontratto()).asObject());
        planIdCol.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getOfferta()).asObject());
        incomeDiscountCol.setCellValueFactory(c -> {
            final boolean hasDiscount = useTypes
                    .stream()
                    .filter(u -> u.getCoduso().equals(c.getValue().getUso()))
                    .anyMatch(u -> u.getScontoreddito() == 1);
            return new SimpleStringProperty(hasDiscount ? "Sì" : "No");
        });

        zoneCol.setCellValueFactory(c -> {
            final Immobili estate = Queries.fetchPremiseFromSubscription(c.getValue().getIdcontratto(), dataSource());
            return new SimpleStringProperty(estate.getComune() + " (" + estate.getProvincia() + ")");
        });
        subsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldS, newS) -> {
            if (newS != null && oldS != newS) {
                doRefreshMeasurements();
                doRefreshReports();
            }
        });
    }

    @FXML
    private void doRefreshSubs() {
        final int clientId = clientIdFilter.getText().equals("") ? NO_CLIENT : Integer.parseInt(clientIdFilter.getText());
        final String statusChoice = statusFilter.getValue();

        try (Connection conn = dataSource().getConnection()) {
            Map<ContrattiApprovati, Bollette> subs = Queries.fetchAllSubscriptionsWithLastReport(conn);

            subsList = subs.entrySet()
                    .stream()
                    .filter(p -> statuses.get(statusChoice).test(p.getKey(), conn))
                    .filter(p -> clientId == NO_CLIENT || p.getKey().getIdcliente().equals(clientId))
                    .filter(p -> !lastReportFilter.isSelected()
                            || (p.getValue().getDataemissione().isBefore(LocalDate.now().minus(Period.ofMonths(
                                        REPORT_PERIOD_MONTHS)))))
                    .filter(p -> !pastDeadlineFilter.isSelected() ||
                            !Queries.allReportsPaid(p.getKey().getIdcontratto(), conn))
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
        }
        subsTable.setItems(FXCollections.observableList(subsList));
    }

    @FXML
    private void showSubDetails() {
        final ContrattiApprovati selected = subsTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            createSubWindow(OperatorSubDetailsController.create(null, dataSource(), getSessionHolder(), selected));
        } else {
            FXUtils.showBlockingWarning("Seleziona un contratto.");
        }
    }

    private void initMeasurementsTable() {
        measPublishDateCol.setCellValueFactory(c -> new SimpleStringProperty(dateIt.format(c.getValue().getDataeffettuazione())));
        measConsumptionCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getConsumi().toString()));
        measConfirmedCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStato()));
    }

    private void doRefreshMeasurements() {
        final ContrattiApprovati sub = subsTable.getSelectionModel().getSelectedItem();
        if (sub != null) {
            try (Connection conn = dataSource().getConnection()) {
                final List<Letture> measurements = Queries.fetchMeasurements(sub.getIdcontratto(), conn);
                measurementsTable.setItems(FXCollections.observableList(measurements));
            } catch (SQLException e) {
                e.printStackTrace();
                FXUtils.showError("Impossibile aggiornare la tabella delle letture.");
            }
        } else {
            measurementsTable.setItems(FXCollections.emptyObservableList());
        }
    }

    @FXML
    private void doConfirmMeasurement() {
        updateMeasurement(StatusType.APPROVED);
    }

    @FXML
    private void doRejectMeasurement() {
        updateMeasurement(StatusType.REJECTED);
    }

    private void updateMeasurement(final StatusType newStatus) {
        final Letture selected = measurementsTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            try (Connection conn = dataSource().getConnection()) {
                final int result = Queries.setMeasurementStatus(selected.getNumerolettura(), newStatus.toString(), conn);
                if (result != 0) {
                    FXUtils.showBlockingWarning("Lettura aggiornata.");
                } else {
                    FXUtils.showBlockingWarning("Lettura già aggiornata.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                FXUtils.showBlockingWarning(StringUtils.getGenericError());
            }
        } else {
            FXUtils.showBlockingWarning("Seleziona una lettura.");
        }
    }

    private void initReportsTable() {
        repPublishDateCol.setCellValueFactory(c -> new SimpleStringProperty(dateIt.format(c.getValue().getDataemissione())));
        repDeadlineCol.setCellValueFactory(c -> new SimpleStringProperty(dateIt.format(c.getValue().getDatascadenza())));

        repPaidDateCol.setCellValueFactory(c -> {
            SimpleStringProperty outVal = new SimpleStringProperty("");
            try (final Connection conn = dataSource().getConnection()) {
                Optional<Pagamenti> payment = Queries.fetchReportPayment(c.getValue().getNumerobolletta(), conn);
                if (payment.isPresent()) {
                    outVal = new SimpleStringProperty(dateIt.format(payment.get().getDatapagamento()));
                }
            } catch (SQLException e) {
                FXUtils.showError(e.getMessage());
            }
            return outVal;
        });

        repCostCol.setCellValueFactory(c -> new SimpleStringProperty("€ " + c.getValue().getImporto()));
        repEstimatedCol.setCellValueFactory(c -> new SimpleStringProperty(
                StringUtils.byteToYesNo(c.getValue().getStimata())));
    }

    private void doRefreshReports() {
        final ContrattiApprovati sub = subsTable.getSelectionModel().getSelectedItem();
        if (sub != null) {
            try (Connection conn = dataSource().getConnection()) {
                final var reports = Queries.fetchSubscriptionReports(sub, conn);
                reportsTable.setItems(FXCollections.observableList(reports));
            } catch (SQLException e) {
                e.printStackTrace();
                FXUtils.showBlockingWarning("Impossibile aggiornare la tabella delle bollette.");
            }
        } else {
            reportsTable.setItems(FXCollections.emptyObservableList());
        }
    }

    /**
     * Mock implementation.
     */
    @FXML
    private void doDownloadFile() {
        FXUtils.showBlockingWarning("File scaricato.");
    }

    @FXML
    private void doDeleteReport() {
        final Bollette report = reportsTable.getSelectionModel().getSelectedItem();
        if (report != null) {
            try (Connection conn = dataSource().getConnection()) {
                final int result = Queries.deleteReport(report.getNumerobolletta(), conn);
                if (result != 0) {
                    FXUtils.showBlockingWarning("Bolletta eliminata.");
                } else {
                    FXUtils.showBlockingWarning("Impossibile eliminare la bolletta.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                FXUtils.showBlockingWarning("Seleziona una bolletta.");
            }
        }
    }

    /**
     * Mock implementation.
     */
    @FXML
    private void loadReportFile() {
        reportFile = new byte[] {'T', 'E', 'S', 'T'};
        reportFileStatus.setText("caricato");
    }

    @FXML
    private void doPublishReport() {
        if (canPublishReport()) {
            final ContrattiApprovati sub = subsTable.getSelectionModel().getSelectedItem();
            if (sub != null) {
                try (final Connection conn = dataSource().getConnection()) {
                    final int result = Queries.publishReport(
                            intervalStartDatePicker.getValue(),
                            intervalEndDatePicker.getValue(),
                            DEADLINE_DAYS,
                            new BigDecimal(finalCostField.getText()),
                            new BigDecimal(consumptionField.getText()),
                            reportFile,
                            (byte) (estimatedCheckbox.isSelected() ? 1 : 0),
                            getSessionHolder().session().orElseThrow().userId(),
                            sub.getIdcontratto(),
                            conn);
                    if (result != 0) {
                        FXUtils.showBlockingWarning("Bolletta emessa.");
                        clearReportData();
                        doRefreshReports();
                    } else {
                        FXUtils.showBlockingWarning("Impossibile emettere la bolletta.");
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    FXUtils.showBlockingWarning(StringUtils.getGenericError());
                }
            } else {
                FXUtils.showBlockingWarning("Seleziona il contratto per il quale vuoi emettere una bolletta.");
            }
        } else {
            FXUtils.showBlockingWarning("Controlla che i dati inseriti siano corretti e che il file di dettaglio sia" +
                    "stato caricato.");
        }
    }

    private void clearReportData() {
        reportFile = null;
        finalCostField.setText("");
    }

    private boolean canPublishReport() {
        return Checks.isBigDecimal(finalCostField.getText()) &&
                Checks.isValidConsumption(consumptionField.getText()) &&
                intervalStartDatePicker.getValue().isBefore(intervalEndDatePicker.getValue()) &&
                reportFile != null;
    }

    private void refreshAll() {
        doRefreshSubs();
        doRefreshMeasurements();
        doRefreshReports();
    }

    @FXML
    private void goBack() {
        switchTo(AreaSelectorController.create(stage(), dataSource(), getSessionHolder()));
    }
}
