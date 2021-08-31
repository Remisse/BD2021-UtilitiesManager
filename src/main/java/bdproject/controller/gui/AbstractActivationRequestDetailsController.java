package bdproject.controller.gui;

import bdproject.controller.Checks;
import bdproject.model.Queries;
import bdproject.tables.pojos.Immobili;
import bdproject.tables.pojos.Offerte;
import bdproject.tables.pojos.RichiesteAttivazione;
import bdproject.tables.pojos.TipologieUso;
import bdproject.utils.LocaleUtils;
import bdproject.view.StringUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

import javax.sql.DataSource;
import java.net.URL;
import java.sql.Connection;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.ResourceBundle;

public abstract class AbstractActivationRequestDetailsController extends AbstractViewController implements Initializable {

    private static final String FXML_FILE = "requestDetails.fxml";
    private static final String FLOW_CSS = "-fx-font: 16 arial";
    private final RichiesteAttivazione request;

    private final DateTimeFormatter dateIt = LocaleUtils.getItDateFormatter();
    private final Map<String, String> mUnit = LocaleUtils.getItUtilitiesUnits();

    @FXML private TextFlow clientDetails;
    @FXML private TextFlow planDetails;
    @FXML private TextFlow premisesDetails;
    @FXML private TextFlow notesFlow;

    @FXML private Label resultLabel;
    @FXML private Label peopleNoName;
    @FXML private Label peopleNo;
    @FXML private Label use;
    @FXML private Label activation;

    protected AbstractActivationRequestDetailsController(final Stage stage, final DataSource dataSource,
            final RichiesteAttivazione request) {
        super(stage, dataSource, FXML_FILE);
        this.request = request;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try (Connection conn = getDataSource().getConnection()) {
            use.setText(Queries.fetchUsageFromRequest(request.getNumero(), conn).getNome());
            activation.setText(Queries.fetchActivationFromRequest(request.getNumero(), conn).getNome());

            setNotes();
            setRequestResult();
            setClientDetails(conn);
            setPremisesDetails();
            setPeopleNo();
            setPlanDetails();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setNotes() {
        final Text notesText = new Text(request.getNote());
        notesText.setStyle(FLOW_CSS);
        notesFlow.getChildren().add(notesText);
    }

    private void setClientDetails(final Connection conn) {
        final Text clientText = new Text(StringUtils.clientToString(request.getCliente(), conn));
        clientText.setStyle(FLOW_CSS);
        clientDetails.getChildren().add(clientText);
    }

    private void setPremisesDetails() {
        final Immobili premises = Queries.fetchPremisesFromMeter(request.getContatore(), getDataSource());
        final Text premisesText = new Text(StringUtils.premisesToString(premises));
        premisesText.setStyle(FLOW_CSS);
        premisesDetails.getChildren().add(premisesText);
    }

    private void setPeopleNo() {
        try (Connection conn = getDataSource().getConnection()) {
            final TipologieUso use = Queries.fetchUsageById(request.getUso(), conn).orElseThrow();
            if (Checks.requiresPeopleNumber(use)) {
                peopleNoName.setText("Componenti nucleo familiare:");
                peopleNo.setText(request.getNumerocomponenti().toString());
            } else {
                peopleNoName.setVisible(false);
                peopleNo.setVisible(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setPlanDetails() {
        final Offerte plan = Queries.fetchPlanById(request.getOfferta(), getDataSource()).orElseThrow();
        final Text planText = new Text(StringUtils.planToString(plan));
        planText.setStyle(FLOW_CSS);
        planDetails.getChildren().add(planText);
    }

    private void setRequestResult() {
        resultLabel.setText(StringUtils.requestStatusToString(request.getStato()));
    }

    protected RichiesteAttivazione getRequest() {
        return this.request;
    }
}
