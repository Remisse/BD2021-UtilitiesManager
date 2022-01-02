package bdproject.controller.gui.operators;

import bdproject.controller.Checks;
import bdproject.controller.gui.AbstractController;
import bdproject.controller.gui.Controller;
import bdproject.controller.gui.admin.AreaSelectorController;
import bdproject.model.Queries;
import bdproject.model.SessionHolder;
import bdproject.tables.pojos.Persone;
import bdproject.utils.ViewUtils;
import bdproject.view.StringUtils;
import javafx.application.Platform;
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
import java.util.Optional;
import java.util.ResourceBundle;

import static bdproject.tables.Persone.PERSONE;

public class MiscController extends AbstractController implements Initializable {

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

    private MiscController(final Stage stage, final DataSource dataSource, final SessionHolder holder) {
        super(stage, dataSource, holder, FXML_FILE);
    }

    public static Controller create(final Stage stage, final DataSource dataSource, final SessionHolder holder) {
        return new MiscController(stage, dataSource, holder);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        populateUserDetails();
    }

    private void populateUserDetails() {
        Optional<Persone> person = Optional.empty();
        try (Connection conn = dataSource().getConnection()) {
            person = Queries.fetchPersonById(getSessionHolder().session().orElseThrow().userId(), conn);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        person.ifPresent(p -> Platform.runLater(() -> {
            street.setText(p.getVia());
            civic.setText(p.getNumcivico());
            postcode.setText(p.getCap());
            municipality.setText(p.getComune());
            state.setText(p.getProvincia());
            email.setText(p.getEmail());
            phone.setText(p.getNumerotelefono());
        }));
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
        try (Connection conn = dataSource().getConnection()) {
            person = Queries.fetchPersonById(getSessionHolder().session().orElseThrow().userId(), conn).orElseThrow();
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
            ViewUtils.showBlockingWarning("Verifica di aver inserito correttamente i nuovi dati.");
        } else {
            final int operatorId = getSessionHolder().session().orElseThrow().userId();
            try (Connection conn = dataSource().getConnection()) {
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
                    ViewUtils.showBlockingWarning("Dati modificati con successo.");
                } else {
                    ViewUtils.showBlockingWarning("Impossibile modificare i dati.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                ViewUtils.showError(StringUtils.getGenericError());
            }
        }
    }

    @FXML
    private void doUpdatePassword() {
        if (!isAreAllPasswordFieldsBlank() && isNewPasswordCorrectlySet()) {
            final int operatorId = getSessionHolder().session().orElseThrow().userId();
            try (Connection conn = dataSource().getConnection()) {
                final int resultUpdatePw = Queries.updateOneFieldWhere(PERSONE, PERSONE.IDPERSONA, operatorId,
                        PERSONE.PASSWORD, newPw.getText(), conn);
                if (resultUpdatePw == 1) {
                    ViewUtils.showBlockingWarning("Password modificata con successo.");
                } else {
                    ViewUtils.showBlockingWarning("Impossibile modificare la password.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                ViewUtils.showError(StringUtils.getGenericError());
            }
        } else {
            ViewUtils.showBlockingWarning("Verifica i dati inseriti.");
        }
    }

    @FXML
    private void goBack() {
        switchTo(AreaSelectorController.create(stage(), dataSource(), getSessionHolder()));
    }
}
