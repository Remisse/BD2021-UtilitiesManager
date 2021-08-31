package bdproject.controller.gui.userarea;

import bdproject.controller.gui.AbstractSubscriptionDetailsController;
import bdproject.controller.gui.ViewController;
import bdproject.model.Queries;
import bdproject.model.SessionHolder;
import bdproject.tables.pojos.ContrattiDettagliati;
import bdproject.tables.pojos.RichiesteCessazione;
import bdproject.utils.FXUtils;
import bdproject.view.StringUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class UserSubDetailsController extends AbstractSubscriptionDetailsController implements Initializable {

    @FXML private TableView<RichiesteCessazione> endRequestTable;

    protected UserSubDetailsController(Stage stage, DataSource dataSource, ContrattiDettagliati detailedSub) {
        super(stage, dataSource, detailedSub);
    }

    public static ViewController create(final Stage stage, final DataSource dataSource,
            final ContrattiDettagliati detailedSub) {
        return new UserSubDetailsController(stage, dataSource, detailedSub);
    }

    /**
     * Left unimplemented.
     */
    @Override
    protected void setOther() {
    }

    @Override
    protected ViewController getBackController() {
        return UserAreaController.create(getStage(), getDataSource());
    }

    @Override
    protected void abstractDoInsertEndRequest() {
        final ContrattiDettagliati subscription = getSubscription();

        try (Connection conn = getDataSource().getConnection()) {
            if (subscription.getDatacessazione() != null) {
                FXUtils.showBlockingWarning("Il contratto risulta già cessato.");
            } else if (Queries.hasOngoingInterruption(subscription, conn)) {
                FXUtils.showBlockingWarning("La fornitura risulta temporaneamente interrotta. Non è attualmente" +
                        "possibile richiedere la cessazione.");
            } else if (!Queries.allReportsPaid(subscription, conn)) {
                FXUtils.showBlockingWarning("Risultano bollette non pagate. Non è attualmente" +
                        "possibile richiedere la cessazione.");
            } else {
                final int result = Queries.insertEndRequest(getSubscription().getIdcontratto(), conn);
                if (result == 1) {
                    FXUtils.showBlockingWarning("Richiesta di cessazione inviata.");
                    refreshEndRequestTable();
                } else {
                    FXUtils.showBlockingWarning("Impossibile inviare la richiesta.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            FXUtils.showError(e.getSQLState());
        }
    }

    @Override
    protected void abstractDoDeleteEndRequest() {
        final RichiesteCessazione selectedRequest = endRequestTable.getSelectionModel().getSelectedItem();

        if (selectedRequest != null && selectedRequest.getStato() == null) {
            try (Connection conn = getDataSource().getConnection()) {
                final int result = Queries.deleteEndRequest(selectedRequest.getNumero(), conn);
                if (result == 1) {
                    FXUtils.showBlockingWarning("Richiesta eliminata.");
                } else {
                    FXUtils.showBlockingWarning(StringUtils.getGenericError());
                }
            } catch (SQLException e) {
                e.printStackTrace();
                FXUtils.showError(e.getSQLState());
            }
        } else {
            FXUtils.showBlockingWarning("Seleziona una richiesta valida.");
        }
    }
}
