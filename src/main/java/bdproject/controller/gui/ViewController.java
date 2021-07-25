package bdproject.controller.gui;

public interface ViewController {

    void switchTo(ViewController controller);

    String getFxml();
}
