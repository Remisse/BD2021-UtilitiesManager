package bdproject.controller.gui.users;

import bdproject.controller.gui.AbstractSubscriptionDetailsController;
import bdproject.controller.gui.Controller;
import bdproject.model.Queries;
import bdproject.model.SessionHolder;
import bdproject.tables.pojos.Cessazioni;
import bdproject.tables.pojos.ContrattiApprovati;
import bdproject.tables.pojos.ContrattiAttivi;
import bdproject.utils.ViewUtils;
import bdproject.view.StringUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class UserSubDetailsController extends AbstractSubscriptionDetailsController implements Initializable {

    @FXML private TableView<Cessazioni> endRequestTable;

    protected UserSubDetailsController(final Stage stage, final DataSource dataSource, final SessionHolder holder,
            final ContrattiApprovati subscription) {
        super(stage, dataSource, holder, subscription);
    }

    public static Controller create(final Stage stage, final DataSource dataSource, final SessionHolder holder,
            final ContrattiApprovati subscription) {
        return new UserSubDetailsController(stage, dataSource, holder, subscription);
    }

    /**
     * Left unimplemented.
     */
    @Override
    protected void setOther() {
    }

    @Override
    protected Controller getBackController() {
        return UserAreaController.create(stage(), dataSource(), getSessionHolder());
    }

    @Override
    protected void abstractDoInsertEndRequest() {
        final ContrattiApprovati subscription = getSubscription();

        try (Connection conn = dataSource().getConnection()) {
            if (Queries.fetchApprovedEndRequestBySubscription(subscription.getIdcontratto(), conn).isPresent()) {
                ViewUtils.showBlockingWarning("Il contratto risulta già cessato.");
            } else if (!Queries.areAllReportsPaid(subscription.getIdcontratto(), conn)) {
                ViewUtils.showBlockingWarning("Risultano bollette non pagate. Non è attualmente" +
                        "possibile richiedere la cessazione.");
            } else {
                final int result = Queries.insertEndRequest(getSubscription().getIdcontratto(), conn);
                if (result == 1) {
                    ViewUtils.showBlockingWarning("Richiesta di cessazione inviata.");
                    refreshEndRequestTable();
                } else {
                    ViewUtils.showBlockingWarning("Impossibile inviare la richiesta.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            ViewUtils.showError(e.getSQLState());
        }
    }

    @Override
    protected void abstractDoDeleteEndRequest() {
        final Cessazioni selected = endRequestTable.getSelectionModel().getSelectedItem();

        if (selected != null && (selected.getStatorichiesta().equals("In gestione"))) {
            try (Connection conn = dataSource().getConnection()) {
                final int result = Queries.deleteEndRequest(selected.getNumerorichiesta(), conn);
                if (result == 1) {
                    ViewUtils.showBlockingWarning("Richiesta eliminata.");
                    refreshEndRequestTable();
                } else {
                    ViewUtils.showBlockingWarning(StringUtils.getGenericError());
                }
            } catch (SQLException e) {
                e.printStackTrace();
                ViewUtils.showError(e.getSQLState());
            }
        } else {
            ViewUtils.showBlockingWarning("Seleziona una richiesta valida.");
        }
    }
}
