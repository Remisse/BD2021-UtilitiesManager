package bdproject.controller.gui;

import bdproject.controller.Choice;
import bdproject.controller.ChoiceImpl;
import bdproject.utils.FXUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.apache.commons.validator.routines.EmailValidator;
import org.apache.commons.validator.routines.IBANValidator;
import org.jooq.*;
import org.jooq.impl.DSL;

import javax.sql.DataSource;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ResourceBundle;

import static bdproject.Tables.*;
import static org.jooq.impl.DSL.*;

public class SignupController extends AbstractViewController implements Initializable {

    private static final String fxml = "signup.fxml";

    private static final int ID_CODE_LIMIT = 16;
    private static final int VAT_CODE_LIMIT = 11;
    private static final int POSTCODE_LIMIT = 5;
    private static final int PASSWORD_MAX = 30;
    private static final int PASSWORD_MIN = 8;
    @FXML
    private TextField name;
    @FXML
    private TextField surname;
    @FXML
    private TextField idCode;
    @FXML
    private TextField vatCode;
    @FXML
    private DatePicker birthdate;
    @FXML
    private TextField street;
    @FXML
    private TextField streetNo;
    @FXML
    private TextField municipality;
    @FXML
    private TextField postcode;
    @FXML
    private TextField state;
    @FXML
    private ComboBox<String> income;
    @FXML
    private TextField phone;
    @FXML
    private TextField email;
    @FXML
    private PasswordField password;
    @FXML
    private PasswordField confirmPw;

    private SignupController(Stage stage, DataSource dataSource) {
        super(stage, dataSource, fxml);
    }

    public static ViewController create(final Stage stage, final DataSource dataSource) {
        return new SignupController(stage, dataSource);
    }

    @FXML
    private void backToHome(ActionEvent event) {
        Alert alert = new Alert(
                Alert.AlertType.WARNING,
                "Vuoi davvero tornare al menu principale? Tutti i dati inseriti verranno persi.",
                ButtonType.YES, ButtonType.NO);
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                switchTo(HomeController.create(getStage(), getDataSource()));
            } else {
                alert.close();
            }
        });
    }

    @FXML
    private void doSignup(ActionEvent event) {
        if (!checkAllFields()) {
            FXUtils.showBlockingWarning("Verifica i dati immessi.");
        } else {
            try (Connection conn = getDataSource().getConnection()) {
                DSLContext create = DSL.using(conn, SQLDialect.MYSQL);
                var existing = create.select()
                        .from(PERSONE_FISICHE)
                        .where(PERSONE_FISICHE.EMAIL.eq(email.getText()))
                        .fetchOptional();
                if (existing.isEmpty()) {
                    int insertion = create.insertInto(PERSONE_FISICHE)
                            .values(
                                    defaultValue(),
                                    idCode.getText(),
                                    vatCode.getText().length() == 0 ? defaultValue() : vatCode.getText(),
                                    name.getText(),
                                    surname.getText(),
                                    street.getText(),
                                    streetNo.getText(),
                                    Integer.parseInt(postcode.getText()),
                                    municipality.getText(),
                                    birthdate.getValue(),
                                    phone.getText(),
                                    email.getText(),
                                    password.getText(),
                                    income.getValue()
                            ).execute();
                    if (insertion == 1) {
                        FXUtils.showBlockingWarning("Registrazione completata.");
                        switchTo(HomeController.create(getStage(), getDataSource()));
                    } else {
                        FXUtils.showError("Creazione dell'account non riuscita.");
                    }
                } else {
                    FXUtils.showBlockingWarning("Esiste già un account associato a questo indirizzo e-mail.");
                }
            } catch (SQLException | NullPointerException e) {
                FXUtils.showError(e.getMessage());
            }
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try (Connection conn = getDataSource().getConnection()) {
            DSLContext create = DSL.using(conn, SQLDialect.MYSQL);

            var brackets = create.select(REDDITI.FASCIA)
                    .from(REDDITI)
                    .orderBy(REDDITI.FASCIA)
                    .fetch();
            income.setItems(FXCollections.observableList(brackets.getValues(REDDITI.FASCIA)));
        } catch (SQLException e) {
            FXUtils.showError(e.getMessage());
        }
    }

    private boolean checkAllFields() {
        return (name.getText().length() == 0
            || surname.getText().length() == 0
            || idCode.getText().length() != ID_CODE_LIMIT
            || (vatCode.getText().length() == 0 || vatCode.getText().length() > 0 && vatCode.getText().length() != VAT_CODE_LIMIT)
            || birthdate.getValue() == null
            || street.getText().length() == 0
            || streetNo.getText().length() == 0
            || municipality.getText().length() == 0
            || postcode.getText().length() != POSTCODE_LIMIT
            || state.getText().length() == 0
            || income.getValue() == null
            || phone.getText().length() == 0
            || email.getText().length() == 0
            || EmailValidator.getInstance().isValid(email.getText())
            || password.getText().length() < PASSWORD_MIN
            || password.getText().length() > PASSWORD_MAX
            || !confirmPw.getText().equals(password.getText()));
    }
}
