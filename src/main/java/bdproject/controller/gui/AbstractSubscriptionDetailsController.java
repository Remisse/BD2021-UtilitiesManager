package bdproject.controller.gui;

import bdproject.controller.Checks;
import bdproject.model.Queries;
import bdproject.tables.pojos.*;
import bdproject.utils.LocaleUtils;
import bdproject.view.StringUtils;
import bdproject.utils.FXUtils;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

import javax.sql.DataSource;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

public abstract class AbstractSubscriptionDetailsController extends AbstractViewController implements Initializable {

    private static final String FXML_FILE = "subDetails.fxml";
    private static final String FLOW_CSS = "-fx-font: 16 arial";

    private final ContrattiDettagliati detailedSub;
    private final DateTimeFormatter dateIt = LocaleUtils.getItDateFormatter();

    @FXML private Button back;
    @FXML private TextFlow clientDetails;
    @FXML private TextFlow planDetails;
    @FXML private TextFlow premisesDetails;

    @FXML private Label peopleNoName;
    @FXML private Label peopleNo;
    @FXML private Label subStartDate;
    @FXML private Label subEndDate;
    @FXML private Label subState;
    @FXML private Label use;
    @FXML private Label activation;

    @FXML private TableView<Interruzioni> interruptionTable;
    @FXML private TableColumn<Interruzioni, String> interruptionDate;
    @FXML private TableColumn<Interruzioni, String> reactivationDate;
    @FXML private TableColumn<Interruzioni, String> interruptionDescription;

    @FXML private TableView<RichiesteCessazione> endRequestTable;
    @FXML private TableColumn<RichiesteCessazione, String> reqPublishDateCol;
    @FXML private TableColumn<RichiesteCessazione, String> reqResultCol;
    @FXML private TableColumn<RichiesteCessazione, String> reqNotesCol;

    protected AbstractSubscriptionDetailsController(final Stage stage, final DataSource dataSource,
            final ContrattiDettagliati detailedSub) {
        super(stage, dataSource, FXML_FILE);
        this.detailedSub = detailedSub;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try (Connection conn = getDataSource().getConnection()) {
            final LocalDate startDate = detailedSub.getDatainizio();
            final LocalDate endDate = detailedSub.getDatacessazione();

            subStartDate.setText(startDate != null ? dateIt.format(startDate) : "N.D.");
            subEndDate.setText(endDate != null ? dateIt.format(endDate) : "N.D.");
            use.setText(Queries.fetchUsageFromSub(detailedSub.getIdcontratto(), conn).getNome());
            activation.setText(Queries.fetchActivationFromSub(detailedSub.getIdcontratto(), conn).getNome());

            setClientDetails(conn);
            setPlanDetails();
            setPeopleNo();
            setStatus(endDate, conn);
            setPremisesDetails();
            setInterruptionTable(conn);
            setEndRequestTable();
            refreshEndRequestTable();
            setOther();
        } catch (SQLException e) {
            e.printStackTrace();
            FXUtils.showError(e.getSQLState() + "\n" + e.getMessage());
        }
    }

    protected abstract void setOther();

    protected ContrattiDettagliati getSubscription() {
        return detailedSub;
    }

    private void setClientDetails(final Connection conn) {
        clientDetails.getChildren().add(new Text(StringUtils.clientToString(detailedSub.getCliente(), conn)));
    }

    private void setInterruptionTable(Connection conn) {
        final List<Interruzioni> interruptions = Queries.fetchInterruptions(detailedSub.getIdcontratto(), conn);
        interruptionTable.setItems(FXCollections.observableList(interruptions));
        interruptionDate.setCellValueFactory(c -> new SimpleStringProperty(dateIt.format(c.getValue().getDatainterruzione())));
        reactivationDate.setCellValueFactory(c -> new SimpleStringProperty(dateIt.format(c.getValue().getDatariattivazione())));
        interruptionDescription.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getMotivazione()));
    }

    private void setPremisesDetails() {
        final Immobili premises = Queries.fetchPremisesFromMeter(detailedSub.getContatore(), getDataSource());
        final Text premisesText = new Text(StringUtils.premisesToString(premises));
        premisesText.setStyle(FLOW_CSS);
        premisesDetails.getChildren().add(premisesText);
    }

    private void setPeopleNo() {
        try (Connection conn = getDataSource().getConnection()) {
            final TipologieUso use = Queries.fetchUsageFromSub(detailedSub.getIdcontratto(), conn);
            if (Checks.requiresPeopleNumber(use)) {
                peopleNoName.setText(use.getNome().equals("Commerciale")
                                     ? "Numero dipendenti:"
                                     : "Componenti nucleo familiare:");
                peopleNo.setText(detailedSub.getNumerocomponenti().toString());
            } else {
                peopleNoName.setVisible(false);
                peopleNo.setVisible(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setPlanDetails() {
        final Offerte plan = Queries.fetchPlanById(detailedSub.getOfferta(), getDataSource()).orElseThrow();
        final Text planText = new Text(StringUtils.planToString(plan));
        planText.setStyle(FLOW_CSS);
        planDetails.getChildren().add(planText);
    }

    protected void setStatus(LocalDate endDate, final Connection conn) {
        subState.setText(endDate != null ? "Cessato"
                         : Checks.isSubscriptionActive(detailedSub, conn) ? "Attivo"
                         : "Interrotto");
    }

    private void setEndRequestTable() {
        reqPublishDateCol.setCellValueFactory(c -> new SimpleStringProperty(dateIt.format(c.getValue().getDatarichiesta())));
        reqResultCol.setCellValueFactory(c -> new SimpleStringProperty(StringUtils.requestStatusToString(c.getValue().getStato())));
        reqNotesCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNote()));
    }

    protected void refreshEndRequestTable() {
        List<RichiesteCessazione> requests = Collections.emptyList();
        try (Connection conn = getDataSource().getConnection()) {
            requests = Queries.fetchEndRequestsFor(detailedSub.getIdcontratto(), conn);
        } catch (Exception e) {
            e.printStackTrace();
        }
        endRequestTable.setItems(FXCollections.observableList(requests));
    }

    @FXML
    private void goBack() {
        switchTo(getBackController());
    }

    @FXML
    private void doInsertEndRequest() {
        abstractDoInsertEndRequest();
    }

    @FXML
    private void doDeleteEndRequest() {
        abstractDoDeleteEndRequest();
    }

    protected abstract void abstractDoDeleteEndRequest();

    protected abstract void abstractDoInsertEndRequest();

    protected abstract ViewController getBackController();
}
