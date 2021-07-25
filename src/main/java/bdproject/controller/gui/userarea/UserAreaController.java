package bdproject.controller.gui.userarea;

import bdproject.controller.Choice;
import bdproject.controller.ChoiceImpl;
import bdproject.controller.gui.AbstractViewController;
import bdproject.controller.gui.HomeController;
import bdproject.controller.gui.ViewController;
import bdproject.controller.Checks;
import bdproject.model.Queries;
import bdproject.tables.pojos.*;
import bdproject.utils.FXUtils;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
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
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static bdproject.Tables.BOLLETTE;
import static bdproject.Tables.CONTRATTI;
import static bdproject.Tables.TIPOLOGIE_USO;

public class UserAreaController extends AbstractViewController implements Initializable {

    private static final String FXML_FILE = "userArea.fxml";
    private static final int POSTCODE_LIMIT = 5;
    private static final int PASSWORD_MAX = 30;
    private static final int PASSWORD_MIN = 8;
    private final Map<String, String> measurementUnit = Map.of(
            "Luce", "kWh",
            "Gas", "Smc",
            "Acqua", "mc"
    );
    private final DateTimeFormatter df_it = DateTimeFormatter.ofPattern("MM/dd/yyyy").withLocale(Locale.ITALIAN);
    private final DecimalFormat decimal = new DecimalFormat("#,###.000");
    @FXML
    private Label fullName;
    @FXML
    private TableView<Bollette> reportTable;
    @FXML
    private TableColumn<Bollette, String> publishDate;
    @FXML
    private TableColumn<Bollette, String> deadline;
    @FXML
    private TableColumn<Bollette, String> paid;
    @FXML
    private TableColumn<Bollette, String> amount;
    @FXML
    private TableColumn<Bollette, String> consumed;
    @FXML
    private TableColumn<Bollette, String> activation;
    @FXML
    private TableColumn<Bollette, String> partialRAI;
    @FXML
    private ComboBox<Choice<Integer, Contratti>> subscriptionChoice;
    @FXML
    private Button subDetails;
    @FXML
    private Button consumptionTrend;
    @FXML
    private Button payReport;
    @FXML
    private TextField f1;
    @FXML
    private TextField f2;
    @FXML
    private TextField f3;
    @FXML
    private TextFlow lastReading;
    @FXML
    private Button addMeasurement;
    @FXML
    private TextField street;
    @FXML
    private TextField civic;
    @FXML
    private TextField postcode;
    @FXML
    private TextField municipality;
    @FXML
    private TextField state;
    @FXML
    private TextField email;
    @FXML
    private TextField phone;
    @FXML
    private PasswordField currentPw;
    @FXML
    private PasswordField newPw;
    @FXML
    private PasswordField confirmPw;
    @FXML
    private ComboBox<String> incomeBracket;
    @FXML
    private Button back;
    @FXML
    private Button save;

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
        showLastMeasurement();
        showFullUsername();
    }

    private void showFullUsername() {
        try (Connection conn = getDataSource().getConnection()) {
            final PersoneFisiche client = Queries.getClientRecordFromSession(conn);
            fullName.setText(client.getNome() + " " + client.getCognome());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void populateUserDetails() {
        try (Connection conn = getDataSource().getConnection()) {
            final PersoneFisiche client = Queries.getClientRecordFromSession(conn);

            street.setText(client.getVia());
            civic.setText(client.getNumcivico());
            postcode.setText(String.valueOf(client.getCap()));
            municipality.setText(client.getComune());
            state.setText(client.getProvincia());
            email.setText(client.getEmail());
            phone.setText(client.getNumerotelefono());
            incomeBracket.setItems(FXCollections.observableList(Queries.getIncomeBrackets(conn)));
            incomeBracket.setValue(client.getFasciareddito());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void populateSubscriptionBox() {
        List<Choice<Integer, Contratti>> subs = null;
        try (Connection conn = getDataSource().getConnection()) {
            subs = Queries.getUserSubscriptions(Queries.getClientRecordFromSession(conn), conn)
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
        consumptionTrend.setDisable(!(subscriptionChoice.getValue() != null
                && subscriptionChoice.getValue().getValue().getDatainizio() != null));
    }

    private void toggleMeasurementFields() {
        f1.setDisable(false);
        f1.setText("");
        f2.setText("");
        f3.setText("");
        try (Connection conn = getDataSource().getConnection()) {
            final boolean showF2F3 = Queries.getUtility(subscriptionChoice.getValue().getValue(), conn).getNome().equals("Luce");
            f2.setVisible(showF2F3);
            f3.setVisible(showF2F3);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private TipologieUso getUse(final Contratti currentSub) {
        TipologieUso use = null;
        try (Connection conn = getDataSource().getConnection()) {
            DSLContext query = DSL.using(conn, SQLDialect.MYSQL);
            use = query.select(TIPOLOGIE_USO.asterisk())
                    .from(TIPOLOGIE_USO, CONTRATTI)
                    .where(CONTRATTI.IDCONTRATTO.eq(currentSub.getIdcontratto()))
                    .and(TIPOLOGIE_USO.NOME.eq(CONTRATTI.TIPOUSO))
                    .fetchOneInto(TipologieUso.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return use;
    }

    private void initializeReportTable() {
        try (Connection conn = getDataSource().getConnection()) {
            final PersoneFisiche client = Queries.getClientRecordFromSession(conn);
            if (!Queries.getUserSubscriptions(client, conn).isEmpty()) {
                publishDate.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getDataemissione().format(df_it)));
                deadline.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getDatascadenza().format(df_it)));
                paid.setCellValueFactory(cellData -> {
                    final LocalDate paidDate = cellData.getValue().getDatapagamento();
                    return new SimpleStringProperty(paidDate != null ? paidDate.format(df_it) : "No");
                });
                amount.setCellValueFactory(cellData -> new SimpleStringProperty(
                        "€" + NumberFormat.getCurrencyInstance().format(cellData.getValue().getImporto())));

                final Contratti currentSub = subscriptionChoice.getValue().getValue();
                final String utility = Queries.getUtility(currentSub, conn).getNome();
                consumed.setCellValueFactory(cellData -> {
                    final Bollette report = cellData.getValue();
                    return new SimpleStringProperty(report.getConsumi() +
                            measurementUnit.get(utility) +
                            (report.getStimata() == (byte) 0 ? "" : " (stima)"));
                });
                activation.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getCostoattivazione().toString()));
                partialRAI.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getParzialecanonerai().toString()));

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

    @FXML
    private void doAddMeasurement() {
        try (Connection conn = getDataSource().getConnection()) {
            if (Checks.isSubscriptionActive(subscriptionChoice.getValue().getValue(), conn)) {
                if (Checks.isValidMeasurement(f1, f2, f3)) {
                    var measurement = new Letture(
                            BigDecimal.valueOf(Long.parseLong(f1.getText())),
                            f2.isVisible() ? BigDecimal.valueOf(Long.parseLong(f2.getText())) : BigDecimal.ZERO,
                            f3.isVisible() ? BigDecimal.valueOf(Long.parseLong(f3.getText())) : BigDecimal.ZERO,
                            reportTable.getSelectionModel().getSelectedItem().getIdcontratto(),
                            LocalDate.now(),
                            (byte) 0
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
    }

    private void showLastMeasurement() {
        final var subChoice = subscriptionChoice.getSelectionModel().getSelectedItem();
        if (subChoice != null) {
            try (Connection conn = getDataSource().getConnection()) {
                Optional<Letture> lastMeasurement = Queries.getLastMeasurement(subChoice.getValue(), conn);
                lastMeasurement.ifPresentOrElse(m -> {
                    final Text text = new Text("F1: " + m.getFascia1()
                            + "\nF2: " + m.getFascia2()
                            + "\nF3: " + m.getFascia3()
                            + "\nData: " + m.getDataeffettuazione().format(df_it)
                            + "\nConfermata: " + (m.getConfermata() == 1 ? "Sì" : "No"));
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
        switchTo(SubscriptionDetailsController.create(getStage(), getDataSource(), subscriptionChoice.getValue().getValue()));
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
                DSLContext query = DSL.using(conn, SQLDialect.MYSQL);
                query.update(BOLLETTE)
                        .set(BOLLETTE.DATAPAGAMENTO, LocalDate.now())
                        .where(BOLLETTE.IDCONTRATTO.eq(report.getIdcontratto()))
                        .and(BOLLETTE.DATAEMISSIONE.eq(report.getDataemissione()))
                        .execute();
                FXUtils.showBlockingWarning("Pagamento effettuato con successo.");
                updateTableItems();
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
    private void saveChanges() {
        if (areFieldsInvalid()) {
            FXUtils.showBlockingWarning("Verifica di aver inserito correttamente i nuovi dati.");
        } else {
            try (Connection conn = getDataSource().getConnection()) {
                final PersoneFisiche client = Queries.getClientRecordFromSession(conn);
                int successful;
                if (email.getText().equals(client.getEmail())) {
                    successful = Queries.updateClientNoEmail(client, conn);
                } else {
                    successful = Queries.updateClientAndEmail(client, conn);
                }
                if (successful == 1) {
                    FXUtils.showBlockingWarning("Dati modificati con successo.");
                } else {
                    FXUtils.showBlockingWarning("Impossibile modificare i dati.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                FXUtils.showError("Impossibile aggiornare i dati.");
            }
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

    private boolean areFieldsInvalid() {
        return street.getText().length() == 0
                || civic.getText().length() == 0
                || municipality.getText().length() == 0
                || postcode.getText().length() != POSTCODE_LIMIT
                || state.getText().length() != 2
                || phone.getText().length() == 0
                || email.getText().length() == 0
                || !EmailValidator.getInstance().isValid(email.getText())
                || phone.getText().length() == 0
                || !Checks.isNumber(phone.getText())
                || !isPasswordValid();
    }

    private boolean isPasswordValid() {
        PersoneFisiche client = null;
        try (Connection conn = getDataSource().getConnection()) {
            client = Queries.getClientRecordFromSession(conn);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        assert client != null;
        final boolean allEmpty = currentPw.getText().length() == 0 && newPw.getText().length() == 0 && confirmPw.getText().length() == 0;
        final boolean allValid = currentPw.getText().equals(client.getPassword()) && newPw.getText().length() > PASSWORD_MIN && newPw.getText().length() < PASSWORD_MAX
                && confirmPw.getText().equals(newPw.getText());
        return allEmpty || allValid;
    }
}
