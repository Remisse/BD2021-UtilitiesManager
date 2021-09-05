package bdproject.controller.gui.operators;

import bdproject.controller.Checks;
import bdproject.controller.gui.AbstractViewController;
import bdproject.controller.gui.ViewController;
import bdproject.model.Queries;
import bdproject.model.SessionHolder;
import bdproject.tables.pojos.ClientiDettagliati;
import bdproject.tables.pojos.Persone;
import bdproject.utils.FXUtils;
import bdproject.view.StringUtils;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.apache.commons.validator.routines.EmailValidator;

import javax.sql.DataSource;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ResourceBundle;

import static bdproject.tables.Persone.PERSONE;

public class MiscController extends AbstractViewController implements Initializable {

    private static final String FXML_FILE = "operatorMisc.fxml";
    private static final int POSTCODE_LIMIT = 5;
    private static final int PASSWORD_MAX = 30;
    private static final int PASSWORD_MIN = 8;

    @FXML private TextField street;
    @FXML private TextField civic;
    @FXML private TextField postcode;
    @FXML private TextField municipality;
    @FXML private TextField state;
    @FXML private TextField email;
    @FXML private TextField phone;
    @FXML private PasswordField currentPw;
    @FXML private PasswordField newPw;
    @FXML private PasswordField confirmPw;

    private MiscController(Stage stage, DataSource dataSource) {
        super(stage, dataSource, FXML_FILE);
    }

    public static ViewController create(final Stage stage, final DataSource dataSource) {
        return new MiscController(stage, dataSource);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        populateUserDetails();
    }

    private void populateUserDetails() {
        try (Connection conn = getDataSource().getConnection()) {
            final Persone person = Queries.fetchPersonById(SessionHolder.getSession().orElseThrow().getUserId(), conn)
                    .orElseThrow();

            street.setText(person.getVia());
            civic.setText(person.getNumcivico());
            postcode.setText(person.getCap());
            municipality.setText(person.getComune());
            state.setText(person.getProvincia());
            email.setText(person.getEmail());
            phone.setText(person.getNumerotelefono());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private boolean areUserFieldsInvalid() {
        return street.getText().length() == 0
                || civic.getText().length() == 0
                || municipality.getText().length() == 0
                || postcode.getText().length() != POSTCODE_LIMIT
                || state.getText().length() != 2
                || phone.getText().length() == 0
                || email.getText().length() == 0
                || !EmailValidator.getInstance().isValid(email.getText())
                || phone.getText().length() == 0
                || !Checks.isIntegerNumber(phone.getText());
    }

    private boolean isAreAllPasswordFieldsBlank() {
        return currentPw.getText().length() == 0 || newPw.getText().length() == 0 || confirmPw.getText().length() == 0;
    }

    private boolean isNewPasswordCorrectlySet() {
        Persone person = null;
        try (Connection conn = getDataSource().getConnection()) {
            person = Queries.fetchPersonById(SessionHolder.getSession().orElseThrow().getUserId(), conn).orElseThrow();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (person == null) {
            throw new IllegalStateException("Fetched operator in their own area should not be null!");
        }
        return currentPw.getText().equals(person.getPassword()) &&
                newPw.getText().length() >= PASSWORD_MIN && newPw.getText().length() <= PASSWORD_MAX &&
                confirmPw.getText().equals(newPw.getText());
    }

    @FXML
    private void doUpdatePerson() {
        if (areUserFieldsInvalid()) {
            FXUtils.showBlockingWarning("Verifica di aver inserito correttamente i nuovi dati.");
        } else {
            final int operatorId = SessionHolder.getSession().orElseThrow().getUserId();
            try (Connection conn = getDataSource().getConnection()) {
                final int resultUpdateData = Queries.updatePerson(
                        email.getText(),
                        postcode.getText(),
                        municipality.getText(),
                        civic.getText(),
                        phone.getText(),
                        state.getText(),
                        street.getText(),
                        operatorId,
                        conn);
                if (resultUpdateData == 1) {
                    FXUtils.showBlockingWarning("Dati modificati con successo.");
                } else {
                    FXUtils.showBlockingWarning("Impossibile modificare i dati.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                FXUtils.showError(StringUtils.getGenericError());
            }
        }
    }

    @FXML
    private void doUpdatePassword() {
        if (!isAreAllPasswordFieldsBlank() && isNewPasswordCorrectlySet()) {
            final int operatorId = SessionHolder.getSession().orElseThrow().getUserId();
            try (Connection conn = getDataSource().getConnection()) {
                final int resultUpdatePw = Queries.updateOneFieldWhere(PERSONE, PERSONE.IDENTIFICATIVO, operatorId,
                        PERSONE.PASSWORD, newPw.getText(), conn);
                if (resultUpdatePw == 1) {
                    FXUtils.showBlockingWarning("Password modificata con successo.");
                } else {
                    FXUtils.showBlockingWarning("Impossibile modificare la password.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                FXUtils.showError(StringUtils.getGenericError());
            }
        } else {
            FXUtils.showBlockingWarning("Verifica i dati inseriti.");
        }
    }

    @FXML
    private void goBack() {
        switchTo(AreaSelectorController.create(getStage(), getDataSource()));
    }
}
