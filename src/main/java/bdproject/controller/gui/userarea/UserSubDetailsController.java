package bdproject.controller.gui.userarea;

import bdproject.controller.gui.AbstractSubscriptionDetailsController;
import bdproject.controller.gui.ViewController;
import bdproject.model.Queries;
import bdproject.tables.pojos.Contratti;
import bdproject.utils.FXUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.stage.Stage;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class UserSubDetailsController extends AbstractSubscriptionDetailsController implements Initializable {

    protected UserSubDetailsController(Stage stage, DataSource dataSource, Contratti subscription) {
        super(stage, dataSource, subscription);
    }

    public static ViewController create(final Stage stage, final DataSource dataSource, final Contratti subscription) {
        return new UserSubDetailsController(stage, dataSource, subscription);
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

    @FXML
    private void doEndSubscription() {
        final Contratti subscription = getSubscription();

        try (Connection conn = getDataSource().getConnection()) {
            if (subscription.getDatacessazione() != null) {
                FXUtils.showBlockingWarning("Il contratto risulta già cessato.");
            } else if (Queries.hasOngoingInterruption(subscription, conn)) {
                FXUtils.showBlockingWarning("La fornitura risulta temporaneamente interrotta. Non è attualmente" +
                        "possibile cessare il contratto.");
            } else if (!Queries.allReportsPaid(subscription, conn)) {
                FXUtils.showBlockingWarning("Risultano bollette non pagate. Non è attualmente" +
                        "possibile cessare il contratto.");
            } else {
                Queries.ceaseSubscription(subscription, conn);
                setStatus(subscription.getDatainizio(), subscription.getDatacessazione(), conn);
                FXUtils.showBlockingWarning("Contratto cessato.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            FXUtils.showError(e.getSQLState());
        }
    }
}
