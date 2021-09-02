package bdproject.controller.gui.users;

import bdproject.controller.gui.AbstractSignupController;
import bdproject.controller.gui.ViewController;
import bdproject.model.Queries;
import javafx.stage.Stage;

import javax.sql.DataSource;
import java.sql.Connection;

public class UserSignupController extends AbstractSignupController {

    private UserSignupController(final Stage stage, final DataSource dataSource) {
        super(stage, dataSource);
    }

    public static ViewController create(final Stage stage, final DataSource dataSource) {
        return new UserSignupController(stage, dataSource);
    }

    @Override
    protected int abstractInsertRole(final int personId, final String income, final Connection conn) {
        return Queries.insertClient(personId, income, conn);
    }
}
