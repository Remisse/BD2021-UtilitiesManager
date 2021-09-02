package bdproject.controller.gui;

import bdproject.model.Queries;
import bdproject.utils.FXUtils;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.apache.commons.validator.routines.EmailValidator;

import javax.annotation.Nonnull;
import javax.sql.DataSource;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public abstract class AbstractSignupController extends AbstractViewController implements Initializable {

    private static final String FXML_FILE = "signup.fxml";

    private static final int ID_CODE_LIMIT = 16;
    private static final int POSTCODE_LIMIT = 5;
    private static final int PASSWORD_MAX = 30;
    private static final int PASSWORD_MIN = 8;
    private static final int PROVINCE_LIMIT = 2;

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
    @FXML private ComboBox<String> income;
    @FXML private TextField phone;
    @FXML private TextField email;
    @FXML private PasswordField password;
    @FXML private PasswordField confirmPw;

    protected AbstractSignupController(Stage stage, DataSource dataSource) {
        super(stage, dataSource, FXML_FILE);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try (Connection conn = getDataSource().getConnection()) {
            List<String> brackets = Queries.fetchAllIncomeBrackets(conn);
            income.setItems(FXCollections.observableList(brackets));
        } catch (SQLException e) {
            FXUtils.showError(e.getMessage());
        }
    }

    @FXML
    private void doSignup(ActionEvent event) {
        if (areFieldsInvalid()) {
            FXUtils.showBlockingWarning("Verifica i dati immessi.");
        } else {
            try (Connection conn = getDataSource().getConnection()) {
                final int lastInsertId = Queries.insertPersonAndReturnId(
                        idCode.getText(),
                        name.getText(),
                        surname.getText(),
                        street.getText(),
                        streetNo.getText(),
                        postcode.getText(),
                        municipality.getText(),
                        province.getText(),
                        birthdate.getValue(),
                        phone.getText(),
                        email.getText(),
                        password.getText(),
                        conn);
                if (lastInsertId == 0) {
                    FXUtils.showBlockingWarning("Impossibile creare un nuovo account.");
                } else {
                    final int result = abstractInsertRole(lastInsertId, income.getValue(), conn);
                    if (result == 1) {
                        FXUtils.showBlockingWarning("Registrazione completata.");
                        switchTo(HomeController.create(getStage(), getDataSource()));
                    } else {
                        FXUtils.showError("Creazione dell'account non riuscita.");
                    }
                }
            } catch (Exception e) {
                FXUtils.showError(e.getMessage());
            }
        }
    }

    protected abstract int abstractInsertRole(final int personId, final String income, final Connection conn);

    private boolean areFieldsInvalid() {
        return (name.getText().length() == 0
            || surname.getText().length() == 0
            || idCode.getText().length() != ID_CODE_LIMIT
            || birthdate.getValue() == null
            || street.getText().length() == 0
            || streetNo.getText().length() == 0
            || municipality.getText().length() == 0
            || postcode.getText().length() != POSTCODE_LIMIT
            || province.getText().length() != PROVINCE_LIMIT
            || income.getValue() == null
            || phone.getText().length() == 0
            || email.getText().length() == 0
            || EmailValidator.getInstance().isValid(email.getText())
            || password.getText().length() < PASSWORD_MIN
            || password.getText().length() > PASSWORD_MAX
            || !confirmPw.getText().equals(password.getText()));
    }

    @FXML
    private void backToHome(ActionEvent event) {
        FXUtils.showConfirmationDialog(
                "Vuoi davvero tornare al menu principale? Tutti i dati inseriti verranno persi.",
                () -> switchTo(HomeController.create(getStage(), getDataSource())));
    }
}
