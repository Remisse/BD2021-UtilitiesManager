package bdproject.controller.gui.users;

import bdproject.controller.gui.AbstractController;
import bdproject.controller.gui.Controller;
import bdproject.model.Queries;
import bdproject.model.SessionHolder;
import bdproject.tables.pojos.ContrattiApprovati;
import bdproject.tables.pojos.Immobili;
import bdproject.utils.ViewUtils;
import bdproject.utils.LocaleUtils;
import javafx.application.Platform;
import javafx.collections.FXCollections;
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
import java.util.*;

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
        final List<Integer> years = new ArrayList<>();
        for (int year = LocalDate.now().getYear(); year >= subscription.getDatachiusurarichiesta().getYear(); year--) {
            years.add(year);
        }

        Platform.runLater(() -> yearSelect.setItems(FXCollections.observableList(years)));
    }

    private void updateAverages() {
        try (final Connection conn = dataSource().getConnection()) {
            final Immobili premise = Queries.fetchPremiseFromSubscription(subscription.getIdcontratto(), dataSource());
            final String utility = Queries.fetchUtilityFromSubscription(subscription.getIdcontratto(), conn);

            final BigDecimal zoneAvgBigDecimal = Queries.avgConsumptionPerZone(premise, utility, startDate.getValue(),
                            endDate.getValue(), conn);
            final BigDecimal userAvgBigDecimal = Queries.avgConsumptionFromSub(subscription.getIdcontratto(),
                    startDate.getValue(), endDate.getValue(), conn);

            Platform.runLater(() -> {
                peopleAvg.setText(zoneAvgBigDecimal.toString());
                yourAvg.setText(userAvgBigDecimal.toString());
            });
        } catch (SQLException e) {
            e.printStackTrace();
            ViewUtils.showError(e.getCause().getMessage());
        }
    }

    @FXML
    private void onDateSelect() {
        if (startDate.getValue() != null && endDate.getValue() != null) {
            updateAverages();
        } else {
            Platform.runLater(() -> {
                peopleAvg.setText("N.D.");
                yourAvg.setText("N.D.");
            });
        }
    }

    @FXML
    private void onYearSelect() {
        if (!yearSelect.getSelectionModel().isEmpty()) {
            final int selectedYear = yearSelect.getSelectionModel().getSelectedItem();
            Map<LocalDate, BigDecimal> reports = Map.of();

            try (Connection conn = dataSource().getConnection()) {
                reports = Queries.fetchReportsWithConsumptionInYear(subscription.getIdcontratto(), selectedYear, conn);
            } catch (SQLException e) {
                e.printStackTrace();
            }

            final DateTimeFormatter month_it = LocaleUtils.getItLongMonthFormatter();
            final XYChart.Series<String, BigDecimal> series = new XYChart.Series<>();
            reports.forEach((date, cons) -> series.getData().add(new XYChart.Data<>(date.format(month_it), cons)));

            Platform.runLater(() -> {
                yearlyTrend.getData().clear();
                yearlyTrend.getData().add(series);
            });
        } else {
            ViewUtils.showBlockingWarning("Seleziona un anno dal menu a tendina.");
        }
    }

    @FXML
    private void goBack() {
        switchTo(UserAreaController.create(stage(), dataSource(), getSessionHolder()));
    }
}
