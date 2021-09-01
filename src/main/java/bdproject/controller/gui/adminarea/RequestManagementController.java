package bdproject.controller.gui.adminarea;

import bdproject.controller.gui.AbstractViewController;
import bdproject.controller.gui.ViewController;
import bdproject.model.Queries;
import bdproject.tables.pojos.ContrattiDettagliati;
import bdproject.tables.pojos.RichiesteAttivazione;
import bdproject.tables.pojos.RichiesteCessazione;
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
import javafx.stage.Stage;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import javax.sql.DataSource;
import java.net.URL;
import java.sql.Connection;
import java.util.List;
import java.util.ResourceBundle;

import static bdproject.tables.RichiesteAttivazione.RICHIESTE_ATTIVAZIONE;
import static bdproject.tables.RichiesteCessazione.RICHIESTE_CESSAZIONE;

public class RequestManagementController extends AbstractViewController implements Initializable {

    private static final String FXML_FILE = "requestManagement.fxml";

    @FXML private TableView<RichiesteAttivazione> activationRequestTable;
    @FXML private TableColumn<RichiesteAttivazione, String> activPublishDateCol;
    @FXML private TableColumn<RichiesteAttivazione, Integer> activClientCol;
    @FXML private TableColumn<RichiesteAttivazione, String> activStatusCol;
    @FXML private TableColumn<RichiesteAttivazione, String> activNotesCol;

    @FXML private TableView<RichiesteCessazione> endRequestTable;
    @FXML private TableColumn<RichiesteCessazione, String> endPublishDateCol;
    @FXML private TableColumn<RichiesteCessazione, Integer> endSubscriptionCol;
    @FXML private TableColumn<RichiesteCessazione, String> endStatusCol;
    @FXML private TableColumn<RichiesteCessazione, String> endNotesCol;

    @FXML private TextArea refusalNotes;

    private RequestManagementController(Stage stage, DataSource dataSource) {
        super(stage, dataSource, FXML_FILE);
    }

    public static ViewController create(final Stage stage, final DataSource dataSource) {
        return new RequestManagementController(stage, dataSource);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initTables();
        refreshTables();
    }

    private void initTables() {
        activPublishDateCol.setCellValueFactory(c -> new SimpleStringProperty(LocaleUtils.getItDateFormatter().format(
                c.getValue().getDatarichiesta())));
        activClientCol.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getCliente()).asObject());
        activNotesCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNote()));
        activStatusCol.setCellValueFactory(c -> new SimpleStringProperty(StringUtils.requestStatusToString(c.getValue().getStato())));

        endPublishDateCol.setCellValueFactory(c -> new SimpleStringProperty(LocaleUtils.getItDateFormatter().format(
                c.getValue().getDatarichiesta())));
        endSubscriptionCol.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getIdcontratto()).asObject());
        endNotesCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNote()));
        endStatusCol.setCellValueFactory(c -> new SimpleStringProperty(StringUtils.requestStatusToString(c.getValue().getStato())));

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
        try (Connection conn = getDataSource().getConnection()) {
            final DSLContext ctx = DSL.using(conn, SQLDialect.MYSQL);
            final List<RichiesteAttivazione> activRequests = Queries.fetchAll(ctx, RICHIESTE_ATTIVAZIONE, RichiesteAttivazione.class);
            final List<RichiesteCessazione> endRequests = Queries.fetchAll(ctx, RICHIESTE_CESSAZIONE, RichiesteCessazione.class);

            activationRequestTable.setItems(FXCollections.observableList(activRequests));
            endRequestTable.setItems(FXCollections.observableList(endRequests));
        } catch (Exception e) {
            e.printStackTrace();
            FXUtils.showError(e.getMessage());
        }
    }

    @FXML
    private void doShowDetails() {
        final RichiesteAttivazione activRequest = activationRequestTable.getSelectionModel().getSelectedItem();
        if (activRequest != null) {
            createSubWindow(AdminActivationRequestDetailsController.create(null, getDataSource(), activRequest));
        } else {
            final RichiesteCessazione endRequest = endRequestTable.getSelectionModel().getSelectedItem();
            if (endRequest != null) {
                ContrattiDettagliati sub = null;
                try (Connection conn = getDataSource().getConnection()) {
                    sub = Queries.fetchSubscriptionFromId(endRequest.getIdcontratto(), conn).orElseThrow();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                createSubWindow(AdminSubDetailsController.create(getStage(), getDataSource(), sub));
            }
        }
    }

    private void updateSelectedRequestStatus(final String status) {
        final RichiesteAttivazione activRequest = activationRequestTable.getSelectionModel().getSelectedItem();
        int result = 0;
        if (activRequest != null) {
            try (Connection conn = getDataSource().getConnection()) {
                result = Queries.setRequestStatus(RICHIESTE_ATTIVAZIONE, activRequest.getNumero(), status, conn);
                if (status.equals("A")) {
                    Queries.createSubscriptionFromRequest(activRequest.getNumero(), conn);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            final RichiesteCessazione endRequest = endRequestTable.getSelectionModel().getSelectedItem();
            if (endRequest != null) {
                try (Connection conn = getDataSource().getConnection()) {
                    result = Queries.setRequestStatus(RICHIESTE_CESSAZIONE, endRequest.getNumero(), status, conn);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        if (result == 1) {
            FXUtils.showBlockingWarning("Richiesta aggiornata.");
            refreshTables();
        } else {
            FXUtils.showBlockingWarning(StringUtils.getGenericError());
        }
    }

    @FXML
    private void doSetInReview() {
        updateSelectedRequestStatus("E");
    }

    @FXML
    private void doAccept() {
        updateSelectedRequestStatus("A");
    }

    @FXML
    private void doRefuse() {
        updateSelectedRequestStatus("R");
    }

    @FXML
    private void doUpdateNotes() {
        final RichiesteAttivazione activRequest = activationRequestTable.getSelectionModel().getSelectedItem();
        int result = 0;
        if (activRequest != null) {
            try (Connection conn = getDataSource().getConnection()) {
                result = Queries.setRequestNotes(RICHIESTE_ATTIVAZIONE, activRequest.getNumero(), refusalNotes.getText(), conn);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            final RichiesteCessazione endRequest = endRequestTable.getSelectionModel().getSelectedItem();
            if (endRequest != null) {
                try (Connection conn = getDataSource().getConnection()) {
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
        switchTo(AdminChooseAreaController.create(getStage(), getDataSource()));
    }
}
