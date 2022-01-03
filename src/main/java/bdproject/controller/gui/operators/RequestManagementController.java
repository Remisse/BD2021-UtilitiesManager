package bdproject.controller.gui.operators;

import bdproject.controller.gui.AbstractController;
import bdproject.controller.gui.Controller;
import bdproject.controller.gui.admin.AreaSelectorController;
import bdproject.model.types.ActivationType;
import bdproject.model.types.StatusType;
import bdproject.model.Queries;
import bdproject.model.SessionHolder;
import bdproject.tables.pojos.*;
import bdproject.utils.ViewUtils;
import bdproject.utils.LocaleUtils;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import javax.sql.DataSource;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class RequestManagementController extends AbstractController implements Initializable {

    private static final String FXML_FILE = "requestManagement.fxml";
    private static final DateTimeFormatter DATE_IT = LocaleUtils.getItDateFormatter();

    @FXML private TableView<RichiesteContratto> activationRequestTable;
    @FXML private TableColumn<RichiesteContratto, String> activPublishDateCol;
    @FXML private TableColumn<RichiesteContratto, String> activStatusCol;
    @FXML private TableColumn<RichiesteContratto, String> activMethodCol;
    @FXML private TableColumn<RichiesteContratto, String> activMeterCol;
    @FXML private TableColumn<RichiesteContratto, String> activCompletionDateCol;

    @FXML private TableView<Cessazioni> endRequestTable;
    @FXML private TableColumn<Cessazioni, String> endPublishDateCol;
    @FXML private TableColumn<Cessazioni, Integer> endSubscriptionCol;
    @FXML private TableColumn<Cessazioni, String> endStatusCol;
    @FXML private TableColumn<Cessazioni, String> endCompletionDateCol;

    @FXML private TextArea refusalNotes;
    @FXML private TextField meterIdField;

    private RequestManagementController(final Stage stage, final DataSource dataSource, final SessionHolder holder) {
        super(stage, dataSource, holder, FXML_FILE);
    }

    public static Controller create(final Stage stage, final DataSource dataSource, final SessionHolder holder) {
        return new RequestManagementController(stage, dataSource, holder);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initTables();
        refreshTables();
    }

    private void initTables() {
        List<TipiAttivazione> methods = Collections.emptyList();
        try (final Connection conn = dataSource().getConnection()) {
            methods = Queries.fetchActivationMethods(conn);
        } catch (SQLException e) {
            e.printStackTrace();
            ViewUtils.showError(e.getMessage());
        }

        activMeterCol.setCellValueFactory(c -> {
            Optional<Contatori> meter = Optional.empty();

            try (final Connection conn = dataSource().getConnection()) {
                meter = Queries.fetchMeterBySubscription(c.getValue().getIdcontratto(), conn);
            } catch (SQLException e) {
                e.printStackTrace();
                ViewUtils.showError(e.getMessage());
            }

            return meter.map(m -> new SimpleStringProperty(m.getMatricola()))
                    .orElseGet(() -> new SimpleStringProperty("Non installato"));
        });

        List<TipiAttivazione> finalMethods = methods;
        activMethodCol.setCellValueFactory(c -> new SimpleStringProperty(finalMethods.stream()
                .filter(a -> a.getCodattivazione().equals(c.getValue().getTipoattivazione()))
                .findFirst()
                .orElseThrow()
                .getNome()));

        activPublishDateCol.setCellValueFactory(c -> new SimpleStringProperty(LocaleUtils.getItDateFormatter().format(
                c.getValue().getDataaperturarichiesta())));
        activCompletionDateCol.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getDatachiusurarichiesta() == null ? "N.A." : DATE_IT.format(c.getValue()
                        .getDatachiusurarichiesta())));
        activStatusCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStatorichiesta()));

        endPublishDateCol.setCellValueFactory(c -> new SimpleStringProperty(LocaleUtils.getItDateFormatter().format(
                c.getValue().getDataaperturarichiesta())));
        endSubscriptionCol.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getIdcontratto()).asObject());
        endCompletionDateCol.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getDatachiusurarichiesta() == null ? "N.A." : DATE_IT.format(c.getValue()
                        .getDatachiusurarichiesta())));
        endStatusCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStatorichiesta()));

        activationRequestTable.getSelectionModel().selectedItemProperty().addListener((obs, oldItem, newItem) -> {
            if (newItem != null) {
                endRequestTable.getSelectionModel().clearSelection();
            }
        });

        endRequestTable.getSelectionModel().selectedItemProperty().addListener((obs, oldItem, newItem) -> {
            if (newItem != null) {
                activationRequestTable.getSelectionModel().clearSelection();
            }
        });
    }

    private void refreshTables() {
        final int operatorId = getSessionHolder().session().orElseThrow().userId();

        try (Connection conn = dataSource().getConnection()) {
            final List<RichiesteContratto> activRequests =
                    Queries.fetchSubscriptionRequestsAssignedToOperator(operatorId, conn);
            final List<Cessazioni> endRequests = Queries.fetchEndRequestsAssignedToOperator(operatorId, conn);

            activationRequestTable.setItems(FXCollections.observableList(activRequests));
            endRequestTable.setItems(FXCollections.observableList(endRequests));
        } catch (Exception e) {
            e.printStackTrace();
            ViewUtils.showError(e.getMessage());
        }
    }

    @FXML
    private void doShowDetails() {
        final RichiesteContratto activRequest = activationRequestTable.getSelectionModel().getSelectedItem();
        if (activRequest != null) {
            createSubWindow(OperatorActivationRequestDetailsController.create(
                    null, dataSource(), getSessionHolder(), activRequest));
        } else {
            final Cessazioni endRequest = endRequestTable.getSelectionModel().getSelectedItem();
            if (endRequest != null) {
                ContrattiApprovati sub = null;
                try (final Connection conn = dataSource().getConnection()) {
                    sub = Queries.fetchApprovedSubscriptionById(endRequest.getIdcontratto(), conn).orElseThrow();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                createSubWindow(OperatorSubDetailsController.create(stage(), dataSource(), getSessionHolder(), sub));
            }
        }
    }

    @FXML
    private void doAccept() {
        final RichiesteContratto activRequest = activationRequestTable.getSelectionModel().getSelectedItem();
        int result = 0;

        if (activRequest != null) {
            try (Connection conn = dataSource().getConnection()) {
                final Optional<Contatori> meter = Queries.fetchMeterBySubscription(activRequest.getIdcontratto(), conn);
                if (meter.isEmpty()) {
                    ViewUtils.showBlockingWarning("Non è stato ancora aggiunto il contatore.");
                } else {
                    result = Queries.approveSubscriptionRequest(activRequest.getIdcontratto(), conn);
                    if (result == 1) {
                        ViewUtils.showBlockingWarning("Contratto attivato.");
                    } else {
                        ViewUtils.showBlockingWarning("Impossibile attivare il contratto. Verifica che non esista già per" +
                                " questo immobile un contratto attivo relativo alla fornitura della stessa materia prima.");
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            final Cessazioni endRequest = endRequestTable.getSelectionModel().getSelectedItem();
            if (endRequest != null) {
                try (Connection conn = dataSource().getConnection()) {
                    result = Queries.ceaseSubscription(endRequest.getIdcontratto(), conn);
                    if (result == 1) {
                        ViewUtils.showBlockingWarning("Contratto cessato.");
                        result += Queries.markEndRequestAsApproved(endRequest.getNumerorichiesta(),
                                endRequest.getNoterichiesta(), conn);
                        if (result != 2) {
                            ViewUtils.showError("Errore nell'aggiornamento della richiesta.");
                        }
                    } else {
                        ViewUtils.showBlockingWarning("Impossibile cessare il contratto.");
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        refreshTables();
    }

    @FXML
    private void doRefuse() {
        final RichiesteContratto activRequest = activationRequestTable.getSelectionModel().getSelectedItem();
        int result = 0;

        if (activRequest != null) {
            try (Connection conn = dataSource().getConnection()) {
                result = Queries.markSubscriptionRequestAsRejected(activRequest.getIdcontratto(),
                        activRequest.getNoterichiesta(), conn);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            final Cessazioni endRequest = endRequestTable.getSelectionModel().getSelectedItem();
            if (endRequest != null) {
                try (Connection conn = dataSource().getConnection()) {
                    result = Queries.markEndRequestAsRejected(endRequest.getNumerorichiesta(),
                            endRequest.getNoterichiesta(), conn);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        if (result != 0) {
            ViewUtils.showBlockingWarning("Richiesta aggiornata");
            refreshTables();
        } else {
            ViewUtils.showBlockingWarning("Impossibile aggiornare la richiesta");
        }
    }

    @FXML
    private void doSetMeter() {
        final RichiesteContratto activRequest = activationRequestTable.getSelectionModel().getSelectedItem();

        if (activRequest != null) {
            if (activRequest.getStatorichiesta().equals(StatusType.REVIEWING.toString())) {
                if (meterIdField.getText().length() > 0) {
                    try (Connection conn = dataSource().getConnection()) {
                        final String utility = Queries.fetchUtilityFromSubscription(activRequest.getIdcontratto(), conn)
                                        .getNome();

                        final int result =
                                Queries.insertMeter(meterIdField.getText(), utility, activRequest.getIdimmobile(), conn);
                        if (result == 1) {
                            ViewUtils.showBlockingWarning("Matricola inserita.");
                            refreshTables();
                        } else {
                            ViewUtils.showBlockingWarning("Impossibile inserire la matricola.");
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                } else {
                    ViewUtils.showBlockingWarning("Verifica che la matricola sia stata scritta correttamente.");
                }
            } else {
                ViewUtils.showBlockingWarning("La richiesta è già stata finalizzata.");
            }
        } else {
            ViewUtils.showBlockingWarning("Seleziona una richiesta di attivazione.");
        }
    }

    @FXML
    private void doUpdateNotes() {
        final RichiesteContratto activRequest = activationRequestTable.getSelectionModel().getSelectedItem();
        int result = 0;

        if (activRequest != null) {
            try (Connection conn = dataSource().getConnection()) {
                result = Queries.updateSubscriptionRequestNotes(activRequest.getIdcontratto(), refusalNotes.getText(), conn);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            final Cessazioni endRequest = endRequestTable.getSelectionModel().getSelectedItem();
            if (endRequest != null) {
                try (Connection conn = dataSource().getConnection()) {
                    result = Queries.updateEndRequestNotes(endRequest.getNumerorichiesta(), refusalNotes.getText(), conn);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        if (result == 1) {
            ViewUtils.showBlockingWarning("Note aggiornate.");
            refreshTables();
        } else {
            ViewUtils.showBlockingWarning("Impossibile aggiornare le note.");
        }
    }

    @FXML
    private void goBack() {
        switchTo(AreaSelectorController.create(stage(), dataSource(), getSessionHolder()));
    }
}
