package bdproject.controller.gui;

import bdproject.controller.Checks;
import bdproject.model.Queries;
import bdproject.tables.pojos.Persone;
import bdproject.view.StringRepresentations;
import bdproject.tables.pojos.Contratti;
import bdproject.tables.pojos.Interruzioni;
import bdproject.utils.FXUtils;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import org.checkerframework.common.value.qual.MinLenFieldInvariant;

import javax.sql.DataSource;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;

public abstract class AbstractSubscriptionDetailsController extends AbstractViewController implements Initializable {

    private static final String FXML_FILE = "subDetails.fxml";
    private final Contratti subscription;
    private final Map<String, String> mUnit = Map.of(
            "Luce", "€/kWh",
            "Gas", "€/Smc",
            "Acqua", "€/mc"
    );
    @FXML private Button back;
    @FXML private Button endSubscription;
    @FXML private Button modifyFirm;
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
    @FXML private TableColumn<Interruzioni, LocalDate> interruptionDate;
    @FXML private TableColumn<Interruzioni, LocalDate> reactivationDate;

    protected AbstractSubscriptionDetailsController(final Stage stage, final DataSource dataSource, final Contratti subscription) {
        super(stage, dataSource, FXML_FILE);
        this.subscription = subscription;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try (Connection conn = getDataSource().getConnection()) {
            final LocalDate startDate = subscription.getDatainizio();
            final LocalDate endDate = subscription.getDatacessazione();
            subStartDate.setText(startDate != null ? startDate.toString() : "N.D.");
            subEndDate.setText(endDate != null ? endDate.toString() : "N.D.");

            use.setText(subscription.getTipouso());
            activation.setText(subscription.getNomeattivazione());

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

    protected Contratti getSubscription() {
        return subscription;
    }

    private void setClientDetails(final Connection conn) {
        Optional<Persone> client = Queries.fetchClientById(subscription.getCodicecliente(), conn);
        client.ifPresent(c -> clientDetails.getChildren().add(new Text(StringRepresentations.clientToString(c))));
    }

    private void setInterruptionTable(Connection conn) {
        var interruptions = Queries.getInterruptions(subscription, conn);
        interruptionTable.setItems(FXCollections.observableList(interruptions));
        interruptionDate.setCellValueFactory(new PropertyValueFactory<>("datainterruzione"));
        reactivationDate.setCellValueFactory(new PropertyValueFactory<>("datariattivazione"));
    }

    private void setPremisesDetails(Connection conn) {
        var premises = Queries.getPremises(subscription, conn);
        premisesDetails.getChildren().add(new Text(StringRepresentations.premisesToString(premises, conn)));
    }

    private void setPeopleNo() {
        if (Checks.requiresPeopleNumber(subscription)) {
            peopleNoName.setText(subscription.getTipouso().equals("Commerciale")
                                 ? "Numero dipendenti:"
                                 : "Componenti nucleo familiare:");
            peopleNo.setText(subscription.getNumeropersone().toString());
        } else {
            peopleNoName.setVisible(false);
            peopleNo.setVisible(false);
        }
    }

    private void setPlanDetails(Connection conn) {
        var plan = Queries.getPlan(subscription, conn);
        planDetails.getChildren().add(new Text(
                        plan.getNome() +
                        "\nMateria prima: " + plan.getMateriaprima() +
                        "\nCosto materia prima: " + plan.getCostomateriaprima() + mUnit.get(plan.getMateriaprima())));
    }

    protected void setStatus(LocalDate startDate, LocalDate endDate, final Connection conn) {
        final boolean isInterrupted = Queries.hasOngoingInterruption(subscription, conn);
        subState.setText(Checks.isSubscriptionActive(subscription, conn) ? "Attivo"
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
        abstractDoEndSubscription();
    }

    protected abstract void abstractDoEndSubscription();

    protected abstract ViewController getBackController();
}
