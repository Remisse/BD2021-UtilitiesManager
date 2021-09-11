package bdproject.controller.gui;

import bdproject.model.SessionHolder;

public interface Controller {

    void switchTo(Controller controller);

    void createSubWindow(Controller controller);

    SessionHolder sessionHolder();

    String fxml();
}
