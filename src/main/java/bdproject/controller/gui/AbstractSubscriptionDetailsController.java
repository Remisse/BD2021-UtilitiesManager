package bdproject.controller.gui;

import bdproject.controller.Checks;
import bdproject.model.Queries;
import bdproject.tables.pojos.Distributori;
import bdproject.utils.LocaleUtils;
import bdproject.view.StringRepresentations;
import bdproject.tables.pojos.Contratti;
import bdproject.tables.pojos.Interruzioni;
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
import java.util.Map;
import java.util.ResourceBundle;

public abstract class AbstractSubscriptionDetailsController extends AbstractViewController implements Initializable {

    private static final String FXML_FILE = "subDetails.fxml";
    private final Contratti subscription;
    private final DateTimeFormatter dateIt = LocaleUtils.getItDateFormatter();
    private final Map<String, String> mUnit = LocaleUtils.getItUtilitiesUnits();

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
    @FXML private TableColumn<Interruzioni, String> interruptionDate;
    @FXML private TableColumn<Interruzioni, String> reactivationDate;
    @FXML private TableColumn<Interruzioni, String> interruptionDescription;

    @FXML private Label distributorLabel;
    @FXML private TableView<Distributori> distributorTable;
    @FXML private TableColumn<Distributori, String> distributorNameCol;
    @FXML private TableColumn<Distributori, String> distributorPhoneCol;
    @FXML private TableColumn<Distributori, String> distributorEmailCol;

    protected AbstractSubscriptionDetailsController(final Stage stage, final DataSource dataSource, final Contratti subscription) {
        super(stage, dataSource, FXML_FILE);
        this.subscription = subscription;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try (Connection conn = getDataSource().getConnection()) {
            final LocalDate startDate = subscription.getDatainizio();
            final LocalDate endDate = subscription.getDatacessazione();
            subStartDate.setText(startDate != null ? dateIt.format(startDate) : "N.D.");
            subEndDate.setText(endDate != null ? dateIt.format(endDate) : "N.D.");

            use.setText(subscription.getTipouso());
            activation.setText(subscription.getNomeattivazione());

            setClientDetails(conn);
            setPlanDetails(conn);
            setPeopleNo();
            setStatus(startDate, endDate, conn);
            setPremisesDetails(conn);
            setDistributorTable(conn);
            setInterruptionTable(conn);
            setOther();

        } catch (SQLException e) {
            e.printStackTrace();
            FXUtils.showError(e.getSQLState() + "\n" + e.getMessage());
        }
    }

    private void setDistributorTable(final Connection conn) {
        if (showDistributorTable()) {
            distributorEmailCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEmailcontatto()));
            distributorNameCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNome()));
            distributorPhoneCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNumerocontatto()));

            List<Distributori> distributors = Queries.findDistributor(Queries.zon)

        } else {
            distributorLabel.setVisible(false);
            distributorTable.setVisible(false);
        }
    }

    protected abstract boolean showDistributorTable();

    protected abstract void setOther();

    protected Contratti getSubscription() {
        return subscription;
    }

    private void setClientDetails(final Connection conn) {
        clientDetails.getChildren().add(new Text(StringRepresentations.clientToString(subscription.getCodicecliente(), conn)));
    }

    private void setInterruptionTable(Connection conn) {
        final List<Interruzioni> interruptions = Queries.getInterruptions(subscription.getIdcontratto(), conn);
        interruptionTable.setItems(FXCollections.observableList(interruptions));
        interruptionDate.setCellValueFactory(c -> new SimpleStringProperty(dateIt.format(c.getValue().getDatainterruzione())));
        reactivationDate.setCellValueFactory(c -> new SimpleStringProperty(dateIt.format(c.getValue().getDatariattivazione())));
        interruptionDescription.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDescrizione()));
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
