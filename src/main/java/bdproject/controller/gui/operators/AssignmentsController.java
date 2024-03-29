package bdproject.controller.gui.operators;

import bdproject.controller.gui.AbstractController;
import bdproject.controller.gui.Controller;
import bdproject.model.Queries;
import bdproject.model.SessionHolder;
import bdproject.model.types.StatusType;
import bdproject.tables.pojos.*;
import bdproject.utils.LocaleUtils;
import bdproject.utils.ViewUtils;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
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
import java.time.format.DateTimeFormatter;
import java.util.*;

import static bdproject.tables.Cessazioni.CESSAZIONI;
import static bdproject.tables.Letture.LETTURE;
import static bdproject.tables.OperatoriCessazioni.OPERATORI_CESSAZIONI;
import static bdproject.tables.OperatoriContratti.OPERATORI_CONTRATTI;
import static bdproject.tables.OperatoriLetture.OPERATORI_LETTURE;
import static bdproject.tables.RichiesteContratto.RICHIESTE_CONTRATTO;

public class AssignmentsController extends AbstractController implements Initializable {

    private static final String FXML_FILE = "assignments.fxml";
    private static final DateTimeFormatter DATE_IT = LocaleUtils.getItDateFormatter();
    private static final String FAILED_ERR = "Impossibile completare la richiesta.";
    private static final String SUCCESS_MSG = "Presa in carico.";

    private List<OperatoriCessazioni> endAssignments;
    private List<OperatoriContratti> subAssignments;
    private List<OperatoriLetture> measurementAssignments;

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
        List<RichiesteContratto> subs = Collections.emptyList();
        List<Cessazioni> ends = Collections.emptyList();
        List<Letture> measurements = Collections.emptyList();

        try (final Connection conn = dataSource().getConnection()) {
            final DSLContext ctx = Queries.createContext(conn);

            endAssignments = Queries.fetchAll(ctx, OPERATORI_CESSAZIONI, OperatoriCessazioni.class);
            subAssignments = Queries.fetchAll(ctx, OPERATORI_CONTRATTI, OperatoriContratti.class);
            measurementAssignments = Queries.fetchAll(ctx, OPERATORI_LETTURE, OperatoriLetture.class);

            subs = Queries.fetchAll(ctx, RICHIESTE_CONTRATTO, RichiesteContratto.class);
            ends = Queries.fetchAll(ctx, CESSAZIONI, Cessazioni.class);
            measurements = Queries.fetchAll(ctx, LETTURE, Letture.class);
        } catch (SQLException e) {
            e.printStackTrace();
            ViewUtils.showError(e.getMessage());
        }

