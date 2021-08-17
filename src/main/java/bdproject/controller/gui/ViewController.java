package bdproject.controller.gui;

public interface ViewController {

    void switchTo(ViewController controller);

    void createSubWindow(ViewController controller);

    String getFxml();
}
