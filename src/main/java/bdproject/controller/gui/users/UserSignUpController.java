package bdproject.controller.gui.users;

import bdproject.controller.gui.AbstractSignUpController;
import bdproject.controller.gui.ViewController;
import bdproject.model.Queries;
import javafx.stage.Stage;

import javax.sql.DataSource;
import java.sql.Connection;

public class UserSignUpController extends AbstractSignUpController {

    private UserSignUpController(final Stage stage, final DataSource dataSource) {
        super(stage, dataSource);
    }

    /**
     * Empty
     * @param conn
     */
    @Override
    protected void initOther(final Connection conn) {

    }

    public static ViewController create(final Stage stage, final DataSource dataSource) {
        return new UserSignUpController(stage, dataSource);
    }

    @Override
    protected int abstractInsertRole(final int personId, final int income, final Connection conn) {
        return Queries.insertClient(personId, income, conn);
    }
}
