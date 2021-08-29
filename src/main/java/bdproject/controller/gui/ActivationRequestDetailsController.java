package bdproject.controller.gui;

import bdproject.tables.pojos.RichiesteAttivazione;
import bdproject.utils.LocaleUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

import javax.sql.DataSource;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.ResourceBundle;

public class ActivationRequestDetailsController extends AbstractViewController implements Initializable {

    private static final String FXML_FILE = "requestDetails.fxml";
    private final RichiesteAttivazione request;

    private final DateTimeFormatter dateIt = LocaleUtils.getItDateFormatter();
    private final Map<String, String> mUnit = LocaleUtils.getItUtilitiesUnits();

    @FXML private Button back;
    @FXML private TextFlow clientDetails;
    @FXML private TextFlow planDetails;
    @FXML private TextFlow premisesDetails;

    @FXML private Label peopleNoName;
    @FXML private Label peopleNo;
    @FXML private Label subStartDate;
    @FXML private Label use;
    @FXML private Label activation;

    protected ActivationRequestDetailsController(final Stage stage, final DataSource dataSource,
            final RichiesteAttivazione request) {
        super(stage, dataSource, FXML_FILE);
        this.request = request;
    }

    public static ViewController create(final Stage stage, final DataSource dataSource,
            final RichiesteAttivazione request) {
        return new ActivationRequestDetailsController(stage, dataSource, request);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
