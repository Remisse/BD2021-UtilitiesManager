package bdproject.controller.gui.adminarea;

import bdproject.controller.Checks;
import bdproject.controller.gui.AbstractViewController;
import bdproject.controller.gui.ViewController;
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

public class AdminSubManagementController extends AbstractViewController implements Initializable {

    private static final String FXML_FILE = "adminSubManagement.fxml";
    private static final int DEADLINE_DAYS = 14;
    private static final int REPORT_PERIOD_MONTHS = 2;
    private static final int NO_CLIENT = -1;

    private List<ContrattiDettagliati> subsList = Collections.emptyList();
    private List<TipologieUso> useTypes;
    private final Map<String, BiPredicate<ContrattiDettagliati, Connection>> statuses = Map.of(
            "Attivo", (s, c) -> s.getDatacessazione() == null,
            "Interrotto", (s, c) -> s.getDatacessazione() == null && Queries.hasOngoingInterruption(s, c),
            "Cessato", (s, c) -> s.getDatacessazione() != null
    );
    private byte[] reportFile = null;

    private final DateTimeFormatter dateIt = LocaleUtils.getItDateFormatter();

    @FXML private Button back;
    @FXML private TableView<ContrattiDettagliati> subsTable;
    @FXML private TableColumn<ContrattiDettagliati, Integer> clientIdCol;
    @FXML private TableColumn<ContrattiDettagliati, Integer> subIdCol;
    @FXML private TableColumn<ContrattiDettagliati, String> zoneCol;
    @FXML private TableColumn<ContrattiDettagliati, Integer> planIdCol;
    @FXML private TableColumn<ContrattiDettagliati, String> incomeDiscountCol;
    @FXML private TextField clientIdFilter;
    @FXML private ComboBox<String> statusFilter;
    @FXML private CheckBox lastReportFilter;
    @FXML private CheckBox pastDeadlineFilter;
    @FXML private Button interruptButton;
    @FXML private TextField interruptionDescription;
    @FXML private Button reactivateButton;

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

    @FXML private CheckBox estimatedCheckbox;
    @FXML private TextField finalCostField;
    @FXML private Label reportFileStatus;

    private AdminSubManagementController(Stage stage, DataSource dataSource) {
        super(stage, dataSource, FXML_FILE);
    }

