package bdproject.controller.gui.users;

import bdproject.controller.gui.AbstractController;
import bdproject.controller.gui.HomeController;
import bdproject.controller.gui.Controller;
import bdproject.model.Queries;
import bdproject.model.SessionHolder;
import bdproject.model.SubscriptionProcess;
import bdproject.tables.pojos.Contatori;
import bdproject.tables.pojos.Immobili;
import bdproject.tables.pojos.TipiImmobile;
import bdproject.utils.FXUtils;
import bdproject.view.StringUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;

import javax.sql.DataSource;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Map;
import java.util.ResourceBundle;

import static bdproject.tables.TipiImmobile.TIPI_IMMOBILE;
import static org.jooq.impl.DSL.field;

public class SubscriptionConfirmationController extends AbstractController implements Initializable {

    private static final String FXML = "subConfirmation.fxml";
    private static final String FLOW_CSS = "-fx-font: 16 arial";

    private final SubscriptionProcess process;
    private final Map<Integer, Runnable> typeBackAction;

    @FXML private Label planText;
    @FXML private Label utilityText;
    @FXML private Label useText;
    @FXML private Label activationText;
    @FXML private Label currentClientLabel;
    @FXML private TextFlow premisesFlow;
    @FXML private TextFlow currentClientFlow;

    private SubscriptionConfirmationController(final Stage stage, final DataSource dataSource, final SessionHolder holder,
            final SubscriptionProcess process) {
        super(stage, dataSource, holder, FXML);
        this.process = process;
        this.typeBackAction = Map.of(
                3, () -> switchTo(ParametersSelectionController.create(stage(), dataSource(), sessionHolder(), this.process)),
                2, () -> {
                    if (this.process.estate().orElseThrow().getIdimmobile() == 0) {
                        switchTo(PremiseInsertionController.create(stage(), dataSource(), sessionHolder(), this.process));
                    } else {
                        switchTo(ParametersSelectionController.create(stage(), dataSource(), sessionHolder(), this.process));
                    }
                },
                1, () -> switchTo(PremiseInsertionController.create(stage(), dataSource(), sessionHolder(), this.process))
        );
    }

    public static Controller create(final Stage stage, final DataSource dataSource, final SessionHolder holder,
            final SubscriptionProcess process) {
        return new SubscriptionConfirmationController(stage, dataSource, holder, process);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        planText.setText(process.plan().orElseThrow().getNome());
        utilityText.setText(process.plan().orElseThrow().getMateriaprima());
        useText.setText(process.usage().orElseThrow().getNome());
        activationText.setText(process.activation().orElseThrow().getNome());

        setPremisesText();

        try (Connection conn = dataSource().getConnection()) {
            process.otherClient().ifPresentOrElse(
                    c -> {
                        final Text clientText = new Text(StringUtils.clientToString(c.getIdentificativo(), conn));
                        clientText.setStyle(FLOW_CSS);
                        currentClientFlow.getChildren().add(clientText);
                        currentClientLabel.setVisible(true);
                    },
                    () -> {
                        currentClientFlow.getChildren().add(new Text(""));
                        currentClientLabel.setVisible(false);
                    });
        } catch (SQLException e) {
            FXUtils.showError(e.getMessage());
        }
    }

    private void setPremisesText() {
        final TipiImmobile type = Queries.fetchOne(
                TIPI_IMMOBILE, TIPI_IMMOBILE.CODICE, process.estate().orElseThrow().getTipo(), TipiImmobile.class, dataSource()
        ).orElseThrow();
        final Text premisesText = new Text(StringUtils.premiseToString(process.estate().orElseThrow(), type));
        premisesText.setStyle(FLOW_CSS);
        premisesFlow.getChildren().add(premisesText);
    }

    @FXML
    private void goBack() {
        typeBackAction.get(process.activation().orElseThrow().getCodice()).run();
    }

    @FXML
    private void insertSubscription() {
        FXUtils.showConfirmationDialog(
            "Stai per richiedere l'attivazione di un contratto di fornitura. Vuoi continuare?",
            () -> {
                try (Connection conn = dataSource().getConnection()) {
                    int estateId;
                    if (process.estate().orElseThrow().getIdimmobile() == 0) {
                        final Immobili pTemp = process.estate().orElseThrow();
                        Queries.insertPremiseThenReturnId(
                                pTemp.getTipo(),
                                pTemp.getVia(),
                                pTemp.getNumcivico(),
                                pTemp.getComune(),
                                pTemp.getCap(),
                                pTemp.getProvincia(),
                                pTemp.getInterno(),
                                conn);
                        estateId = DSL.using(conn, SQLDialect.MYSQL)
                                .select(field("last_insert_id()", SQLDataType.INTEGER))
                                .fetchOne()
                                .component1();
                    } else {
                        estateId = process.estate().orElseThrow().getIdimmobile();
                    }

                    final Contatori m = process.meter().orElseThrow();
                    if (m.getProgressivo() == 0) {
                        Queries.insertMeterThenReturnId(
                                m.getMatricola(),
                                m.getMateriaprima(),
                                estateId,
                                conn
                        );
                        final int lastMeterId = DSL.using(conn, SQLDialect.MYSQL)
                                .select(field("last_insert_id()", SQLDataType.INTEGER))
                                .fetchOne()
                                .component1();
                        process.setMeter(new Contatori(
                                lastMeterId,
                                m.getMatricola(),
                                m.getMateriaprima(),
                                estateId
                        ));
                    }

                    process.measurement().ifPresent(mt -> Queries.insertMeasurement(mt, conn));

                    final int result = Queries.insertSubscriptionRequest(
                            process.getClientId(),
                            LocalDate.now(),
                            process.plan().orElseThrow().getCodice(),
                            process.meter().orElseThrow().getProgressivo(),
                            process.usage().orElseThrow().getCodice(),
                            process.activation().orElseThrow().getCodice(),
                            process.peopleNo(),
                            conn);
                    if (result != 0) {
                        FXUtils.showBlockingWarning("Richiesta inserita con successo.");
                        switchTo(HomeController.create(stage(), dataSource(), sessionHolder()));
                    } else {
                        FXUtils.showBlockingWarning("Richiesta non inserita!");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
        });
    }
}
