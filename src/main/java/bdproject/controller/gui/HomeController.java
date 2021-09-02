package bdproject.controller.gui;

import bdproject.controller.gui.operators.AreaSelectorController;
import bdproject.controller.gui.users.UserAreaController;
import bdproject.controller.gui.users.UserSignUpController;
import bdproject.model.Queries;
import bdproject.model.SessionHolder;
import bdproject.utils.FXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import javax.sql.DataSource;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.NoSuchElementException;
import java.util.ResourceBundle;

import static bdproject.Tables.PERSONE;

public class HomeController extends AbstractViewController implements Initializable {

    private static final String FXML_FILE = "home.fxml";

    @FXML private TextField email;
    @FXML private PasswordField password;
    @FXML private Label loginLabel;
    @FXML private Button login;
    @FXML private Label greeting;
    @FXML private Button logout;
    @FXML private Button userArea;
    @FXML private Label signupLabel;
    @FXML private Button signupButton;
    @FXML private Button adminArea;
    @FXML private Label catalogueLabel;
    @FXML private Button catalogueButton;

    private HomeController(final Stage stage, final DataSource dataSource) {
        super(stage, dataSource, FXML_FILE);
    }

    public static ViewController create(Stage stage, DataSource dataSource) {
        return new HomeController(stage, dataSource);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        updateSignInElements();
    }

    private void updateSignInElements() {
        SessionHolder.getSession().ifPresentOrElse(s -> {
            email.setVisible(false);
            password.setVisible(false);
            password.setText("");
            loginLabel.setVisible(false);
            login.setVisible(false);

            greeting.setVisible(true);
            greeting.setText("Ciao, " + s.getName());
            logout.setVisible(true);
            userArea.setVisible(true);

            signupLabel.setVisible(false);
            signupButton.setVisible(false);

            catalogueLabel.setVisible(!s.isOperator());
            catalogueButton.setVisible(!s.isOperator());

            adminArea.setVisible(s.isOperator());
        }, () -> {
            email.setVisible(true);
            password.setVisible(true);
            password.setText("");
            loginLabel.setVisible(true);
            login.setVisible(true);

            greeting.setVisible(false);
            greeting.setText("Ciao");
            logout.setVisible(false);
            userArea.setVisible(false);

            signupLabel.setVisible(true);
            signupButton.setVisible(true);

            catalogueLabel.setVisible(true);
            catalogueButton.setVisible(true);

            adminArea.setVisible(false);
        });
    }

    @FXML
    private void viewCatalogue(ActionEvent event) {
        switchTo(CatalogueController.create(getStage(), getDataSource()));
    }

    @FXML
    private void viewSignupPage(ActionEvent event) {
        switchTo(UserSignUpController.create(getStage(), getDataSource()));
    }

    @FXML
    private void doLogin(ActionEvent event) {
        try (Connection conn = getDataSource().getConnection()) {
            DSLContext query = DSL.using(conn, SQLDialect.MYSQL);

            query.select(PERSONE.IDENTIFICATIVO, PERSONE.NOME)
                .from(PERSONE)
                .where(PERSONE.EMAIL.eq(email.getText()))
                .and(PERSONE.PASSWORD.eq(password.getText()))
                .fetchOptional()
                .ifPresentOrElse(u -> {
                    final boolean isOperator = Queries.isOperator(u.component1(), conn);
                    SessionHolder.create(u.component1(), isOperator, u.component2());
                    updateSignInElements();
                }, () -> FXUtils.showBlockingWarning("Indirizzo e-mail o password errati."));

        } catch (SQLException | NoSuchElementException e) {
            FXUtils.showError(e.getMessage());
        }
    }

    @FXML
    private void doLogout(ActionEvent event) {
        SessionHolder.disconnect();
        updateSignInElements();
        FXUtils.showBlockingWarning("Disconnessione avvenuta con successo.");
    }

    @FXML
    private void toUserArea(ActionEvent e) {
        switchTo(UserAreaController.create(getStage(), getDataSource()));
    }

    @FXML
    private void toAdminArea(ActionEvent e) {
        switchTo(AreaSelectorController.create(getStage(), getDataSource()));
    }
}
