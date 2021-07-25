package bdproject.controller.gui;

import bdproject.controller.gui.userarea.UserAreaController;
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
import org.jooq.Record3;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import javax.sql.DataSource;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.ResourceBundle;

import static bdproject.Tables.PERSONE_FISICHE;

public class HomeController extends AbstractViewController implements Initializable {

    private static final String fxml = "home.fxml";

    @FXML
    private TextField email;
    @FXML
    private PasswordField password;
    @FXML
    private Label loginLabel;
    @FXML
    private Button login;
    @FXML
    private Label greeting;
    @FXML
    private Button logout;
    @FXML
    private Button userArea;
    @FXML
    private Label signupLabel;
    @FXML
    private Button signupButton;
    @FXML
    private Button adminArea;

    private HomeController(final Stage stage, final DataSource dataSource) {
        super(stage, dataSource, fxml);
    }

    public static ViewController create(Stage stage, DataSource dataSource) {
        return new HomeController(stage, dataSource);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        updateSigninElements();
    }

    private void updateSigninElements() {
        SessionHolder.get().ifPresentOrElse(s -> {
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

            adminArea.setVisible(s.isAdmin());
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

            adminArea.setVisible(false);
        });



    }

    @FXML
    private void viewCatalogue(ActionEvent event) {
        switchTo(CatalogueController.create(getStage(), getDataSource()));
    }

    @FXML
    private void viewSignupPage(ActionEvent event) {
        switchTo(SignupController.create(getStage(), getDataSource()));
    }

    @FXML
    private void doLogin(ActionEvent event) {
        try (Connection conn = getDataSource().getConnection()) {
            DSLContext query = DSL.using(conn, SQLDialect.MYSQL);

            Optional<Record3<Integer, Byte, String>> user = query.select(
                        PERSONE_FISICHE.CODICECLIENTE,
                        PERSONE_FISICHE.AMMINISTRATORE,
                        PERSONE_FISICHE.NOME)
                    .from(PERSONE_FISICHE)
                    .where(PERSONE_FISICHE.EMAIL.eq(email.getText()))
                    .and(PERSONE_FISICHE.PASSWORD.eq(password.getText()))
                    .fetchOptional();
            user.ifPresentOrElse(u -> {
                        SessionHolder.create(u.component1(), u.component2(), u.component3());
                        updateSigninElements();
                    }, () -> FXUtils.showBlockingWarning("Indirizzo e-mail o password errati."));

        } catch (SQLException | NoSuchElementException e) {
            FXUtils.showError(e.getMessage());
        }
    }

    @FXML
    private void doLogout(ActionEvent event) {
        SessionHolder.disconnect();
        updateSigninElements();
        FXUtils.showBlockingWarning("Disconnessione avvenuta con successo.");
    }

    @FXML
    private void toUserArea(ActionEvent e) {
        switchTo(UserAreaController.create(getStage(), getDataSource()));
    }

    @FXML
    private void toAdminArea(ActionEvent e) {

    }
}
