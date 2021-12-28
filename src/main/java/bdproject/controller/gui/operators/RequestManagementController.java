package bdproject.controller.gui.operators;

import bdproject.controller.gui.AbstractController;
import bdproject.controller.gui.Controller;
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
import java.util.ResourceBundle;

import static bdproject.tables.Cessazioni.CESSAZIONI;
import static bdproject.tables.Contatori.CONTATORI;
import static bdproject.tables.Contratti.CONTRATTI;
import static bdproject.tables.TipiAttivazione.TIPI_ATTIVAZIONE;

public class RequestManagementController extends AbstractController implements Initializable {

    private static final String FXML_FILE = "requestManagement.fxml";

    @FXML private TableView<Contratti> activationRequestTable;
    @FXML private TableColumn<Contratti, String> activPublishDateCol;
    @FXML private TableColumn<Contratti, Integer> activClientCol;
    @FXML private TableColumn<Contratti, String> activStatusCol;
    @FXML private TableColumn<Contratti, String> activMethodCol;
    @FXML private TableColumn<Contratti, String> activMeterCol;
    @FXML private TableColumn<Contratti, String> activOperatorCol;

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
        } catch (Exception e) {
            e.printStackTrace();
        }

        activMeterCol.setCellValueFactory(c -> {
            final Contatori meter = Queries.fetchByKey(
                    CONTATORI, CONTATORI.MATRICOLA, c.getValue().getContatore(), Contatori.class, dataSource()).orElseThrow();
            return new SimpleStringProperty(meter.getMatricola() == null ? "Non inserito" : meter.getMatricola());
        });

        activPublishDateCol.setCellValueFactory(c -> new SimpleStringProperty(LocaleUtils.getItDateFormatter().format(
                c.getValue().getDatarichiesta())));
        activClientCol.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getCliente()).asObject());
        activStatusCol.setCellValueFactory(
                c -> new SimpleStringProperty(StringUtils.requestStatusToString(c.getValue().getStato())));
        activOperatorCol.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getOperatore() == null ? "-" : c.getValue().getOperatore().toString()));

        endPublishDateCol.setCellValueFactory(c -> new SimpleStringProperty(LocaleUtils.getItDateFormatter().format(
                c.getValue().getDatarichiesta())));
        endSubscriptionCol.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getIdcontratto()).asObject());
        endNotesCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNote()));
        endStatusCol.setCellValueFactory(c -> new SimpleStringProperty(StringUtils.requestStatusToString(c.getValue().getStato())));
        endOperatorCol.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getOperatore() == null ? "-" : c.getValue().getOperatore().toString()));

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
            final DSLContext ctx = DSL.using(conn, SQLDialect.MYSQL);
            final List<Contratti> activRequests = Queries.fetchAll(ctx, CONTRATTI, Contratti.class);
            final List<Cessazioni> endRequests = Queries.fetchAll(ctx, CESSAZIONI, Cessazioni.class);

            activationRequestTable.setItems(FXCollections.observableList(activRequests));
            endRequestTable.setItems(FXCollections.observableList(endRequests));
        } catch (Exception e) {
            e.printStackTrace();
            FXUtils.showError(e.getMessage());
        }
    }

    @FXML
    private void doShowDetails() {
        final Contratti activRequest = activationRequestTable.getSelectionModel().getSelectedItem();
        if (activRequest != null) {
            createSubWindow(OperatorActivationRequestDetailsController.create(
                    null, dataSource(), sessionHolder(), activRequest));
        } else {
            final Cessazioni endRequest = endRequestTable.getSelectionModel().getSelectedItem();
            if (endRequest != null) {
                Contratti sub = null;
                try (Connection conn = dataSource().getConnection()) {
                    sub = Queries.fetchSubscriptionFromId(endRequest.getIdcontratto(), conn).orElseThrow();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                createSubWindow(OperatorSubDetailsController.create(stage(), dataSource(), sessionHolder(), sub));
            }
        }
    }

    @FXML
    private void doAccept() {
        final int operatorId = sessionHolder().session().orElseThrow().userId();
        final Contratti activRequest = activationRequestTable.getSelectionModel().getSelectedItem();
        int result = 0;

        if (activRequest != null) {
            try (Connection conn = dataSource().getConnection()) {
                Queries.updateOneFieldWhere(CONTRATTI, CONTRATTI.IDCONTRATTO,
                        activRequest.getIdcontratto(), RICHIESTE_ATTIVAZIONE.OPERATORE, operatorId, conn);
                final Contatori meter = Queries.fetchByKey(CONTATORI, CONTATORI.PROGRESSIVO, activRequest.getContatore(),
                                Contatori.class, dataSource())
                        .orElseThrow();
                if (meter.getMatricola() == null) {
                    FXUtils.showBlockingWarning("Non è stata ancora inserita la matricola del contatore.");
                } else {
                    result = Queries.updateSubscriptionRequest(activRequest.getNumero(),
                            activRequest.getContatore(), conn);
                    if (result == 1) {
                        FXUtils.showBlockingWarning("Contratto creato.");
                        result += Queries.setRequestStatus(RICHIESTE_ATTIVAZIONE, activRequest.getNumero(), "A", conn);
                    } else {
                        FXUtils.showBlockingWarning("Impossibile creare il contratto.");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            final RichiesteCessazione endRequest = endRequestTable.getSelectionModel().getSelectedItem();
            if (endRequest != null) {
                try (Connection conn = dataSource().getConnection()) {
                    Queries.updateOneFieldWhere(RICHIESTE_CESSAZIONE, RICHIESTE_CESSAZIONE.NUMERO,
                            endRequest.getNumero(), RICHIESTE_CESSAZIONE.OPERATORE, operatorId, conn);
                    result = Queries.ceaseSubscription(endRequest.getIdcontratto(), conn);
                    if (result == 1) {
                        FXUtils.showBlockingWarning("Contratto cessato.");
                        result += Queries.setRequestStatus(RICHIESTE_CESSAZIONE, endRequest.getNumero(), "A", conn);
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
        final int operatorId = sessionHolder().session().orElseThrow().userId();
        final RichiesteAttivazione activRequest = activationRequestTable.getSelectionModel().getSelectedItem();
        int result = 0;

        if (activRequest != null) {
            try (Connection conn = dataSource().getConnection()) {
                Queries.updateOneFieldWhere(RICHIESTE_ATTIVAZIONE, RICHIESTE_ATTIVAZIONE.NUMERO,
                        activRequest.getNumero(), RICHIESTE_ATTIVAZIONE.OPERATORE, operatorId, conn);
                result = Queries.setRequestStatus(RICHIESTE_ATTIVAZIONE, activRequest.getNumero(), "R", conn);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            final RichiesteCessazione endRequest = endRequestTable.getSelectionModel().getSelectedItem();
            if (endRequest != null) {
                try (Connection conn = dataSource().getConnection()) {
                    Queries.updateOneFieldWhere(RICHIESTE_CESSAZIONE, RICHIESTE_CESSAZIONE.NUMERO,
                            endRequest.getNumero(), RICHIESTE_CESSAZIONE.OPERATORE, operatorId, conn);
                    result = Queries.setRequestStatus(RICHIESTE_CESSAZIONE, endRequest.getNumero(), "R", conn);
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
        final int operatorId = sessionHolder().session().orElseThrow().userId();
        final RichiesteAttivazione activRequest = activationRequestTable.getSelectionModel().getSelectedItem();

        if (activRequest != null) {
            if (activRequest.getStato().equals("N") || activRequest.getStato().equals("E")) {
                if (meterIdField.getText().length() > 0) {
                    try (Connection conn = dataSource().getConnection()) {
                        final DSLContext ctx = DSL.using(conn, SQLDialect.MYSQL);
                        Queries.updateOneFieldWhere(RICHIESTE_ATTIVAZIONE, RICHIESTE_ATTIVAZIONE.NUMERO,
                                activRequest.getNumero(), RICHIESTE_ATTIVAZIONE.OPERATORE, operatorId, conn);
                        final int result =
                                Queries.updateMeterId(activRequest.getContatore(), meterIdField.getText(), ctx);
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
        final RichiesteAttivazione activRequest = activationRequestTable.getSelectionModel().getSelectedItem();
        int result = 0;
        if (activRequest != null) {
            try (Connection conn = dataSource().getConnection()) {
                result = Queries.setRequestNotes(RICHIESTE_ATTIVAZIONE, activRequest.getNumero(), refusalNotes.getText(), conn);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            final RichiesteCessazione endRequest = endRequestTable.getSelectionModel().getSelectedItem();
            if (endRequest != null) {
                try (Connection conn = dataSource().getConnection()) {
                    result = Queries.setRequestNotes(RICHIESTE_CESSAZIONE, endRequest.getNumero(), refusalNotes.getText(), conn);
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
        switchTo(AreaSelectorController.create(stage(), dataSource(), sessionHolder()));
    }
}
