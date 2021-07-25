package bdproject.controller.gui.userarea;

import bdproject.controller.gui.AbstractViewController;
import bdproject.controller.gui.ViewController;
import bdproject.controller.Checks;
import bdproject.model.Queries;
import bdproject.view.StringRepresentations;
import bdproject.tables.pojos.Contratti;
import bdproject.tables.pojos.Interruzioni;
import bdproject.tables.pojos.PersoneGiuridiche;
import bdproject.utils.FXUtils;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

import javax.sql.DataSource;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Map;
import java.util.ResourceBundle;

public class SubscriptionDetailsController extends AbstractViewController implements Initializable {

    private static final String FXML_FILE = "subDetails.fxml";
    public static final int FORMA_GIURIDICA_MAX = 50;
    public static final int RAGIONE_SOCIALE_MAX = 30;
    public static final int VAT_CODE_MAX = 11;
    public static final int STREET_MAX = 50;
    public static final int CIVIC_MAX = 10;
    public static final int POSTCODE_LENGTH = 5;
    public static final int MUNICIPALITY_MAX = 30;
    public static final int STATE_LENGTH = 2;
    private final Contratti subscription;
    private final Map<String, String> mUnit = Map.of(
            "Luce", "€/kWh",
            "Gas", "€/Smc",
            "Acqua", "€/mc"
    );
    @FXML
    private Button back;
    @FXML
    private Button endSubscription;
    @FXML
    private Button modifyFirm;
    @FXML
    private TextFlow planDetails;
    @FXML
    private TextFlow premisesDetails;
    @FXML
    private Label peopleNoName;
    @FXML
    private Label peopleNo;
    @FXML
    private Label subStartDate;
    @FXML
    private Label subEndDate;
    @FXML
    private Label subState;
    @FXML
    private Label use;
    @FXML
    private Label activation;
    @FXML
    private Label firmLabel;
    @FXML
    private TextField formaGiuridica;
    @FXML
    private TextField ragioneSociale;
    @FXML
    private TextField vatCode;
    @FXML
    private TextField street;
    @FXML
    private TextField civic;
    @FXML
    private TextField municipality;
    @FXML
    private TextField postcode;
    @FXML
    private TextField state;
    @FXML
    private TableView<Interruzioni> interruptionTable;
    @FXML
    private TableColumn<Interruzioni, LocalDate> interruptionDate;
    @FXML
    private TableColumn<Interruzioni, LocalDate> reactivationDate;

    private SubscriptionDetailsController(final Stage stage, final DataSource dataSource, final Contratti subscription) {
        super(stage, dataSource, FXML_FILE);
        this.subscription = subscription;
    }

    public static ViewController create(final Stage stage, final DataSource dataSource, final Contratti subscription) {
        return new SubscriptionDetailsController(stage, dataSource, subscription);
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

            setPlanDetails(conn);
            setPeopleNo();
            setStatus(startDate, endDate, conn);
            setPremisesDetails(conn);
            setInterruptionTable(conn);
            setFirm(conn);

        } catch (SQLException e) {
            e.printStackTrace();
            FXUtils.showError(e.getSQLState() + "\n" + e.getMessage());
        }
    }

    private void setFirm(Connection conn) {
        PersoneGiuridiche firm = Queries.getFirm(subscription, conn);
        if (firm != null) {
            formaGiuridica.setText(firm.getFormagiuridica());
            ragioneSociale.setText(firm.getRagionesociale());
            vatCode.setText(firm.getPartitaiva());
            street.setText(firm.getVia());
            civic.setText(firm.getNumcivico());
            municipality.setText(firm.getComune());
            postcode.setText(String.valueOf(firm.getCap()));
            state.setText(firm.getProvincia());
        } else {
            firmLabel.setVisible(false);
            formaGiuridica.setVisible(false);
            ragioneSociale.setVisible(false);
            vatCode.setVisible(false);
            street.setVisible(false);
            civic.setVisible(false);
            municipality.setVisible(false);
            postcode.setVisible(false);
            state.setVisible(false);
            modifyFirm.setVisible(false);
        }
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

    private void setStatus(LocalDate startDate, LocalDate endDate, final Connection conn) {
        final boolean isInterrupted = Queries.hasOngoingInterruption(subscription, conn);
        subState.setText(Checks.isSubscriptionActive(subscription, conn) ? "Attivo"
                         : startDate != null && endDate == null && isInterrupted ? "Interrotto"
                         : startDate != null ? "Cessato"
                         : "In attesa di attivazione");
    }

    @FXML
    private void goBack() {
        switchTo(UserAreaController.create(getStage(), getDataSource()));
    }

    @FXML
    private void doEndSubscription() {
        try (Connection conn = getDataSource().getConnection()) {
            if (subscription.getDatacessazione() != null) {
                FXUtils.showBlockingWarning("Il contratto risulta già cessato.");
            } else if (Queries.hasOngoingInterruption(subscription, conn)) {
                FXUtils.showBlockingWarning("La fornitura risulta temporaneamente interrotta. Non è attualmente" +
                        "possibile cessare il contratto.");
            } else if (!Queries.allReportsPaid(subscription, conn)) {
                FXUtils.showBlockingWarning("Risultano bollette non pagate. Non è attualmente" +
                        "possibile cessare il contratto.");
            } else {
                Queries.ceaseSubscription(subscription, conn);
                setStatus(subscription.getDatainizio(), subscription.getDatacessazione(), conn);
                FXUtils.showBlockingWarning("Contratto cessato.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            FXUtils.showError(e.getSQLState());
        }
    }

    @FXML
    private void doModifyFirm() {
        if (firmFieldsValid()) {
            try (Connection conn = getDataSource().getConnection()) {
                var firmCode = Queries.getFirm(subscription, conn).getCodiceazienda();
                var newFirm = new PersoneGiuridiche(
                        firmCode,
                        vatCode.getText(),
                        street.getText(),
                        civic.getText(),
                        Integer.parseInt(postcode.getText()),
                        municipality.getText(),
                        state.getText(),
                        formaGiuridica.getText(),
                        ragioneSociale.getText()
                );
                final int successful = Queries.updateFirm(newFirm, conn);
                if (successful == 1) {
                    FXUtils.showBlockingWarning("Dati dell'azienda aggiornati.");
                } else {
                    FXUtils.showBlockingWarning("Aggiornamento non riuscito.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            FXUtils.showBlockingWarning("Controlla di aver inserito correttamente i nuovi dati.");
        }
    }

    private boolean firmFieldsValid() {
        return formaGiuridica.getText().length() > 0 && formaGiuridica.getText().length() <= FORMA_GIURIDICA_MAX
                && ragioneSociale.getText().length() > 0 && ragioneSociale.getText().length() <= RAGIONE_SOCIALE_MAX
                && vatCode.getText().length() > 0 && vatCode.getText().length() <= VAT_CODE_MAX
                && street.getText().length() > 0 && street.getText().length() <= STREET_MAX
                && civic. getText().length() > 0 && civic.getText().length() <= CIVIC_MAX
                && postcode.getText().length() == POSTCODE_LENGTH && Checks.isNumber(postcode.getText())
                && municipality.getText().length() > 0 && municipality.getText().length() <= MUNICIPALITY_MAX
                && state.getText().length() == STATE_LENGTH;
    }
}
