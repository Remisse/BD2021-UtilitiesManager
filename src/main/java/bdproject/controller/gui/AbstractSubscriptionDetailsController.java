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
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public abstract class AbstractSubscriptionDetailsController extends AbstractViewController implements Initializable {

    private static final String FXML_FILE = "subDetails.fxml";
    private final ContrattiDettagliati detailedSub;
    private final DateTimeFormatter dateIt = LocaleUtils.getItDateFormatter();
    private final Map<String, String> mUnit = LocaleUtils.getItUtilitiesUnits();

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
            setPlanDetails(conn);
            setPeopleNo();
            setStatus(startDate, endDate, conn);
            setPremisesDetails(conn);
            setInterruptionTable(conn);
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

    private void setPremisesDetails(Connection conn) {
        final Immobili premises = Queries.fetchPremisesFromSubscription(detailedSub, getDataSource());
        premisesDetails.getChildren().add(new Text(StringUtils.premisesToString(premises)));
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

    private void setPlanDetails(Connection conn) {
        Offerte plan = Queries.fetchPlanById(detailedSub.getOfferta(), getDataSource()).orElseThrow();
        planDetails.getChildren().add(new Text(
                        plan.getNome() +
                        "\nMateria prima: " + plan.getMateriaprima() +
                        "\nCosto materia prima: " + plan.getCostomateriaprima() + mUnit.get(plan.getMateriaprima())));
    }

    protected void setStatus(LocalDate startDate, LocalDate endDate, final Connection conn) {
        final boolean isInterrupted = Queries.hasOngoingInterruption(detailedSub, conn);
        subState.setText(Checks.isSubscriptionActive(detailedSub, conn) ? "Attivo"
                         : startDate != null && endDate == null && isInterrupted ? "Interrotto"
                         : startDate != null ? "Cessato"
                         : "In attesa di attivazione");
    }

    @FXML
    private void goBack() {
        switchTo(getBackController());
    }

    @FXML
    private void doEndSubscription() {
        abstractDoInsertEndRequest();
    }

    protected abstract void abstractDoInsertEndRequest();

    protected abstract ViewController getBackController();
}
