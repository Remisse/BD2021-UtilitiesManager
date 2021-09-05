package bdproject.controller.gui.operators;

import bdproject.controller.gui.AbstractSignUpController;
import bdproject.controller.gui.ViewController;
import bdproject.model.Queries;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import javax.sql.DataSource;
import java.sql.Connection;

public class OperatorSignUpController extends AbstractSignUpController {

    @FXML private Label incomeLabel;
    @FXML private ComboBox<String> income;

    private OperatorSignUpController(final Stage stage, final DataSource dataSource) {
        super(stage, dataSource);
    }

    @Override
    protected void initOther(final Connection conn) {
        income.setVisible(false);
        incomeLabel.setVisible(false);
    }

    public static ViewController create(final Stage stage, final DataSource dataSource) {
        return new OperatorSignUpController(stage, dataSource);
    }

    @Override
    protected int abstractInsertRole(final int personId, final int income, final Connection conn) {
        return Queries.insertOperator(personId, conn);
    }
}
