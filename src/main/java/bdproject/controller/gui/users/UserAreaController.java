package bdproject.controller.gui.users;

import bdproject.controller.Choice;
import bdproject.controller.ChoiceImpl;
import bdproject.controller.gui.AbstractController;
import bdproject.controller.gui.HomeController;
import bdproject.controller.gui.Controller;
import bdproject.controller.Checks;
import bdproject.model.types.StatusType;
import bdproject.model.Queries;
import bdproject.model.SessionHolder;
import bdproject.tables.pojos.*;
import bdproject.utils.ViewUtils;
import bdproject.utils.LocaleUtils;
import bdproject.view.StringUtils;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.apache.commons.validator.routines.EmailValidator;
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

public class UserAreaController extends AbstractController implements Initializable {

    private static final String FXML_FILE = "userArea.fxml";
    private static final int POSTCODE_LIMIT = 5;
    private static final int PASSWORD_MAX = 30;
    private static final int PASSWORD_MIN = 8;
    private static final DateTimeFormatter DATE_FORMAT = LocaleUtils.getItDateFormatter();
    private static final DecimalFormat DECIMAL_FORMAT = LocaleUtils.getItDecimalFormat();

    @FXML private Label clientFullName;

    @FXML private TableView<Bollette> reportTable;
    @FXML private TableColumn<Bollette, String> publishDate;
    @FXML private TableColumn<Bollette, String> deadline;
    @FXML private TableColumn<Bollette, String> paid;
    @FXML private TableColumn<Bollette, String> amount;
    @FXML private TableColumn<Bollette, String> reportConsumptionCol;
    @FXML private TableColumn<Bollette, String> estimatedCol;

    @FXML private TableView<RichiesteContratto> activationReqTable;
    @FXML private TableColumn<RichiesteContratto, String> reqCreationDateCol;
    @FXML private TableColumn<RichiesteContratto, String> reqUtilityCol;
    @FXML private TableColumn<RichiesteContratto, String> reqResultCol;

    @FXML private ComboBox<Choice<Integer, ContrattiApprovati>> subscriptionChoice;
    @FXML private Button subDetails;
    @FXML private Button consumptionTrend;

    @FXML private TableView<Letture> measurementsTable;
    @FXML private TableColumn<Letture, String> measurementDateCol;
    @FXML private TableColumn<Letture, String> measurementConsumpCol;
    @FXML private TableColumn<Letture, String> measurementStatusCol;
    @FXML private TextField consumption;
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
    @FXML private ComboBox<Choice<Redditi, String>> incomeBracket;

    private UserAreaController(final Stage stage, final DataSource dataSource,  final SessionHolder holder) {
        super(stage, dataSource, holder, FXML_FILE);
    }

    public static Controller create(final Stage stage, final DataSource dataSource,  final SessionHolder holder) {
        return new UserAreaController(stage, dataSource, holder);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        populateSubscriptionBox();
        populateUserDetails();
        initializeReportTable();
        initializeActivationRequestTable();
        initializeMeasurementsTable();
        showFullUsername();
    }