    public static ViewController create(final Stage stage, final DataSource dataSource) {
        return new AdminSubManagementController(stage, dataSource);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try (Connection conn = getDataSource().getConnection()) {
            useTypes = Queries.fetchAllUsages(conn);
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
        clientIdCol.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getCliente()).asObject());
        subIdCol.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getIdcontratto()).asObject());
        planIdCol.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getOfferta()).asObject());
        incomeDiscountCol.setCellValueFactory(c -> {
            final boolean hasDiscount = useTypes
                    .stream()
                    .filter(u -> u.getCodice().equals(c.getValue().getUso()))
                    .anyMatch(u -> u.getScontoreddito() == 1);
            return new SimpleStringProperty(hasDiscount ? "Sì" : "No");
        });

        zoneCol.setCellValueFactory(c -> {
            final Immobili premises = Queries.fetchPremisesFromMeter(c.getValue().getContatore(), getDataSource());
            return new SimpleStringProperty(premises.getComune() + " (" + premises.getProvincia() + ")");
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

        try (Connection conn = getDataSource().getConnection()) {
            Map<ContrattiDettagliati, Bollette> subs = Queries.fetchAllSubscriptionsWithLastReport(conn);
            subsList = subs.entrySet()
                    .stream()
                    .filter(p -> statuses.get(statusChoice).test(p.getKey(), conn))
                    .filter(p -> clientId == NO_CLIENT || p.getKey().getCliente().equals(clientId))
                    .filter(p -> !lastReportFilter.isSelected()
                            || (p.getValue().getDataemissione() != null
                                && p.getValue().getDataemissione().isBefore(LocalDate.now().minus(Period.ofMonths(
                                        REPORT_PERIOD_MONTHS)))))
                    .filter(p -> !pastDeadlineFilter.isSelected()
                            || (p.getValue().getDatascadenza() != null
                                && p.getValue().getDatapagamento() == null
                                && p.getValue().getDatascadenza().isBefore(LocalDate.now().minus(Period.ofDays(
                                        DEADLINE_DAYS)))))
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
        }
        subsTable.setItems(FXCollections.observableList(subsList));
    }

    @FXML
    private void doInterrupt() {
        final ContrattiDettagliati sub = subsTable.getSelectionModel().getSelectedItem();

        if (sub != null) {
            if (!interruptionDescription.getText().equals("")) {
                try (Connection conn = getDataSource().getConnection()) {
                    final int result = Queries.interruptSubscription(
                            sub.getIdcontratto(),
                            interruptionDescription.getText(),
                            conn);
                    if (result != 0) {
                        FXUtils.showBlockingWarning("Contratto interrotto.");
                        interruptionDescription.setText("");
                        refreshAll();
                    } else {
                        FXUtils.showBlockingWarning("Impossibile interrompere il contratto.");
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    FXUtils.showError(e.getMessage());
                }
            } else {
                FXUtils.showBlockingWarning("Inserisci una motivazione.");
            }
        } else {
            FXUtils.showBlockingWarning("Seleziona un contratto.");
        }
    }

    @FXML
    private void doReactivate() {
        final ContrattiDettagliati sub = subsTable.getSelectionModel().getSelectedItem();
        if (sub != null) {
            try (Connection conn = getDataSource().getConnection()) {
                final int result = Queries.reactivateSubscription(sub.getIdcontratto(), conn);
                if (result != 0) {
                    FXUtils.showBlockingWarning("Contratto riattivato.");
                    refreshAll();
                } else {
                    FXUtils.showBlockingWarning("Impossibile riattivare il contratto.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                FXUtils.showError(e.getMessage());
            }
        } else {
            FXUtils.showBlockingWarning("Seleziona un contratto.");
        }
    }

    @FXML
    private void showSubDetails() {
        final ContrattiDettagliati selected = subsTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            createSubWindow(AdminSubDetailsController.create(null, getDataSource(), selected));
        } else {
            FXUtils.showBlockingWarning("Seleziona un contratto.");
        }
    }

    private void initMeasurementsTable() {
        measPublishDateCol.setCellValueFactory(c -> new SimpleStringProperty(dateIt.format(c.getValue().getDataeffettuazione())));
        measConsumptionCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getConsumi().toString()));
        measConfirmedCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getConfermata() == 1 ? "Sì" : "No"));
    }

    private void doRefreshMeasurements() {
        final ContrattiDettagliati sub = subsTable.getSelectionModel().getSelectedItem();
        if (sub != null) {
            try (Connection conn = getDataSource().getConnection()) {
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
        final Letture selectedMeas = measurementsTable.getSelectionModel().getSelectedItem();
        if (selectedMeas != null) {
            try (Connection conn = getDataSource().getConnection()) {
                final int result = Queries.confirmMeasurement(selectedMeas.getContatore(), selectedMeas.getDataeffettuazione(), conn);
                if (result != 0) {
                    FXUtils.showBlockingWarning("Lettura confermata.");
                } else {
                    FXUtils.showBlockingWarning("Lettura già confermata.");
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
        repPaidDateCol.setCellValueFactory(c -> new SimpleStringProperty(dateIt.format(c.getValue().getDatapagamento())));
        repCostCol.setCellValueFactory(c -> new SimpleStringProperty("€ " + c.getValue().getImporto()));
        repEstimatedCol.setCellValueFactory(c -> new SimpleStringProperty(
                StringUtils.byteToYesNo(c.getValue().getStimata())));

    }

    private void doRefreshReports() {
        final ContrattiDettagliati sub = subsTable.getSelectionModel().getSelectedItem();
        if (sub != null) {
            try (Connection conn = getDataSource().getConnection()) {
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
            try (Connection conn = getDataSource().getConnection()) {
                final int result = Queries.deleteReport(report.getIdcontratto(), report.getDataemissione(), conn);
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
            final ContrattiDettagliati sub = subsTable.getSelectionModel().getSelectedItem();
            try (final Connection conn = getDataSource().getConnection()) {
                final int result = Queries.publishReport(
                        sub.getIdcontratto(),
                        1,
                        BigDecimal.valueOf(Long.parseLong(finalCostField.getText())),
                        reportFile,
                        (byte) (estimatedCheckbox.isSelected() ? 1 : 0),
                        conn);
                if (result != 0) {
                    FXUtils.showBlockingWarning("Bolletta emessa.");
                    clearReportData();
                } else {
                    FXUtils.showBlockingWarning("Impossibile emettere la bolletta.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                FXUtils.showBlockingWarning(StringUtils.getGenericError());
            }
        }
    }

    private void clearReportData() {
        reportFile = null;
        finalCostField.setText("");
    }

    private boolean canPublishReport() {
        return Checks.isNumber(finalCostField.toString()) && reportFile != null;
    }

    private void refreshAll() {
        doRefreshSubs();
        doRefreshMeasurements();
        doRefreshReports();
    }

    @FXML
    private void goBack() {
        switchTo(AdminChooseAreaController.create(getStage(), getDataSource()));
    }
}
