package bdproject.controller.gui.userarea;

import bdproject.controller.gui.AbstractViewController;
import bdproject.controller.gui.ViewController;
import bdproject.model.Queries;
import bdproject.tables.pojos.ContrattiDettagliati;
import bdproject.tables.pojos.Bollette;
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

public class ConsumptionStatsController extends AbstractViewController implements Initializable {

    private static final String FXML_FILE = "consumptionTrend.fxml";
    private final ContrattiDettagliati subscription;
    @FXML
    private Button back;
    @FXML
    private LineChart<String, BigDecimal> yearlyTrend;
    @FXML
    private ComboBox<Integer> yearSelect;
    @FXML
    private Label yourAvg;
    @FXML
    private Label peopleAvg;
    @FXML
    private DatePicker startDate;
    @FXML
    private DatePicker endDate;

    private ConsumptionStatsController(final Stage stage, final DataSource dataSource,
            final ContrattiDettagliati subscription) {
        super(stage, dataSource, FXML_FILE);
        this.subscription = subscription;
    }

    public static ViewController create(final Stage stage, final DataSource dataSource,
            final ContrattiDettagliati subscription) {
        return new ConsumptionStatsController(stage, dataSource, subscription);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        populateYearSelection();

    }

    private void populateYearSelection() {
        if (subscription.getDatainizio() != null) {
            final List<Integer> years = new ArrayList<>();
            for (int year = LocalDate.now().getYear(); year >= subscription.getDatainizio().getYear(); year--) {
                years.add(year);
            }
            yearSelect.setItems(FXCollections.observableList(years));
        }
    }

    private void updateAverages(final Connection conn) {
        if (startDate.getValue() != null && endDate.getValue() != null) {
            final Immobili premises = Queries.fetchPremisesFromSubscription(subscription, getDataSource());
            final String utility = Queries.fetchUtilityFromSubscription(subscription, conn).getNome();
            peopleAvg.setText(Queries.avgConsumptionPerZone(
                    premises,
                    utility,
                    startDate.getValue(),
                    endDate.getValue(),
                    conn).toString());
            yourAvg.setText(Queries.avgConsumptionFromSub(subscription, startDate.getValue(), endDate.getValue(), conn)
                    .toString());
        } else {
            peopleAvg.setText("N.D.");
            yourAvg.setText("N.D.");
        }
    }

    @FXML
    private void onDateSelect(ActionEvent event) {
        try (Connection conn = getDataSource().getConnection()) {
            updateAverages(conn);
        } catch (SQLException e) {
            e.printStackTrace();
            FXUtils.showError(e.getSQLState());
        }
    }

    @FXML
    private void onYearSelect() {
        final DateTimeFormatter month_it = LocaleUtils.getItLongMonthFormatter();
        try (Connection conn = getDataSource().getConnection()) {
            final var reports = Queries.fetchSubscriptionReports(subscription, conn);
            XYChart.Series<String, BigDecimal> series = new XYChart.Series<>();
            for (Bollette report : reports) {
                if (report.getImporto() != null) {
                    series.getData().add(new XYChart.Data<>(
                            report.getDataemissione().format(month_it),
                            report.getImporto()));
                }
            }
            yearlyTrend.getData().clear();
            yearlyTrend.getData().add(series);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void goBack() {
        switchTo(UserAreaController.create(getStage(), getDataSource()));
    }
}
