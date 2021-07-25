package bdproject;

import bdproject.controller.gui.HomeController;
import com.mysql.cj.jdbc.MysqlDataSource;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.util.Locale;

public class AppLoader extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        final String username = "root";
        final String password = "BDFC4JKX4hefpJBUqDO1";
        final String serverName = "localhost";

        MysqlDataSource dataSource = new MysqlDataSource();
        dataSource.setUser(username);
        dataSource.setPassword(password);
        dataSource.setServerName(serverName);

        Locale.setDefault(Locale.ITALY);
        final FXMLLoader loader = new FXMLLoader(ClassLoader.getSystemResource("home.fxml"));
        loader.setController(HomeController.create(primaryStage, dataSource));
        Pane pane = loader.load();
        primaryStage.setScene(new Scene(pane));
        primaryStage.setResizable(false);
        primaryStage.setTitle("Utenze");
        primaryStage.show();
    }
}
