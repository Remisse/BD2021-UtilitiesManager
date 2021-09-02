package bdproject.controller.gui.operators;

import bdproject.controller.gui.AbstractSignUpController;
import bdproject.controller.gui.ViewController;
import bdproject.model.Queries;
import javafx.stage.Stage;

import javax.sql.DataSource;
import java.sql.Connection;

public class OperatorSignUpController extends AbstractSignUpController {

    private OperatorSignUpController(final Stage stage, final DataSource dataSource) {
        super(stage, dataSource);
    }

    public static ViewController create(final Stage stage, final DataSource dataSource) {
        return new OperatorSignUpController(stage, dataSource);
    }

    @Override
    protected int abstractInsertRole(final int personId, final String income, final Connection conn) {
        return Queries.insertOperator(personId, conn);
    }
}
