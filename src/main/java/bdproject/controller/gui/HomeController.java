package bdproject.controller.gui;

import bdproject.controller.gui.operators.AreaSelectorController;
import bdproject.controller.gui.users.CatalogueController;
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
import java.util.ResourceBundle;

public class HomeController extends AbstractController implements Initializable {

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

    private HomeController(final Stage stage, final DataSource dataSource, final SessionHolder holder) {
        super(stage, dataSource, holder, FXML_FILE);
    }

    public static Controller create(final Stage stage, final DataSource dataSource, final SessionHolder holder) {
        return new HomeController(stage, dataSource, holder);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        updateSignInElements();
    }

    private void updateSignInElements() {
        getSessionHolder().session().ifPresentOrElse(s -> {
            email.setVisible(false);
            password.setVisible(false);
            password.setText("");
            loginLabel.setVisible(false);
            login.setVisible(false);

            greeting.setVisible(true);
            greeting.setText("Ciao, " + s.username());
            logout.setVisible(true);
            userArea.setVisible(true);

            signupLabel.setVisible(false);
            signupButton.setVisible(false);

            catalogueLabel.setVisible(!s.isOperator());
            catalogueButton.setVisible(!s.isOperator());

            adminArea.setVisible(s.isOperator());
            userArea.setVisible(!s.isOperator());
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
            userArea.setVisible(false);
        });
    }

    @FXML
    private void viewCatalogue(ActionEvent event) {
        switchTo(CatalogueController.create(stage(), dataSource(), getSessionHolder()));
    }

    @FXML
    private void viewSignupPage(ActionEvent event) {
        switchTo(UserSignUpController.create(stage(), dataSource(), getSessionHolder()));
    }

    @FXML
    private void doLogin(ActionEvent event) {
        try (Connection conn = dataSource().getConnection()) {
            DSLContext query = DSL.using(conn, SQLDialect.MYSQL);

            Queries.fetchPersonIdAndName(email.getText(), password.getText(), query)
                .ifPresentOrElse(u -> {
                    final boolean isOperator = Queries.isOperator(u.component1(), conn);
                    switchTo(HomeController.create(stage(), dataSource(), SessionHolder.of(
                            u.component1(),
                            u.component2(),
                            isOperator
                    )));
                }, () -> FXUtils.showBlockingWarning("Indirizzo e-mail o password errati."));
        } catch (Exception e) {
            FXUtils.showError(e.getMessage());
        }
    }

    @FXML
    private void doLogout(ActionEvent event) {
        switchTo(HomeController.create(stage(), dataSource(), SessionHolder.empty()));
        FXUtils.showBlockingWarning("Disconnessione avvenuta con successo.");
    }

    @FXML
    private void toUserArea(ActionEvent e) {
        switchTo(UserAreaController.create(stage(), dataSource(), getSessionHolder()));
    }

    @FXML
    private void toAdminArea(ActionEvent e) {
        switchTo(AreaSelectorController.create(stage(), dataSource(), getSessionHolder()));
    }
}
