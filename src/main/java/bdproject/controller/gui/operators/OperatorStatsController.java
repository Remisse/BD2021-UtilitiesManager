package bdproject.controller.gui.operators;

import bdproject.controller.gui.AbstractController;
import bdproject.controller.gui.Controller;
import bdproject.model.Queries;
import bdproject.model.SessionHolder;
import bdproject.tables.pojos.Offerte;
import bdproject.utils.ViewUtils;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.jooq.Record3;

import javax.sql.DataSource;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class OperatorStatsController extends AbstractController implements Initializable {

    private static final String FXML_FILE = "operatorStats.fxml";

    @FXML private Label subRequestsByOperatorLabel;
    @FXML private Label endRequestsByOperatorLabel;
    @FXML private Label subsInYearCountLabel;
    @FXML private Label mostRequestedPlanLabel;
    @FXML private ComboBox<Integer> yearComboBox;
    @FXML private ComboBox<String> utilityComboBox;

    private OperatorStatsController(Stage stage, DataSource dataSource, SessionHolder holder) {
        super(stage, dataSource, holder, FXML_FILE);
    }

    public static Controller create(final Stage stage, final DataSource dataSource, final SessionHolder holder) {
        return new OperatorStatsController(stage, dataSource, holder);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        final int operatorId = getSessionHolder().session().orElseThrow().userId();

        final List<Integer> years = new ArrayList<>();
        for (int year = LocalDate.now().getYear(); year >= LocalDate.now().getYear() - 2; year--) {
            years.add(year);
        }
        yearComboBox.setItems(FXCollections.observableList(years));

        try (final Connection conn = dataSource().getConnection()) {
            final List<String> utilities = Queries.fetchAllUtilities(conn);
            utilityComboBox.setItems(FXCollections.observableList(utilities));

            subRequestsByOperatorLabel.setText(String.valueOf(Queries.countSubRequestsClosedByOperator(operatorId, conn)));
            endRequestsByOperatorLabel.setText(String.valueOf(Queries.countEndRequestsClosedByOperator(operatorId, conn)));
        } catch (SQLException e) {
            e.printStackTrace();
            ViewUtils.showError(e.getMessage());
        }
    }

    @FXML
    private void onYearSelect() {
        try (final Connection conn = dataSource().getConnection()) {
            refreshSubsInYearLabel(conn);
        } catch (SQLException e) {
            e.printStackTrace();
            ViewUtils.showError(e.getMessage());
        }
    }

    private void refreshSubsInYearLabel(final Connection conn) {
        subsInYearCountLabel.setText(String.valueOf(Queries.countSubscriptionsActivatedInYear(yearComboBox.getSelectionModel()
                .getSelectedItem(), conn)));
    }

    @FXML
    private void onUtilitySelect() {
        try (final Connection conn = dataSource().getConnection()) {
            refreshMostRequestedPlanLabel(conn);
        } catch (SQLException e) {
            e.printStackTrace();
            ViewUtils.showError(e.getMessage());
        }
    }

    private void refreshMostRequestedPlanLabel(final Connection conn) {
        final Record3<Integer, String, Integer> plan = Queries.fetchMostRequestedPlan(utilityComboBox.getSelectionModel()
                .getSelectedItem(), conn);
        mostRequestedPlanLabel.setText(plan.component2() + " (codice: " + plan.component1() +
                "; numero di contratti: " + plan.component3() + ")");
    }

    @FXML
    private void goBack() {
        switchTo(AreaSelectorController.create(stage(), dataSource(), getSessionHolder()));
    }
}
