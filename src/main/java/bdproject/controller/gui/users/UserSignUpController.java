package bdproject.controller.gui.users;

import bdproject.controller.gui.AbstractSignUpController;
import bdproject.controller.gui.Controller;
import bdproject.model.Queries;
import bdproject.model.SessionHolder;
import javafx.stage.Stage;

import javax.sql.DataSource;
import java.sql.Connection;

public class UserSignUpController extends AbstractSignUpController {

    private UserSignUpController(final Stage stage, final DataSource dataSource, final SessionHolder holder) {
        super(stage, dataSource, holder);
    }

    /**
     * Empty
     * @param conn
     */
    @Override
    protected void initOther(final Connection conn) {

    }

    public static Controller create(final Stage stage, final DataSource dataSource, final SessionHolder holder) {
        return new UserSignUpController(stage, dataSource, holder);
    }

    @Override
    protected int abstractInsertRole(final int personId, final int income, final Connection conn) {
        return Queries.insertClient(personId, income, conn);
    }
}
