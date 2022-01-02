package bdproject.controller.gui;

import bdproject.controller.Checks;
import bdproject.model.Queries;
import bdproject.model.SessionHolder;
import bdproject.tables.pojos.*;
import bdproject.utils.LocaleUtils;
import bdproject.view.StringUtils;
import bdproject.utils.ViewUtils;
import javafx.application.Platform;
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

    private final ContrattiApprovati sub;
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
    @FXML private Label creationDateLabel;
    @FXML private TextFlow requestNotesFlow;

    @FXML private TableView<Cessazioni> endRequestTable;
    @FXML private TableColumn<Cessazioni, String> reqPublishDateCol;
    @FXML private TableColumn<Cessazioni, String> reqResultCol;
    @FXML private TableColumn<Cessazioni, String> reqNotesCol;

    protected AbstractSubscriptionDetailsController(final Stage stage, final DataSource dataSource, final SessionHolder holder,
            final ContrattiApprovati sub) {
        super(stage, dataSource, holder, FXML_FILE);
        this.sub = sub;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try (Connection conn = dataSource().getConnection()) {
            final LocalDate creationDate = sub.getDataaperturarichiesta();
            final LocalDate startDate = sub.getDatachiusurarichiesta();
            final LocalDate endDate = sub.getDatacessazione();

            final Text requestNotes = new Text(sub.getNoterichiesta());

            Platform.runLater(() -> {
                creationDateLabel.setText(dateIt.format(creationDate));
                subStartDate.setText(startDate != null ? dateIt.format(startDate) : "N.D.");
                subEndDate.setText(endDate != null ? dateIt.format(endDate) : "N.D.");

                requestNotes.setStyle(FLOW_CSS);
                requestNotesFlow.getChildren().add(requestNotes);
            });
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
            ViewUtils.showError(e.getSQLState() + "\n" + e.getMessage());
        }
    }

    protected abstract void setOther();

    protected ContrattiApprovati getSubscription() {
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

        Platform.runLater(() -> {
            premiseText.setStyle(FLOW_CSS);
            premisesDetails.getChildren().add(premiseText);
        });
    }

    private void setPeopleNo() {
        try (Connection conn = dataSource().getConnection()) {
            final TipologieUso use = Queries.fetchUsageFromSub(sub.getIdcontratto(), conn);

            Platform.runLater(() -> {
                if (Checks.requiresPeopleNumber(use)) {
                    peopleNoName.setText(use.getNome().equals("Commerciale")
                            ? "Numero dipendenti:"
                            : "Componenti nucleo familiare:");
                    peopleNo.setText(sub.getNumerocomponenti().toString());
                } else {
                    peopleNoName.setVisible(false);
                    peopleNo.setVisible(false);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setPlanDetails() {
        final Offerte plan = Queries.fetchPlanById(sub.getOfferta(), dataSource()).orElseThrow();
        final Text planText = new Text(StringUtils.planToString(plan));

        Platform.runLater(() -> {
            planText.setStyle(FLOW_CSS);
            planDetails.getChildren().add(planText);
        });
    }

    protected void setStatus() {
        subState.setText(Checks.isSubscriptionActive(sub) ? "Attivo" : "Cessato");
    }

    private void setEndRequestTable() {
        Platform.runLater(() -> {
            reqPublishDateCol.setCellValueFactory(c -> new SimpleStringProperty(dateIt.format(c.getValue()
                    .getDataaperturarichiesta())));
            reqResultCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStatorichiesta()));
            reqNotesCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNoterichiesta()));
        });
    }

    protected void refreshEndRequestTable() {
        List<Cessazioni> requests = Collections.emptyList();
        try (Connection conn = dataSource().getConnection()) {
            requests = Queries.fetchEndRequestsFor(sub.getIdcontratto(), conn);
        } catch (Exception e) {
            e.printStackTrace();
        }

        List<Cessazioni> finalRequests = requests;
        Platform.runLater(() -> endRequestTable.setItems(FXCollections.observableList(finalRequests)));
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
