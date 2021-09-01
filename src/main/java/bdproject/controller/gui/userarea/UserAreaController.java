package bdproject.controller.gui.userarea;

import bdproject.controller.Choice;
import bdproject.controller.ChoiceImpl;
import bdproject.controller.gui.AbstractViewController;
import bdproject.controller.gui.HomeController;
import bdproject.controller.gui.ViewController;
import bdproject.controller.Checks;
import bdproject.model.Queries;
import bdproject.model.SessionHolder;
import bdproject.tables.pojos.*;
import bdproject.utils.FXUtils;
import bdproject.utils.LocaleUtils;
import bdproject.view.StringUtils;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import org.apache.commons.validator.routines.EmailValidator;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static bdproject.Tables.*;

public class UserAreaController extends AbstractViewController implements Initializable {

    private static final String FXML_FILE = "userArea.fxml";
    private static final int POSTCODE_LIMIT = 5;
    private static final int PASSWORD_MAX = 30;
    private static final int PASSWORD_MIN = 8;
    private final DateTimeFormatter dateIt = LocaleUtils.getItDateFormatter();
    private final DecimalFormat decimal = LocaleUtils.getItDecimalFormat();

    @FXML private Label clientFullName;

    @FXML private TableView<Bollette> reportTable;
    @FXML private TableColumn<Bollette, String> publishDate;
    @FXML private TableColumn<Bollette, String> deadline;
    @FXML private TableColumn<Bollette, String> paid;
    @FXML private TableColumn<Bollette, String> amount;
    @FXML private TableColumn<Bollette, String> estimatedCol;

    @FXML private TableView<RichiesteAttivazione> activationReqTable;
    @FXML private TableColumn<RichiesteAttivazione, String> reqCreationDateCol;
    @FXML private TableColumn<RichiesteAttivazione, String> reqUtilityCol;
    @FXML private TableColumn<RichiesteAttivazione, String> reqResultCol;

    @FXML private ComboBox<Choice<Integer, ContrattiDettagliati>> subscriptionChoice;
    @FXML private Button subDetails;
    @FXML private Button consumptionTrend;

    @FXML private TextField consumption;
    @FXML private TextFlow lastReading;
    @FXML private Button addMeasurement;

    @FXML private TextField street;
    @FXML private TextField civic;
    @FXML private TextField postcode;
    @FXML private TextField municipality;
    @FXML private TextField state;
    @FXML private TextField email;
    @FXML private TextField phone;
    @FXML private PasswordField currentPw;
    @FXML private PasswordField newPw;
    @FXML private PasswordField confirmPw;
    @FXML private ComboBox<String> incomeBracket;

    private UserAreaController(final Stage stage, final DataSource dataSource) {
        super(stage, dataSource, FXML_FILE);
    }

    public static ViewController create(final Stage stage, final DataSource dataSource) {
        return new UserAreaController(stage, dataSource);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        populateSubscriptionBox();
        populateUserDetails();
        initializeReportTable();
        initializeActivationRequestTable();
        showLastMeasurement();
        showFullUsername();
    }