    private void showFullUsername() {
        try (Connection conn = dataSource().getConnection()) {
            final Persone client = Queries.fetchPersonById(getSessionHolder().session().orElseThrow().userId(), conn).orElseThrow();

            Platform.runLater(() -> clientFullName.setText(client.getNome() + " " + client.getCognome()));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void populateUserDetails() {
        try (Connection conn = dataSource().getConnection()) {
            final ClientiDettagliati client = Queries.fetchClientById(
                    getSessionHolder().session().orElseThrow().userId(), conn).orElseThrow();
            final List<Redditi> brackets = Queries.fetchAll(DSL.using(conn, SQLDialect.MYSQL), REDDITI, Redditi.class);
            final List<Choice<Redditi, String>> list = brackets.stream()
                    .map(r -> new ChoiceImpl<>(r, r.getFascia(), (i, v) -> v))
                    .collect(Collectors.toList());

            Platform.runLater(() -> {
                street.setText(client.getVia());
                civic.setText(client.getNumcivico());
                postcode.setText(client.getCap());
                municipality.setText(client.getComune());
                state.setText(client.getProvincia());
                email.setText(client.getEmail());
                phone.setText(client.getNumerotelefono());

                incomeBracket.setItems(FXCollections.observableList(list));
                incomeBracket.setValue(list.get(client.getFasciareddito() - 1));
            });
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void populateSubscriptionBox() {
        List<Choice<Integer, ContrattiApprovati>> subs;
        try (final Connection conn = dataSource().getConnection()) {
            subs = Queries.fetchApprovedSubscriptionsByClient(getSessionHolder().session().orElseThrow().userId(), conn)
                    .stream()
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
        Platform.runLater(() -> consumptionTrend.setDisable(subscriptionChoice.getValue() == null));
    }

    private void toggleMeasurementFields() {
        Platform.runLater(() -> {
            consumption.setDisable(false);
            consumption.setText("");
        });
    }

    private void initializeReportTable() {
        final Choice<Integer, ContrattiApprovati> subChoice = subscriptionChoice.getSelectionModel().getSelectedItem();

        if (subChoice != null) {
            paid.setCellValueFactory(cellData -> {
                Optional<Pagamenti> payment = Optional.empty();

                try (final Connection conn = dataSource().getConnection()) {
                    payment = Queries.fetchReportPayment(cellData.getValue().getNumerobolletta(), conn);
                } catch (SQLException e) {
                    ViewUtils.showError(e.getMessage());
                    e.printStackTrace();
                }
                if (payment.isPresent()) {
                    final LocalDate paymentDate = payment.orElseThrow().getDatapagamento();
                    return new SimpleStringProperty(paymentDate.format(DATE_FORMAT));
                }
                return new SimpleStringProperty("Non pagata");
            });

            String utility = "";
            try (final Connection conn = dataSource().getConnection()) {
                utility = Queries.fetchUtilityFromSubscription(subChoice.getValue().getIdcontratto(), conn).getNome();
            } catch (SQLException e) {
                e.printStackTrace();
                ViewUtils.showError(e.getMessage());
            }
            final String unit = LocaleUtils.getItUtilityUnits().getOrDefault(utility, "");

            Platform.runLater(() -> {
                publishDate.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getDataemissione()
                        .format(DATE_FORMAT)));
                deadline.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getDatascadenza()
                        .format(DATE_FORMAT)));
                amount.setCellValueFactory(cellData -> new SimpleStringProperty(
                        "€" + DECIMAL_FORMAT.format(cellData.getValue().getImporto())));
                estimatedCol.setCellValueFactory(c -> new SimpleStringProperty(
                        StringUtils.byteToYesNo(c.getValue().getStimata())));
                reportConsumptionCol.setCellValueFactory(c ->
                        new SimpleStringProperty(DECIMAL_FORMAT.format(c.getValue().getConsumi()) + " " + unit));
            });

            updateReportTable();
        }
    }

    private void updateReportTable() {
        try (Connection conn = dataSource().getConnection()) {
            List<Bollette> reports = Queries.fetchSubscriptionReports(subscriptionChoice.getValue()
                    .getValue()
                    .getIdcontratto(), conn);

            Platform.runLater(() -> reportTable.setItems(FXCollections.observableList(reports)));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void initializeActivationRequestTable() {
        Platform.runLater(() -> {
            reqCreationDateCol.setCellValueFactory(c -> new SimpleStringProperty(DATE_FORMAT.format(c.getValue().getDataaperturarichiesta())));
            reqResultCol.setCellValueFactory(c ->
                    new SimpleStringProperty(c.getValue().getStatorichiesta()));
        });
        reqUtilityCol.setCellValueFactory(c -> {
            final String utility = Queries.fetchPlanById(c.getValue().getOfferta(), dataSource())
                    .orElseThrow()
                    .getMateriaprima();
            return new SimpleStringProperty(utility);
        });
        refreshActivationRequestTable();
    }

    private void refreshActivationRequestTable() {
        try (Connection conn = dataSource().getConnection()) {
            final List<RichiesteContratto> reqs = Queries.fetchSubscriptionRequestsByClient(getSessionHolder().session().orElseThrow().userId(), conn);

            Platform.runLater(() -> activationReqTable.setItems(FXCollections.observableList(reqs)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void doShowActivationReqDetails() {
        final RichiesteContratto request = activationReqTable.getSelectionModel().getSelectedItem();
        if (request != null) {
            createSubWindow(UserActivationRequestDetailsController.create(null, dataSource(), getSessionHolder(), request));
        } else {
            ViewUtils.showBlockingWarning("Seleziona una richiesta.");
        }
    }

    @FXML
    private void doDeleteActivationReq() {
        final RichiesteContratto selectedReq = activationReqTable.getSelectionModel().getSelectedItem();
        if (selectedReq != null) {
            try (Connection conn = dataSource().getConnection()) {
                if (selectedReq.getDatachiusurarichiesta() == null) {
                    final int result = Queries.deleteSubscriptionRequest(selectedReq.getIdcontratto(), conn);
                    if (result == 1) {
                        ViewUtils.showBlockingWarning("Richiesta eliminata.");
                        refreshActivationRequestTable();
                    } else {
                        ViewUtils.showBlockingWarning(StringUtils.getGenericError());
                    }
                } else {
                    ViewUtils.showBlockingWarning("Non puoi eliminare una richiesta già finalizzata.");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            ViewUtils.showBlockingWarning("Seleziona una richiesta da eliminare.");
        }
    }

    @FXML
    private void doAddMeasurement() {
        final var subChoice = subscriptionChoice.getSelectionModel().getSelectedItem();

        if (subChoice != null) {
            Optional<Cessazioni> approvedEnd = Optional.empty();

            try (final Connection conn = dataSource().getConnection()) {
                approvedEnd = Queries.fetchApprovedEndRequestBySubscription(subChoice.getValue().getIdcontratto(), conn);
                if (approvedEnd.isEmpty()) {
                    if (Checks.isValidConsumption(consumption.getText())) {
                        final String meterId = Queries.fetchMeterBySubscription(subChoice.getValue().getIdcontratto(), conn)
                                .orElseThrow()
                                .getMatricola();
                        final Letture measurement = new Letture(
                                0,
                                meterId,
                                LocalDate.now(),
                                null,
                                StatusType.REVIEWING.toString(),
                                "",
                                new BigDecimal(consumption.getText()),
                                getSessionHolder().session().orElseThrow().userId()
                        );

                        Queries.insertMeasurement(measurement, conn);
                        updateMeasurementsTable();
                    } else {
                        ViewUtils.showBlockingWarning("Controlla di aver inserito correttamente la lettura.");
                    }
                } else {
                    ViewUtils.showBlockingWarning("Il contratto risulta non attivo. Non puoi comunicare nuove letture.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                ViewUtils.showError("Impossibile inserire la lettura: " + e.getMessage());
            }
        } else {
            ViewUtils.showBlockingWarning("Seleziona un contratto.");
        }
    }

    private void initializeMeasurementsTable() {
        String utility = "";
        try (final Connection conn = dataSource().getConnection()) {
            final Choice<Integer, ContrattiApprovati> subChoice = subscriptionChoice.getSelectionModel().getSelectedItem();
            if (subChoice != null) {
                utility = Queries.fetchUtilityFromSubscription(subChoice.getValue().getIdcontratto(), conn)
                        .getNome();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            ViewUtils.showError("Errore nel reperimento delle letture.");
        }

        final String unit = LocaleUtils.getItUtilityUnits()
                .getOrDefault(utility, "");

        Platform.runLater(() -> {
            measurementDateCol.setCellValueFactory(c -> new SimpleStringProperty(DATE_FORMAT.format(c.getValue()
                    .getDataeffettuazione())));
            measurementConsumpCol.setCellValueFactory(c -> new SimpleStringProperty(DECIMAL_FORMAT.format(c.getValue()
                    .getConsumi()) + " " + unit));
            measurementStatusCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStato()));
            updateMeasurementsTable();
        });
    }

    private void updateMeasurementsTable() {
        final Choice<Integer, ContrattiApprovati> subChoice = subscriptionChoice.getSelectionModel().getSelectedItem();

        if (subChoice != null) {
            try (Connection conn = dataSource().getConnection()) {
                List<Letture> measurements = Queries.fetchMeasurements(subChoice.getValue().getIdcontratto(), conn);
                measurementsTable.setItems(FXCollections.observableList(measurements));
            } catch (SQLException e) {
                e.printStackTrace();
                ViewUtils.showError("Errore nel reperimento delle letture.");
            }
        }
    }

    @FXML
    private void showSubDetails() {
        switchTo(UserSubDetailsController.create(stage(), dataSource(), getSessionHolder(), subscriptionChoice.getValue().getValue()));
    }

    @FXML
    private void payReport() {
        final Bollette report = reportTable.getSelectionModel().getSelectedItem();

        if (report == null) {
            ViewUtils.showBlockingWarning("Seleziona una bolletta da pagare.");
        } else {
            try (Connection conn = dataSource().getConnection()) {
                final Optional<Pagamenti> payment = Queries.fetchReportPayment(report.getNumerobolletta(), conn);

                if (payment.isPresent()) {
                    ViewUtils.showBlockingWarning("La bolletta selezionata risulta già pagata.");
                } else {
                    final int outcome = Queries.markReportAsPaid(report.getIdcontratto(), conn);
                    if (outcome == 1) {
                        ViewUtils.showBlockingWarning("Pagamento effettuato con successo.");
                        updateReportTable();
                    } else {
                        ViewUtils.showError("Impossibile procedere al pagamento.");
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                ViewUtils.showError("Impossibile procedere al pagamento.");
            }
        }
    }

    @FXML
    private void goBack() {
        switchTo(HomeController.create(stage(), dataSource(), getSessionHolder()));
    }

    @FXML
    private void doUpdateClient() {
        if (areUserFieldsInvalid()) {
            ViewUtils.showBlockingWarning("Verifica di aver inserito correttamente i nuovi dati.");
        } else {
            final int clientId = getSessionHolder().session().orElseThrow().userId();
            try (Connection conn = dataSource().getConnection()) {
                int resultUpdateData = Queries.updatePerson(
                            email.getText(),
                            postcode.getText(),
                            municipality.getText(),
                            civic.getText(),
                            phone.getText(),
                            state.getText(),
                            street.getText(),
                            clientId,
                            conn);
                resultUpdateData += Queries.updateClientIncome(clientId,
                        incomeBracket.getValue().getItem().getCodreddito(), conn);
                if (resultUpdateData == 2) {
                    ViewUtils.showBlockingWarning("Dati modificati con successo.");
                } else {
                    ViewUtils.showBlockingWarning("Operazione non riuscita.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                ViewUtils.showError(StringUtils.getGenericError());
            }
        }
    }

    @FXML
    private void doUpdatePassword() {
        if (!isAtLeastOnePasswordFieldBlank() && isNewPasswordCorrectlySet()) {
            final int clientId = getSessionHolder().session().orElseThrow().userId();
            try (Connection conn = dataSource().getConnection()) {
                final int resultUpdatePw = Queries.updatePersonPassword(clientId, newPw.getText(), conn);
                if (resultUpdatePw == 1) {
                    ViewUtils.showBlockingWarning("Password modificata con successo.");
                } else {
                    ViewUtils.showBlockingWarning("Impossibile modificare la password.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                ViewUtils.showError(StringUtils.getGenericError());
            }
        } else {
            ViewUtils.showBlockingWarning("Verifica i dati inseriti.");
        }
    }

    @FXML
    private void onSubChosen() {
        initializeReportTable();
        toggleMeasurementFields();
        toggleTrendButton();
        initializeMeasurementsTable();
    }

    @FXML
    private void showConsumptionTrend() {
        switchTo(UserStatsController.create(stage(), dataSource(), getSessionHolder(), subscriptionChoice.getValue().getValue()));
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
                || !Checks.isIntegerNumber(phone.getText());
    }

    private boolean isAtLeastOnePasswordFieldBlank() {
        return currentPw.getText().length() == 0 || newPw.getText().length() == 0 || confirmPw.getText().length() == 0;
    }

    private boolean isNewPasswordCorrectlySet() {
        Persone person = null;
        try (Connection conn = dataSource().getConnection()) {
            person = Queries.fetchPersonById(getSessionHolder().session().orElseThrow().userId(), conn).orElseThrow();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (person == null) {
            throw new IllegalStateException("Fetched client in their own user area should not be null!");
        }
        return currentPw.getText().equals(person.getPassword()) &&
                newPw.getText().length() >= PASSWORD_MIN && newPw.getText().length() <= PASSWORD_MAX &&
                confirmPw.getText().equals(newPw.getText());
    }

    /**
     * Mock implementation.
     */
    @FXML
    private void doDownloadFile() {
        Bollette report = reportTable.getSelectionModel().getSelectedItem();
        if (report == null) {
            ViewUtils.showBlockingWarning("Seleziona una bolletta.");
        } else {
            ViewUtils.showBlockingWarning("File scaricato.");
        }
    }
}
