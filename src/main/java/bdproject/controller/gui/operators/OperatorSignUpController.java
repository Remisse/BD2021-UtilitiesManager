package bdproject.controller.gui.operators;

import bdproject.controller.gui.AbstractSignUpController;
import bdproject.controller.gui.Controller;
import bdproject.model.Queries;
import bdproject.model.SessionHolder;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import javax.sql.DataSource;
import java.sql.Connection;

public class OperatorSignUpController extends AbstractSignUpController {

    @FXML private Label incomeLabel;
    @FXML private ComboBox<String> income;

    private OperatorSignUpController(final Stage stage, final DataSource dataSource, final SessionHolder holder) {
        super(stage, dataSource, holder);
    }

    public static Controller create(final Stage stage, final DataSource dataSource, final SessionHolder holder) {
        return new OperatorSignUpController(stage, dataSource, holder);
    }

    @Override
    protected void initOther(final Connection conn) {
        income.setVisible(false);
        incomeLabel.setVisible(false);
    }

    @Override
    protected int abstractInsertRole(final int personId, final int income, final Connection conn) {
        return Queries.insertOperator(personId, conn);
    }
}
