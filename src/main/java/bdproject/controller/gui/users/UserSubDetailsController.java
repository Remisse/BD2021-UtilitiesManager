package bdproject.controller.gui.users;

import bdproject.controller.gui.AbstractSubscriptionDetailsController;
import bdproject.controller.gui.Controller;
import bdproject.model.Queries;
import bdproject.model.SessionHolder;
import bdproject.tables.pojos.Cessazioni;
import bdproject.tables.pojos.Contratti;
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

    @FXML private TableView<Cessazioni> endRequestTable;

    protected UserSubDetailsController(final Stage stage, final DataSource dataSource, final SessionHolder holder,
            final Contratti detailedSub) {
        super(stage, dataSource, holder, detailedSub);
    }

    public static Controller create(final Stage stage, final DataSource dataSource, final SessionHolder holder,
            final Contratti detailedSub) {
        return new UserSubDetailsController(stage, dataSource, holder, detailedSub);
    }

    /**
     * Left unimplemented.
     */
    @Override
    protected void setOther() {
    }

    @Override
    protected Controller getBackController() {
        return UserAreaController.create(stage(), dataSource(), sessionHolder());
    }

    @Override
    protected void abstractDoInsertEndRequest() {
        final Contratti subscription = getSubscription();

        try (Connection conn = dataSource().getConnection()) {
            if (subscription.getDatacessazione() != null) {
                FXUtils.showBlockingWarning("Il contratto risulta già cessato.");
            } else if (!Queries.allReportsPaid(subscription.getIdcontratto(), conn)) {
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
        final Cessazioni selected = endRequestTable.getSelectionModel().getSelectedItem();

        if (selected != null && (selected.getStatorichiesta().equals("In gestione"))) {
            try (Connection conn = dataSource().getConnection()) {
                final int result = Queries.deleteEndRequest(selected.getNumerorichiesta(), conn);
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
