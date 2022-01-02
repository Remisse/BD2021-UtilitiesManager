package bdproject.controller.gui.admin;

import bdproject.controller.gui.AbstractController;
import bdproject.controller.gui.Controller;
import bdproject.model.Queries;
import bdproject.model.SessionHolder;
import bdproject.tables.Letture;
import bdproject.tables.pojos.*;
import bdproject.utils.LocaleUtils;
import bdproject.utils.ViewUtils;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import javax.sql.DataSource;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.DateFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.Function;

import static bdproject.tables.OperatoriCessazioni.OPERATORI_CESSAZIONI;
import static bdproject.tables.OperatoriContratti.OPERATORI_CONTRATTI;
import static bdproject.tables.OperatoriLetture.OPERATORI_LETTURE;

public class AssignmentsController extends AbstractController implements Initializable {

    private static final String FXML_FILE = "assignments.fxml";
    private static final DateTimeFormatter DATE_IT = LocaleUtils.getItDateFormatter();

    private List<OperatoriCessazioni> endAssignments;
    private List<OperatoriContratti> subAssignments;
    private List<OperatoriLetture> measurementAssignments;

    @FXML private CheckBox hideCompleted;

    @FXML private TableView<Operatori> employeeTable;
    @FXML private TableColumn<Operatori, String> employeeIdCol;
    @FXML private TableColumn<Operatori, String> employeeTypeCol;

    @FXML private TableView<RichiesteContratto> subTable;
    @FXML private TableColumn<RichiesteContratto, String> subIdCol;
    @FXML private TableColumn<RichiesteContratto, String> subEmployeeCol;
    @FXML private TableColumn<RichiesteContratto, String> subStatusCol;
    @FXML private TableColumn<RichiesteContratto, String> subCreationDateCol;
    @FXML private TableColumn<RichiesteContratto, String> subCompletionDateCol;

    @FXML private TableView<Cessazioni> endTable;
    @FXML private TableColumn<Cessazioni, String> endIdCol;
    @FXML private TableColumn<Cessazioni, String> endEmployeeCol;
    @FXML private TableColumn<Cessazioni, String> endStatusCol;
    @FXML private TableColumn<Cessazioni, String> endCreationDateCol;
    @FXML private TableColumn<Cessazioni, String> endCompletionDateCol;

    @FXML private TableView<Letture> measurementTable;
    @FXML private TableColumn<Letture, String> measurementNumberCol;
    @FXML private TableColumn<Letture, String> measurementEmployeeCol;
    @FXML private TableColumn<Letture, String> measurementStatusCol;

    private AssignmentsController(final Stage stage, final DataSource dataSource, final SessionHolder holder) {
        super(stage, dataSource, holder, FXML_FILE);
    }

    public static Controller create(final Stage stage, final DataSource dataSource, final SessionHolder holder) {
        return new AssignmentsController(stage, dataSource, holder);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try (final Connection conn = dataSource().getConnection()) {
            endAssignments = Queries.fetchAll(DSL.using(conn, SQLDialect.MYSQL), OPERATORI_CESSAZIONI,
                    OperatoriCessazioni.class);
            subAssignments = Queries.fetchAll(DSL.using(conn, SQLDialect.MYSQL), OPERATORI_CONTRATTI,
                    OperatoriContratti.class);
            measurementAssignments = Queries.fetchAll(DSL.using(conn, SQLDialect.MYSQL), OPERATORI_LETTURE,
                    OperatoriLetture.class);
        } catch (SQLException e) {
            e.printStackTrace();
            ViewUtils.showError(e.getMessage());
        }

        initializeEmployeeTable();
        initializeSubTable();
    }

    private void initializeEmployeeTable() {
        Platform.runLater(() -> {
            employeeIdCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getIdoperatore().toString()));
            employeeTypeCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTipo()));
        });
    }

    private void initializeSubTable() {
        Platform.runLater(() -> {
            subIdCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getIdcontratto().toString()));
            subEmployeeCol.setCellValueFactory(c -> {
                final Optional<OperatoriCessazioni> op = endAssignments.stream()
                        .filter(i -> Objects.equals(i.getNumerorichiesta(), c.getValue().getIdcontratto()))
                        .findFirst();
                return op.map(i -> new SimpleStringProperty(i.getIdoperatore().toString()))
                        .orElseGet(() -> new SimpleStringProperty("Non assegnato"));
            });
            subStatusCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStatorichiesta()));
            subCreationDateCol.setCellValueFactory(c -> new SimpleStringProperty(DATE_IT.format(c.getValue()
                    .getDataaperturarichiesta())));
            subCompletionDateCol.setCellValueFactory(c -> new SimpleStringProperty(
                    c.getValue().getDatachiusurarichiesta() == null ? "N.D." :
                            DATE_IT.format(c.getValue().getDatachiusurarichiesta())));
        });
    }

    @FXML
    private void doHideCompleted() {

    }

    @FXML
    private void assignSub() {

    }

    @FXML
    private void assignMeasurement() {

    }

    @FXML
    private void assignEnd() {

    }

    @FXML
    private void goBack() {
        switchTo(AreaSelectorController.create(stage(), dataSource(), getSessionHolder()));
    }
}
