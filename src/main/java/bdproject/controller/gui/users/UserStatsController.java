package bdproject.controller.gui.users;

import bdproject.controller.Checks;
import bdproject.controller.gui.AbstractController;
import bdproject.controller.gui.Controller;
import bdproject.model.Queries;
import bdproject.model.SessionHolder;
import bdproject.tables.pojos.Bollette;
import bdproject.tables.pojos.ContrattiApprovati;
import bdproject.tables.pojos.Immobili;
import bdproject.utils.FXUtils;
import bdproject.utils.LocaleUtils;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class UserStatsController extends AbstractController implements Initializable {

    private static final String FXML_FILE = "consumptionTrend.fxml";
    private final ContrattiApprovati subscription;

    @FXML private Button back;
    @FXML private LineChart<String, BigDecimal> yearlyTrend;
    @FXML private ComboBox<Integer> yearSelect;
    @FXML private Label yourAvg;
    @FXML private Label peopleAvg;
    @FXML private DatePicker startDate;
    @FXML private DatePicker endDate;

    private UserStatsController(final Stage stage, final DataSource dataSource, final SessionHolder holder,
            final ContrattiApprovati subscription) {
        super(stage, dataSource, holder, FXML_FILE);
        this.subscription = subscription;
    }

    public static Controller create(final Stage stage, final DataSource dataSource, final SessionHolder holder,
            final ContrattiApprovati subscription) {
        return new UserStatsController(stage, dataSource, holder, subscription);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        populateYearSelection();

    }

    private void populateYearSelection() {
        if (Checks.isSubscriptionActive(subscription)) {
            final List<Integer> years = new ArrayList<>();
            for (int year = LocalDate.now().getYear(); year >= subscription.getDatachiusurarichiesta().getYear(); year--) {
                years.add(year);
            }
            yearSelect.setItems(FXCollections.observableList(years));
        }
    }

    private void updateAverages(final Connection conn) {
        if (startDate.getValue() != null && endDate.getValue() != null) {
            final Immobili premise = Queries.fetchPremiseFromSubscription(subscription.getIdcontratto(), dataSource());
            final String utility = Queries.fetchUtilityFromSubscription(subscription.getIdcontratto(), conn).getNome();

            peopleAvg.setText(Queries.avgConsumptionPerZone(
                    premise,
                    utility,
                    startDate.getValue(),
                    endDate.getValue(),
                    conn).toString());
            yourAvg.setText(Queries.avgConsumptionFromSub(subscription.getIdcontratto(), startDate.getValue(), endDate.getValue(), conn)
                    .toString());
        } else {
            peopleAvg.setText("N.D.");
            yourAvg.setText("N.D.");
        }
    }

    @FXML
    private void onDateSelect(ActionEvent event) {
        try (Connection conn = dataSource().getConnection()) {
            updateAverages(conn);
        } catch (SQLException e) {
            e.printStackTrace();
            FXUtils.showError(e.getSQLState());
        }
    }

    @FXML
    private void onYearSelect() {
        final DateTimeFormatter month_it = LocaleUtils.getItLongMonthFormatter();
        try (Connection conn = dataSource().getConnection()) {
            final var reports = Queries.fetchSubscriptionReports(subscription, conn);
            XYChart.Series<String, BigDecimal> series = new XYChart.Series<>();
            for (Bollette report : reports) {
                series.getData().add(new XYChart.Data<>(
                        report.getDataemissione().format(month_it),
                        report.getConsumi()));
            }
            yearlyTrend.getData().clear();
            yearlyTrend.getData().add(series);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void goBack() {
        switchTo(UserAreaController.create(stage(), dataSource(), getSessionHolder()));
    }
}
