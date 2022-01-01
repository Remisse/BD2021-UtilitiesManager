package bdproject.controller.gui.operators;

import bdproject.controller.gui.AbstractController;
import bdproject.controller.gui.Controller;
import bdproject.model.types.StatusType;
import bdproject.model.Queries;
import bdproject.model.SessionHolder;
import bdproject.tables.pojos.*;
import bdproject.utils.FXUtils;
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
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import javax.sql.DataSource;
import java.net.URL;
import java.sql.Connection;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import static bdproject.tables.TipiAttivazione.TIPI_ATTIVAZIONE;

public class RequestManagementController extends AbstractController implements Initializable {

    private static final String FXML_FILE = "requestManagement.fxml";

    @FXML private TableView<RichiesteContratto> activationRequestTable;
    @FXML private TableColumn<RichiesteContratto, String> activPublishDateCol;
    @FXML private TableColumn<RichiesteContratto, Integer> activClientCol;
    @FXML private TableColumn<RichiesteContratto, String> activStatusCol;
    @FXML private TableColumn<RichiesteContratto, String> activMethodCol;
    @FXML private TableColumn<RichiesteContratto, String> activMeterCol;
    @FXML private TableColumn<RichiesteContratto, String> activOperatorCol;

    @FXML private TableView<Cessazioni> endRequestTable;
    @FXML private TableColumn<Cessazioni, String> endPublishDateCol;
    @FXML private TableColumn<Cessazioni, Integer> endSubscriptionCol;
    @FXML private TableColumn<Cessazioni, String> endStatusCol;
    @FXML private TableColumn<Cessazioni, String> endNotesCol;
    @FXML private TableColumn<Cessazioni, String> endOperatorCol;

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
        try (Connection conn = dataSource().getConnection()) {
            DSLContext ctx = DSL.using(conn, SQLDialect.MYSQL);

            List<TipiAttivazione> methods = Queries.fetchAll(ctx, TIPI_ATTIVAZIONE, TipiAttivazione.class);
            activMethodCol.setCellValueFactory(c -> new SimpleStringProperty(methods.stream()
                    .filter(a -> a.getCodattivazione().equals(c.getValue().getTipoattivazione()))
                    .findFirst()
                    .orElseThrow()
                    .getNome()));

            activMeterCol.setCellValueFactory(c -> {
                final Optional<Contatori> meter = Queries.fetchMeterBySubscription(c.getValue().getIdcontratto(), conn);
                return meter.map(m -> new SimpleStringProperty(m.getMatricola()))
                        .orElseGet(() -> new SimpleStringProperty("Non inserito"));
            });

            activOperatorCol.setCellValueFactory(c -> {
                final Optional<OperatoriContratti> assignment =
                        Queries.fetchSubscriptionRequestAssignment(c.getValue().getIdcontratto(), conn);
                return assignment.map(a -> new SimpleStringProperty(a.getIdoperatore().toString()))
                        .orElseGet(() -> new SimpleStringProperty("N.A."));
            });

            endOperatorCol.setCellValueFactory(c -> {
                final Optional<OperatoriCessazioni> assignment =
                        Queries.fetchEndRequestAssignment(c.getValue().getIdcontratto(), conn);
                return assignment.map(a -> new SimpleStringProperty(a.getIdoperatore().toString()))
                        .orElseGet(() -> new SimpleStringProperty("N.A."));
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        activPublishDateCol.setCellValueFactory(c -> new SimpleStringProperty(LocaleUtils.getItDateFormatter().format(
                c.getValue().getDataaperturarichiesta())));
        activClientCol.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getIdcliente()).asObject());
        activStatusCol.setCellValueFactory(
                c -> new SimpleStringProperty(c.getValue().getStatorichiesta()));

        endPublishDateCol.setCellValueFactory(c -> new SimpleStringProperty(LocaleUtils.getItDateFormatter().format(
                c.getValue().getDataaperturarichiesta())));
        endSubscriptionCol.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getIdcontratto()).asObject());
        endNotesCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNoterichiesta()));
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
        try (Connection conn = dataSource().getConnection()) {
            final int operatorId = getSessionHolder().session().orElseThrow().userId();
            final List<RichiesteContratto> activRequests = Queries.fetchSubscriptionRequestsAssignedToOperator(operatorId, conn);
            final List<Cessazioni> endRequests = Queries.fetchEndRequestsAssignedToOperator(operatorId, conn);

            activationRequestTable.setItems(FXCollections.observableList(activRequests));
            endRequestTable.setItems(FXCollections.observableList(endRequests));
        } catch (Exception e) {
            e.printStackTrace();
            FXUtils.showError(e.getMessage());
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
                } catch (Exception e) {
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
                    FXUtils.showBlockingWarning("Non è stato ancora aggiunto il contatore.");
                } else {
                    result = Queries.activateSubscription(activRequest.getIdcontratto(), conn);
                    if (result == 1) {
                        FXUtils.showBlockingWarning("Contratto creato.");
                    } else {
                        FXUtils.showBlockingWarning("Impossibile creare il contratto.");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            final Cessazioni endRequest = endRequestTable.getSelectionModel().getSelectedItem();
            if (endRequest != null) {
                try (Connection conn = dataSource().getConnection()) {
                    result = Queries.ceaseSubscription(endRequest.getIdcontratto(), conn);
                    if (result == 1) {
                        FXUtils.showBlockingWarning("Contratto cessato.");
                        result += Queries.updateEndRequest(endRequest.getNumerorichiesta(), StatusType.APPROVED.toString(),
                                endRequest.getNoterichiesta(), conn);
                        if (result != 2) {
                            FXUtils.showError("Errore nell'aggiornamento della richiesta.");
                        }
                    } else {
                        FXUtils.showBlockingWarning("Impossibile cessare il contratto.");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        if (result == 2) {
            FXUtils.showBlockingWarning("Richiesta aggiornata");
            refreshTables();
        } else {
            FXUtils.showBlockingWarning("Impossibile aggiornare la richiesta");
        }
    }

    @FXML
    private void doRefuse() {
        final RichiesteContratto activRequest = activationRequestTable.getSelectionModel().getSelectedItem();
        int result = 0;

        if (activRequest != null) {
            try (Connection conn = dataSource().getConnection()) {
                result = Queries.updateSubscriptionRequest(activRequest.getIdcontratto(), StatusType.REJECTED.toString(),
                        activRequest.getNoterichiesta(), conn);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            final Cessazioni endRequest = endRequestTable.getSelectionModel().getSelectedItem();
            if (endRequest != null) {
                try (Connection conn = dataSource().getConnection()) {
                    result = Queries.updateEndRequest(endRequest.getNumerorichiesta(), StatusType.REJECTED.toString(),
                            endRequest.getNoterichiesta(), conn);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        if (result != 0) {
            FXUtils.showBlockingWarning("Richiesta aggiornata");
            refreshTables();
        } else {
            FXUtils.showBlockingWarning("Impossibile aggiornare la richiesta");
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
                            FXUtils.showBlockingWarning("Matricola inserita.");
                            refreshTables();
                        } else {
                            FXUtils.showBlockingWarning("Impossibile inserire la matricola.");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    FXUtils.showBlockingWarning("Verifica che la matricola sia stata scritta correttamente.");
                }
            } else {
                FXUtils.showBlockingWarning("La richiesta è già stata finalizzata.");
            }
        } else {
            FXUtils.showBlockingWarning("Seleziona una richiesta di attivazione.");
        }
    }

    @FXML
    private void doUpdateNotes() {
        final RichiesteContratto activRequest = activationRequestTable.getSelectionModel().getSelectedItem();
        int result = 0;

        if (activRequest != null) {
            try (Connection conn = dataSource().getConnection()) {
                result = Queries.updateSubscriptionRequest(activRequest.getIdcontratto(), activRequest.getStatorichiesta(),
                        refusalNotes.getText(), conn);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            final Cessazioni endRequest = endRequestTable.getSelectionModel().getSelectedItem();
            if (endRequest != null) {
                try (Connection conn = dataSource().getConnection()) {
                    result = Queries.updateEndRequest(endRequest.getNumerorichiesta(), endRequest.getStatorichiesta(),
                            refusalNotes.getText(), conn);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        if (result == 1) {
            FXUtils.showBlockingWarning("Note aggiornate.");
            refreshTables();
        } else {
            FXUtils.showBlockingWarning("Impossibile aggiornare le note.");
        }
    }

    @FXML
    private void goBack() {
        switchTo(AreaSelectorController.create(stage(), dataSource(), getSessionHolder()));
    }
}