    private void showFullUsername() {
        try (Connection conn = getDataSource().getConnection()) {
            final Persone client = Queries.fetchPersonById(SessionHolder.getSession().orElseThrow().getUserId(), conn).orElseThrow();
            clientFullName.setText(client.getNome() + " " + client.getCognome());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void populateUserDetails() {
        try (Connection conn = getDataSource().getConnection()) {
            final ClientiDettagliati client = Queries.fetchClientById(
                    SessionHolder.getSession().orElseThrow().getUserId(), conn).orElseThrow();

            street.setText(client.getVia());
            civic.setText(client.getNumcivico());
            postcode.setText(client.getCap());
            municipality.setText(client.getComune());
            state.setText(client.getProvincia());
            email.setText(client.getEmail());
            phone.setText(client.getNumerotelefono());
            incomeBracket.setItems(FXCollections.observableList(Queries.fetchAllIncomeBrackets(conn)));
            incomeBracket.setValue(client.getFasciareddito());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void populateSubscriptionBox() {
        List<Choice<Integer, ContrattiDettagliati>> subs;
        try (Connection conn = getDataSource().getConnection()) {
            final DSLContext ctx = DSL.using(conn, SQLDialect.MYSQL);
            subs = Queries.fetchAll(ctx, CONTRATTI_DETTAGLIATI, ContrattiDettagliati.class)
                    .stream()
                    .filter(s -> s.getCliente().equals(SessionHolder.getSession().orElseThrow().getUserId()))
                    .map(s -> new ChoiceImpl<>(s.getIdcontratto(), s, (id, sub) -> id.toString()))
                    .collect(Collectors.toList());
            subscriptionChoice.setItems(FXCollections.observableList(subs));
            if (!subs.isEmpty()) {
                subscriptionChoice.setValue(subs.get(0));
                subDetails.setDisable(false);
                toggleTrendButton();
                addMeasurement.setDisable(false);
                toggleMeasurementFields();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void toggleTrendButton() {
        consumptionTrend.setDisable(!(subscriptionChoice.getValue() != null
                && subscriptionChoice.getValue().getValue().getDatainizio() != null));
    }

    private void toggleMeasurementFields() {
        consumption.setDisable(false);
        consumption.setText("");
    }

    private void initializeReportTable() {
        try (Connection conn = getDataSource().getConnection()) {
            final DSLContext ctx = DSL.using(conn, SQLDialect.MYSQL);
            final Persone person = Queries.fetchPersonById(SessionHolder.getSession().orElseThrow().getUserId(), conn).orElseThrow();
            final List<ContrattiDettagliati> userSubs = Queries.fetchAll(ctx, CONTRATTI_DETTAGLIATI, ContrattiDettagliati.class)
                    .stream()
                    .filter(s -> s.getCliente().equals(person.getIdentificativo()))
                    .collect(Collectors.toList());

            if (!userSubs.isEmpty()) {
                publishDate.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getDataemissione().format(dateIt)));
                deadline.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getDatascadenza().format(dateIt)));
                paid.setCellValueFactory(cellData -> {
                    final LocalDate paidDate = cellData.getValue().getDatapagamento();
                    return new SimpleStringProperty(paidDate != null ? paidDate.format(dateIt) : "No");
                });
                amount.setCellValueFactory(cellData -> new SimpleStringProperty(
                        "€" + decimal.format(cellData.getValue().getImporto())));
                estimatedCol.setCellValueFactory(c -> new SimpleStringProperty(
                        StringUtils.byteToYesNo(c.getValue().getStimata())));

                updateTableItems();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateTableItems() {
        try (Connection conn = getDataSource().getConnection()) {
            DSLContext query = DSL.using(conn, SQLDialect.MYSQL);
            var reports = query.select(BOLLETTE.asterisk())
                    .from(CONTRATTI, BOLLETTE)
                    .where(CONTRATTI.IDCONTRATTO.eq(subscriptionChoice.getValue().getValue().getIdcontratto()))
                    .and(BOLLETTE.IDCONTRATTO.eq(CONTRATTI.IDCONTRATTO))
                    .fetchInto(Bollette.class);
            reportTable.setItems(FXCollections.observableList(reports));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void initializeActivationRequestTable() {
        reqCreationDateCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDatarichiesta().toString()));
        reqResultCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStato()));
        reqUtilityCol.setCellValueFactory(c -> {
            final String utility = Queries.fetchPlanById(c.getValue().getOfferta(), getDataSource()).orElseThrow().getMateriaprima();
            return new SimpleStringProperty(utility);
        });
        refreshActivationRequestTable();
    }

    private void refreshActivationRequestTable() {
        try (Connection conn = getDataSource().getConnection()) {
            final DSLContext ctx = DSL.using(conn, SQLDialect.MYSQL);
            final List<RichiesteAttivazione> reqs = Queries.fetchAll(ctx, RICHIESTE_ATTIVAZIONE, RichiesteAttivazione.class)
                    .stream()
                    .filter(r -> r.getCliente().equals(SessionHolder.getSession().orElseThrow().getUserId()))
                    .collect(Collectors.toList());
            activationReqTable.setItems(FXCollections.observableList(reqs));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void doShowActivationReqDetails() {
        final RichiesteAttivazione request = activationReqTable.getSelectionModel().getSelectedItem();
        if (request != null) {
            createSubWindow(UserActivationRequestDetailsController.create(null, getDataSource(), request));
        } else {
            FXUtils.showBlockingWarning("Seleziona una richiesta.");
        }
    }

    @FXML
    private void doDeleteActivationReq() {
        final RichiesteAttivazione selectedReq = activationReqTable.getSelectionModel().getSelectedItem();
        if (selectedReq != null) {
            try (Connection conn = getDataSource().getConnection()) {
                if (selectedReq.getStato().equals("N") || selectedReq.getStato().equals("E")) {
                    final int result = Queries.deleteActivationRequest(selectedReq.getNumero(), conn);
                    if (result == 1) {
                        FXUtils.showBlockingWarning("Richiesta eliminata.");
                        refreshActivationRequestTable();
                    } else {
                        FXUtils.showBlockingWarning(StringUtils.getGenericError());
                    }
                } else {
                    FXUtils.showBlockingWarning("Non puoi eliminare una richiesta già esaminata.");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            FXUtils.showBlockingWarning("Seleziona una richiesta da eliminare.");
        }
    }

    @FXML
    private void doAddMeasurement() {
        final var subChoice = subscriptionChoice.getSelectionModel().getSelectedItem();
        if (subChoice != null) {
            try (Connection conn = getDataSource().getConnection()) {
                if (Checks.isSubscriptionActive(subChoice.getValue(), conn)) {
                    if (Checks.isValidConsumption(consumption.getText())) {
                        var measurement = new Letture(
                                BigDecimal.valueOf(Long.parseLong(consumption.getText())),
                                subChoice.getValue().getContatore(),
                                LocalDate.now(),
                                (byte) 0,
                                null
                        );
                        Queries.insertMeasurement(measurement, conn);
                        showLastMeasurement();
                    } else {
                        FXUtils.showBlockingWarning("Controlla di aver inserito correttamente la lettura.");
                    }
                } else {
                    FXUtils.showBlockingWarning("Il contratto risulta non attivo. Non puoi comunicare nuove letture.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                FXUtils.showError("Impossibile inserire la lettura.");
            }
        } else {
            FXUtils.showBlockingWarning("Seleziona un contratto.");
        }
    }

    private void showLastMeasurement() {
        final var subChoice = subscriptionChoice.getSelectionModel().getSelectedItem();
        if (subChoice != null) {
            try (Connection conn = getDataSource().getConnection()) {
                Optional<Letture> lastMeasurement = Queries.fetchLastMeasurement(subChoice.getValue(), conn);
                lastMeasurement.ifPresentOrElse(m -> {
                    final Text text = new Text("Consumi: " + m.getConsumi()
                            + "\nData: " + m.getDataeffettuazione().format(dateIt)
                            + "\nConfermata: " + StringUtils.byteToYesNo(m.getConfermata()));
                    lastReading.getChildren().clear();
                    lastReading.getChildren().add(text);
                }, () -> {
                    lastReading.getChildren().clear();
                    lastReading.getChildren().add(new Text("N.D."));
                });
            } catch (SQLException e) {
                e.printStackTrace();
                FXUtils.showError("Errore nella visualizzazione dell'ultima lettura.");
            }
        } else {
            lastReading.getChildren().add(new Text("N.D."));
        }
    }

    @FXML
    private void showSubDetails() {
        switchTo(UserSubDetailsController.create(getStage(), getDataSource(), subscriptionChoice.getValue().getValue()));
    }

    @FXML
    private void payReport() {
        var report = reportTable.getSelectionModel().getSelectedItem();
        if (report == null) {
            FXUtils.showBlockingWarning("Seleziona una bolletta da pagare.");
        } else if (report.getDatapagamento() != null) {
            FXUtils.showBlockingWarning("La bolletta selezionata risulta già pagata.");
        } else {
            try (Connection conn = getDataSource().getConnection()) {
                final int outcome = Queries.payReport(report.getIdcontratto(), report.getDataemissione(), conn);
                if (outcome == 1) {
                    FXUtils.showBlockingWarning("Pagamento effettuato con successo.");
                    updateTableItems();
                } else {
                    FXUtils.showError("Impossibile procedere al pagamento.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                FXUtils.showError("Impossibile procedere al pagamento.");
            }
        }
    }

    @FXML
    private void goBack() {
        switchTo(HomeController.create(getStage(), getDataSource()));
    }

    @FXML
    private void doUpdateClient() {
        if (areUserFieldsInvalid()) {
            FXUtils.showBlockingWarning("Verifica di aver inserito correttamente i nuovi dati.");
        } else {
            final int clientId = SessionHolder.getSession().orElseThrow().getUserId();
            try (Connection conn = getDataSource().getConnection()) {
                final int resultUpdateData = Queries.updatePerson(
                            email.getText(),
                            postcode.getText(),
                            municipality.getText(),
                            civic.getText(),
                            phone.getText(),
                            state.getText(),
                            street.getText(),
                            clientId,
                            conn);
                if (resultUpdateData == 1) {
                    FXUtils.showBlockingWarning("Dati modificati con successo.");
                } else {
                    FXUtils.showBlockingWarning("Impossibile modificare i dati.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                FXUtils.showError(StringUtils.getGenericError());
            }
        }
    }

    @FXML
    private void doUpdatePassword() {
        if (!isAreAllPasswordFieldsBlank() && isNewPasswordCorrectlySet()) {
            final int clientId = SessionHolder.getSession().orElseThrow().getUserId();
            try (Connection conn = getDataSource().getConnection()) {
                final int resultUpdatePw = Queries.updatePersonPassword(newPw.getText(), clientId, conn);
                if (resultUpdatePw == 1) {
                    FXUtils.showBlockingWarning("Password modificata con successo.");
                } else {
                    FXUtils.showBlockingWarning("Impossibile modificare la password.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                FXUtils.showError(StringUtils.getGenericError());
            }
        } else {
            FXUtils.showBlockingWarning("Verifica i dati inseriti.");
        }
    }

    @FXML
    private void onSubChosen() {
        updateTableItems();
        toggleMeasurementFields();
        toggleTrendButton();
        showLastMeasurement();
    }

    @FXML
    private void showConsumptionTrend() {
        switchTo(ConsumptionStatsController.create(getStage(), getDataSource(), subscriptionChoice.getValue().getValue()));
    }

    private boolean areUserFieldsInvalid() {
        return street.getText().length() == 0
                || civic.getText().length() == 0
                || municipality.getText().length() == 0
                || postcode.getText().length() != POSTCODE_LIMIT
                || state.getText().length() != 2
                || phone.getText().length() == 0
                || email.getText().length() == 0
                || !EmailValidator.getInstance().isValid(email.getText())
                || phone.getText().length() == 0
                || !Checks.isNumber(phone.getText());
    }

    private boolean isAreAllPasswordFieldsBlank() {
        return currentPw.getText().length() == 0 && newPw.getText().length() == 0 && confirmPw.getText().length() == 0;
    }

    private boolean isNewPasswordCorrectlySet() {
        Persone person = null;
        try (Connection conn = getDataSource().getConnection()) {
            person = Queries.fetchPersonById(SessionHolder.getSession().orElseThrow().getUserId(), conn).orElseThrow();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (person == null) {
            throw new IllegalStateException("Fetched client in their own user area should not be null!");
        }
        return currentPw.getText().equals(person.getPassword()) && newPw.getText().length() > PASSWORD_MIN && newPw.getText().length() < PASSWORD_MAX
                && confirmPw.getText().equals(newPw.getText());
    }

    /**
     * Mock implementation.
     */
    @FXML
    private void doDownloadFile() {
        Bollette report = reportTable.getSelectionModel().getSelectedItem();
        if (report == null) {
            FXUtils.showBlockingWarning("Seleziona una bolletta.");
        } else {
            FXUtils.showBlockingWarning("File scaricato.");
        }
    }
}
