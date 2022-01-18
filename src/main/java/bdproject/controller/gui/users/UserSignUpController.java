package bdproject.controller.gui.users;

import bdproject.controller.Choice;
import bdproject.controller.gui.AbstractSignUpController;
import bdproject.controller.gui.Controller;
import bdproject.model.Queries;
import bdproject.model.SessionHolder;
import bdproject.tables.pojos.Redditi;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.apache.commons.validator.routines.EmailValidator;

import javax.sql.DataSource;
import java.sql.Connection;

public class UserSignUpController extends AbstractSignUpController {

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
    @FXML private ComboBox<Choice<Redditi, String>> income;
    @FXML private TextField phone;
    @FXML private TextField email;
    @FXML private PasswordField password;
    @FXML private PasswordField confirmPw;

    @FXML private Label salaryLabel;
    @FXML private TextField salary;

    private UserSignUpController(final Stage stage, final DataSource dataSource, final SessionHolder holder) {
        super(stage, dataSource, holder);
    }

    @Override
    protected void initOther(final Connection conn) {
        Platform.runLater(() -> {
            salaryLabel.setVisible(false);
            salary.setVisible(false);
        });
    }

    public static Controller create(final Stage stage, final DataSource dataSource, final SessionHolder holder) {
        return new UserSignUpController(stage, dataSource, holder);
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
                || income.getValue() == null
                || phone.getText().length() == 0
                || email.getText().length() == 0
                || EmailValidator.getInstance().isValid(email.getText())
                || password.getText().length() < getPasswordMin()
                || password.getText().length() > getPasswordMax()
                || !confirmPw.getText().equals(password.getText()));
    }

    @Override
    protected int abstractInsert(final int personId, final Connection conn) {
        return Queries.insertClient(personId, income.getValue().getItem().getCodreddito(), conn);
    }
}
