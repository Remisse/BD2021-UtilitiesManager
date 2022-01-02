package bdproject.controller.gui;

import bdproject.controller.Choice;
import bdproject.controller.ChoiceImpl;
import bdproject.model.Queries;
import bdproject.model.SessionHolder;
import bdproject.tables.pojos.Redditi;
import bdproject.utils.ViewUtils;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import javax.sql.DataSource;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import static bdproject.Tables.REDDITI;

public abstract class AbstractSignUpController extends AbstractController implements Initializable {

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
    @FXML private ComboBox<Choice<Redditi, String>> income;
    @FXML private TextField phone;
    @FXML private TextField email;
    @FXML private PasswordField password;
    @FXML private PasswordField confirmPw;

    protected AbstractSignUpController(final Stage stage, final DataSource dataSource, final SessionHolder holder) {
        super(stage, dataSource, holder, FXML_FILE);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try (Connection conn = dataSource().getConnection()) {
            final List<Redditi> brackets = Queries.fetchAll(DSL.using(conn, SQLDialect.MYSQL), REDDITI, Redditi.class);
            final List<Choice<Redditi, String>> list = brackets.stream()
                    .map(r -> new ChoiceImpl<>(r, r.getFascia(), (i, v) -> v))
                    .collect(Collectors.toList());
            income.setItems(FXCollections.observableList(list));
            income.setValue(list.get(0));
            initOther(conn);
        } catch (SQLException e) {
            ViewUtils.showError(e.getMessage());
        }
    }

    @FXML
    private void doSignup(ActionEvent event) {
        if (areFieldsInvalid()) {
            ViewUtils.showBlockingWarning("Verifica i dati immessi.");
        } else {
            try (Connection conn = dataSource().getConnection()) {
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
                    ViewUtils.showBlockingWarning("Impossibile creare un nuovo account.");
                } else {
                    final int result = abstractInsert(lastInsertId, conn);
                    if (result == 1) {
                        ViewUtils.showBlockingWarning("Registrazione completata.");
                        switchTo(HomeController.create(stage(), dataSource(), getSessionHolder()));
                    } else {
                        ViewUtils.showError("Creazione dell'account non riuscita.");
                    }
                }
            } catch (Exception e) {
                ViewUtils.showError(e.getMessage());
            }
        }
    }

    protected abstract void initOther(final Connection conn);

    protected abstract int abstractInsert(final int personId, final Connection conn);

    protected abstract boolean areFieldsInvalid();

    protected int getIdCodeLimit() {
        return ID_CODE_LIMIT;
    }

    protected int getPostCodeLimit() {
        return POSTCODE_LIMIT;
    }

    protected int getProvinceLimit() {
        return PROVINCE_LIMIT;
    }

    protected int getPasswordMax() {
        return PASSWORD_MAX;
    }

    protected int getPasswordMin() {
        return PASSWORD_MIN;
    }

    @FXML
    private void backToHome(ActionEvent event) {
        ViewUtils.showConfirmationDialog(
                "Vuoi davvero tornare al menu principale? Tutti i dati inseriti verranno persi.",
                () -> switchTo(HomeController.create(stage(), dataSource(), getSessionHolder())));
    }
}
