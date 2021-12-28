package bdproject.controller.gui;

import bdproject.controller.Checks;
import bdproject.model.Queries;
import bdproject.model.SessionHolder;
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
import java.util.Optional;
import java.util.ResourceBundle;

public abstract class AbstractSubscriptionDetailsController extends AbstractController implements Initializable {

    private static final String FXML_FILE = "subDetails.fxml";
    private static final String FLOW_CSS = "-fx-font: 16 arial";

    private final Contratti sub;
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

    @FXML private TableView<Cessazioni> endRequestTable;
    @FXML private TableColumn<Cessazioni, String> reqPublishDateCol;
    @FXML private TableColumn<Cessazioni, String> reqResultCol;
    @FXML private TableColumn<Cessazioni, String> reqNotesCol;

    protected AbstractSubscriptionDetailsController(final Stage stage, final DataSource dataSource, final SessionHolder holder,
            final Contratti sub) {
        super(stage, dataSource, holder, FXML_FILE);
        this.sub = sub;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try (Connection conn = dataSource().getConnection()) {
            final LocalDate startDate = sub.getDatachiusurarichiesta();
            final LocalDate endDate = sub.getDatacessazione();

            subStartDate.setText(startDate != null ? dateIt.format(startDate) : "N.D.");
            subEndDate.setText(endDate != null ? dateIt.format(endDate) : "N.D.");
            use.setText(Queries.fetchUsageFromSub(sub.getIdcontratto(), conn).getNome());
            activation.setText(Queries.fetchActivationFromSub(sub.getIdcontratto(), conn).getNome());

            setClientDetails(conn);
            setPlanDetails();
            setPeopleNo();
            setStatus();
            setPremisesDetails();
            setEndRequestTable();
            refreshEndRequestTable();
            setOther();
        } catch (SQLException e) {
            e.printStackTrace();
            FXUtils.showError(e.getSQLState() + "\n" + e.getMessage());
        }
    }

    protected abstract void setOther();

    protected Contratti getSubscription() {
        return sub;
    }

    private void setClientDetails(final Connection conn) {
        final Optional<ClientiDettagliati> client = Queries.fetchClientById(sub.getIdcliente(), conn);

        client.ifPresent(c -> clientDetails.getChildren()
                .add(new Text(StringUtils.clientToString(c))));
    }

    private void setPremisesDetails() {
        final Immobili premise = Queries.fetchPremiseFromSubscription(sub.getIdcontratto(), dataSource());
        final Text premiseText = new Text(StringUtils.premiseToString(premise));

        premiseText.setStyle(FLOW_CSS);
        premisesDetails.getChildren().add(premiseText);
    }

    private void setPeopleNo() {
        try (Connection conn = dataSource().getConnection()) {
            final TipologieUso use = Queries.fetchUsageFromSub(sub.getIdcontratto(), conn);
            if (Checks.requiresPeopleNumber(use)) {
                peopleNoName.setText(use.getNome().equals("Commerciale")
                                     ? "Numero dipendenti:"
                                     : "Componenti nucleo familiare:");
                peopleNo.setText(sub.getNumerocomponenti().toString());
            } else {
                peopleNoName.setVisible(false);
                peopleNo.setVisible(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setPlanDetails() {
        final Offerte plan = Queries.fetchPlanById(sub.getOfferta(), dataSource()).orElseThrow();
        final Text planText = new Text(StringUtils.planToString(plan));
        planText.setStyle(FLOW_CSS);
        planDetails.getChildren().add(planText);
    }

    protected void setStatus() {
        subState.setText(Checks.isSubscriptionActive(sub) ? "Attivo" :
                Checks.isSubscriptionBeingReviewed(sub) ? "In gestione" : "Cessato");
    }

    private void setEndRequestTable() {
        reqPublishDateCol.setCellValueFactory(c -> new SimpleStringProperty(dateIt.format(c.getValue()
                .getDataaperturarichiesta())));
        reqResultCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStatorichiesta()));
        reqNotesCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNoterichiesta()));
    }

    protected void refreshEndRequestTable() {
        List<Cessazioni> requests = Collections.emptyList();
        try (Connection conn = dataSource().getConnection()) {
            requests = Queries.fetchEndRequestsFor(sub.getIdcontratto(), conn);
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

    protected abstract Controller getBackController();
}