        initializeSubTable(subs);
        initializeEndTable(ends);
        initializeMeasurementsTable(measurements);
    }

    private void initializeSubTable(final List<RichiesteContratto> subs) {
        subIdCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getIdcontratto().toString()));
        subStatusCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStatorichiesta()));
        subCreationDateCol.setCellValueFactory(c -> new SimpleStringProperty(DATE_IT.format(c.getValue()
                .getDataaperturarichiesta())));
        subCompletionDateCol.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getDatachiusurarichiesta() == null ? "N.D." :
                        DATE_IT.format(c.getValue().getDatachiusurarichiesta())));
        subEmployeeCol.setCellValueFactory(c -> {
            final Optional<OperatoriContratti> op = subAssignments.stream()
                    .filter(i -> Objects.equals(i.getNumerorichiesta(), c.getValue().getIdcontratto()))
                    .findFirst();
            return op.map(i -> new SimpleStringProperty(i.getIdoperatore().toString()))
                    .orElseGet(() -> new SimpleStringProperty("Non assegnato"));
        });
        subTable.setItems(FXCollections.observableList(subs));
    }

    private void refreshSubTable() {
        try (final Connection conn = dataSource().getConnection()) {
            subAssignments = Queries.fetchAll(DSL.using(conn, SQLDialect.MYSQL), OPERATORI_CONTRATTI,
                    OperatoriContratti.class);
            subTable.refresh();
        } catch (SQLException e) {
            e.printStackTrace();
            ViewUtils.showError(e.getMessage());
        }

    }

    private void initializeEndTable(final List<Cessazioni> ends) {
        endIdCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getIdcontratto().toString()));
        endStatusCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStatorichiesta()));
        endCreationDateCol.setCellValueFactory(c -> new SimpleStringProperty(DATE_IT.format(c.getValue()
                .getDataaperturarichiesta())));
        endCompletionDateCol.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getDatachiusurarichiesta() == null ? "N.D." :
                        DATE_IT.format(c.getValue().getDatachiusurarichiesta())));
        endEmployeeCol.setCellValueFactory(c -> {
            final Optional<OperatoriCessazioni> op = endAssignments.stream()
                    .filter(i -> Objects.equals(i.getNumerorichiesta(), c.getValue().getIdcontratto()))
                    .findFirst();
            return op.map(i -> new SimpleStringProperty(i.getIdoperatore().toString()))
                    .orElseGet(() -> new SimpleStringProperty("Non assegnato"));
        });

        endTable.setItems(FXCollections.observableList(ends));
    }

    private void refreshEndTable() {
        try (final Connection conn = dataSource().getConnection()) {
            endAssignments = Queries.fetchAll(DSL.using(conn, SQLDialect.MYSQL), OPERATORI_CESSAZIONI,
                    OperatoriCessazioni.class);
            endTable.refresh();
        } catch (SQLException e) {
            e.printStackTrace();
            ViewUtils.showError(e.getMessage());
        }
    }

    private void initializeMeasurementsTable(final List<Letture> measurements) {
        measurementNumberCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNumerolettura().toString()));
        measurementStatusCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStato()));
        measurementEmployeeCol.setCellValueFactory(c -> {
            final Optional<OperatoriLetture> op = measurementAssignments.stream()
                    .filter(i -> Objects.equals(i.getLettura(), c.getValue().getNumerolettura()))
                    .findFirst();
            return op.map(i -> new SimpleStringProperty(i.getIdoperatore().toString()))
                    .orElseGet(() -> new SimpleStringProperty("Non assegnato"));
        });

        measurementTable.setItems(FXCollections.observableList(measurements));
    }

    private void refreshMeasurementTable() {
        try (final Connection conn = dataSource().getConnection()) {
            measurementAssignments = Queries.fetchAll(DSL.using(conn, SQLDialect.MYSQL), OPERATORI_LETTURE,
                    OperatoriLetture.class);
            measurementTable.refresh();
        } catch (SQLException e) {
            e.printStackTrace();
            ViewUtils.showError(e.getMessage());
        }
    }

    @FXML
    private void assignSub() {
        final int opId = getSessionHolder().session().orElseThrow().userId();
        final RichiesteContratto sub = subTable.getSelectionModel().getSelectedItem();

        if (sub != null && sub.getStatorichiesta().equals(StatusType.REVIEWING.toString())) {
            try (final Connection conn = dataSource().getConnection()) {
                final int result = Queries.insertSubRequestAssignment(opId, sub.getIdcontratto(), conn);

                if (result == 1) {
                    refreshSubTable();
                    ViewUtils.showBlockingWarning(SUCCESS_MSG);
                } else {
                    ViewUtils.showBlockingWarning(FAILED_ERR);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                ViewUtils.showError(e.getMessage());
            }
        } else {
            ViewUtils.showBlockingWarning("Seleziona una richiesta di contratto non ancora finalizzata.");
        }
    }

    @FXML
    private void assignMeasurement() {
        final int opId = getSessionHolder().session().orElseThrow().userId();
        final Letture measurement = measurementTable.getSelectionModel().getSelectedItem();

        if (measurement != null && measurement.getStato().equals(StatusType.REVIEWING.toString())) {
            try (final Connection conn = dataSource().getConnection()) {
                final int result = Queries.insertMeasurementAssignment(opId, measurement.getNumerolettura(), conn);

                if (result == 1) {
                    refreshMeasurementTable();
                    ViewUtils.showBlockingWarning(SUCCESS_MSG);
                } else {
                    ViewUtils.showBlockingWarning(FAILED_ERR);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                ViewUtils.showError(e.getMessage());
            }
        } else {
            ViewUtils.showBlockingWarning("Seleziona una lettura non ancora finalizzata.");
        }
    }

    @FXML
    private void assignEnd() {
        final int opId = getSessionHolder().session().orElseThrow().userId();
        final Cessazioni end = endTable.getSelectionModel().getSelectedItem();

        if (end != null && end.getStatorichiesta().equals(StatusType.REVIEWING.toString())) {
            try (final Connection conn = dataSource().getConnection()) {
                final int result = Queries.insertEndRequestAssignment(opId, end.getNumerorichiesta(), conn);

                if (result == 1) {
                    refreshEndTable();
                    ViewUtils.showBlockingWarning(SUCCESS_MSG);
                } else {
                    ViewUtils.showBlockingWarning(FAILED_ERR);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                ViewUtils.showError(e.getMessage());
            }
        } else {
            ViewUtils.showBlockingWarning("Seleziona una richiesta di cessazione non ancora finalizzata.");
        }
    }

    @FXML
    private void goBack() {
        switchTo(AreaSelectorController.create(stage(), dataSource(), getSessionHolder()));
    }
}
