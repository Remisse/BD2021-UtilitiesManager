package bdproject.controller.gui.admin;

import bdproject.controller.Checks;
import bdproject.controller.Choice;
import bdproject.controller.gui.AbstractSignUpController;
import bdproject.controller.gui.Controller;
import bdproject.model.Queries;
import bdproject.model.SessionHolder;
import bdproject.model.types.EmployeeType;
import bdproject.tables.pojos.Redditi;
import bdproject.utils.ViewUtils;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.apache.commons.validator.routines.EmailValidator;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

public class OperatorSignUpController extends AbstractSignUpController {

    @FXML private TextField name;
    @FXML private TextField surname;
    @FXML private TextField idCode;
    @FXML private TextField vatCode;
    @FXML private DatePicker birthdate;
    @FXML private TextField street;
    @FXML private TextField streetNo;
    @FXML private TextField municipality;
    @FXML private TextField postcode;
    @FXML private TextField province;
    @FXML private TextField phone;
    @FXML private TextField email;
    @FXML private PasswordField password;
    @FXML private PasswordField confirmPw;
    @FXML private Label salaryLabel;
    @FXML private TextField salary;
    @FXML private Label roleLabel;
    @FXML private ComboBox<String> role;

    @FXML private Label incomeLabel;
    @FXML private ComboBox<Choice<Redditi, String>> income;

    private OperatorSignUpController(final Stage stage, final DataSource dataSource, final SessionHolder holder) {
        super(stage, dataSource, holder);
    }

    public static Controller create(final Stage stage, final DataSource dataSource, final SessionHolder holder) {
        return new OperatorSignUpController(stage, dataSource, holder);
    }

    @Override
    protected void initOther(final Connection conn) {
        final List<String> types = new ArrayList<>();
        for (EmployeeType type : EmployeeType.values()) {
            types.add(type.toString());
        }

        Platform.runLater(() -> {
            income.setVisible(false);
            incomeLabel.setVisible(false);
            role.setItems(FXCollections.observableList(types));
        });
    }

    @Override
    protected int abstractInsert(final int personId, final Connection conn) {
        return Queries.insertOperator(personId, role.getValue(), new BigDecimal(salary.getText()), conn);
    }

    protected boolean areFieldsInvalid() {
        return (name.getText().length() == 0
                || surname.getText().length() == 0
                || idCode.getText().length() != getIdCodeLimit()
                || birthdate.getValue() == null
                || street.getText().length() == 0
                || streetNo.getText().length() == 0
                || municipality.getText().length() == 0
                || postcode.getText().length() != getPostCodeLimit()
                || province.getText().length() != getProvinceLimit()
                || phone.getText().length() == 0
                || email.getText().length() == 0
                || EmailValidator.getInstance().isValid(email.getText())
                || password.getText().length() < getPasswordMin()
                || password.getText().length() > getPasswordMax()
                || !confirmPw.getText().equals(password.getText())
                || (!Checks.isBigDecimal(salary.getText()) || salary.getText().length() == 0)
                || role.getValue() == null);
    }

    @FXML
    private void backToHome(ActionEvent event) {
        ViewUtils.showConfirmationDialog(
                "Vuoi davvero tornare al menu? Tutti i dati inseriti verranno persi.",
                () -> switchTo(AreaSelectorController.create(stage(), dataSource(), getSessionHolder())));
    }
}
