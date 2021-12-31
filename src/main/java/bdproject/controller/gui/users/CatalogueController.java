package bdproject.controller.gui.users;

import bdproject.controller.Choice;
import bdproject.controller.ChoiceImpl;
import bdproject.controller.gui.AbstractController;
import bdproject.controller.gui.HomeController;
import bdproject.controller.gui.Controller;
import bdproject.controller.gui.users.parametersselection.ParametersClientChangeController;
import bdproject.controller.gui.users.parametersselection.ParametersNewActivationController;
import bdproject.controller.gui.users.parametersselection.ParametersSubentroController;
import bdproject.model.Queries;
import bdproject.model.SessionHolder;
import bdproject.model.SubscriptionProcess;
import bdproject.model.SubscriptionProcessImpl;
import bdproject.tables.pojos.Offerte;
import bdproject.tables.pojos.TipiAttivazione;
import bdproject.tables.pojos.TipologieUso;
import bdproject.utils.FXUtils;
import bdproject.utils.LocaleUtils;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import javax.sql.DataSource;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import static bdproject.Tables.*;

public class CatalogueController extends AbstractController implements Initializable {

    private static final String FXML_FILE = "catalogue.fxml";
    private SubscriptionProcess process;
    private final Map<String, String> measurementUnit = LocaleUtils.getItPriceUnits();

    @FXML private Button back;
    @FXML private Button activate;
    @FXML private ComboBox<Choice<TipologieUso, String>> uses;
    @FXML private ComboBox<String> utilities;
    @FXML private ComboBox<Choice<TipiAttivazione, String>> activationBox;
    @FXML private TableView<Offerte> table;
    @FXML private TableColumn<Offerte, String> nameColumn;
    @FXML private TableColumn<Offerte, String> descColumn;
    @FXML private TableColumn<Offerte, String> costColumn;

    private CatalogueController(final Stage stage, final DataSource dataSource, final SessionHolder holder) {
        super(stage, dataSource, holder, FXML_FILE);
    }

    public static Controller create(final Stage stage, final DataSource dataSource, final SessionHolder holder) {
        return new CatalogueController(stage, dataSource, holder);
    }

    @FXML
    private void backToHome(ActionEvent e) {
        switchTo(HomeController.create(stage(), dataSource(), getSessionHolder()));
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializePlanTable();
    }

    private void initializePlanTable() {
        nameColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNome()));
        descColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDescrizione()));
        costColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getCostomateriaprima()
                        + " "
                        + measurementUnit.get(cellData.getValue().getMateriaprima()))
        );
        try (Connection conn = dataSource().getConnection()) {
            populateComboBoxes(conn);
            populatePlanTable(conn);
        } catch (SQLException e) {
            FXUtils.showError(e.getMessage());
            e.printStackTrace();
        }
    }

    private void populatePlanTable(Connection conn) {
        DSLContext query = DSL.using(conn, SQLDialect.MYSQL);

        var plans = query.select(OFFERTE.asterisk())
                .from(OFFERTE, COMPATIBILITÀ)
                .where(OFFERTE.MATERIAPRIMA.eq(utilities.getValue()))
                .and(OFFERTE.ATTIVA.eq((byte) 1))
                .and(COMPATIBILITÀ.USO.eq(uses.getValue().getItem().getCoduso()))
                .and(OFFERTE.CODOFFERTA.eq(COMPATIBILITÀ.OFFERTA))
                .fetchInto(Offerte.class);

        table.setItems(FXCollections.observableList(plans));
    }

    private void populateComboBoxes(Connection conn) {
        DSLContext ctx = DSL.using(conn, SQLDialect.MYSQL);

        var utilityRecords = ctx.select(MATERIE_PRIME.NOME)
                .from(MATERIE_PRIME)
                .orderBy(MATERIE_PRIME.NOME.desc())
                .fetch();
        utilities.setItems(FXCollections.observableList(utilityRecords.getValues(MATERIE_PRIME.NOME)));
        utilities.setValue(utilityRecords.getValue(0, MATERIE_PRIME.NOME));

        List<Choice<TipologieUso, String>> useRecords = Queries.fetchAll(ctx, TIPOLOGIE_USO, TipologieUso.class)
                .stream()
                .map(t -> new ChoiceImpl<>(t, t.getNome(), (i, n) -> n))
                .collect(Collectors.toList());
        uses.setItems(FXCollections.observableList(useRecords));
        uses.setValue(useRecords.get(0));

        List<Choice<TipiAttivazione, String>> activationRecords = Queries.fetchAll(ctx, TIPI_ATTIVAZIONE, TipiAttivazione.class)
                .stream()
                .map(t -> new ChoiceImpl<>(t, t.getNome() , (i, n) -> n))
                .collect(Collectors.toList());
        activationBox.setItems(FXCollections.observableList(activationRecords));
        activationBox.setValue(activationRecords.get(0));
    }

    @FXML
    private void triggerPopulate(ActionEvent e) {
        try (Connection conn = dataSource().getConnection()) {
            populatePlanTable(conn);
        } catch (SQLException ex) {
            FXUtils.showError(ex.getMessage());
        }
    }

    @FXML
    private void togglePlanTable() {
        table.setDisable(activationBox.getValue().getItem().getNome().equals("Voltura"));
        table.getSelectionModel().clearSelection();
    }

    @FXML
    private void startSubscriptionProcess(ActionEvent e) {
        if (!activationBox.getValue().getItem().getNome().equals("Voltura") &&
                table.getSelectionModel().getSelectedItem() == null) {
            FXUtils.showBlockingWarning("Non hai selezionato un'offerta.");
        } else if (getSessionHolder().session().isEmpty()) {
            FXUtils.showBlockingWarning("Per poter continuare, devi effettuare l'accesso.");
        } else {
            if (process == null) {
                process = new SubscriptionProcessImpl();
            }
            process.setClientId(getSessionHolder().session().orElseThrow().userId());
            process.setPlan(table.isDisabled() ? null : table.getSelectionModel().getSelectedItem());
            process.setUse(uses.getValue().getItem());
            process.setActivationMethod(activationBox.getValue().getItem());

            switch (activationBox.getValue().getItem().getNome()) {
                case "Nuova attivazione":
                    switchTo(ParametersNewActivationController.create(stage(), dataSource(), getSessionHolder(), process));
                    break;
                case "Subentro":
                    switchTo(ParametersSubentroController.create(stage(), dataSource(), getSessionHolder(), process));
                    break;
                case "Voltura":
                    switchTo(ParametersClientChangeController.create(stage(), dataSource(), getSessionHolder(), process));
                    break;
            }
        }
    }
}
