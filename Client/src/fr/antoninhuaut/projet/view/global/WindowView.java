package fr.antoninhuaut.projet.view.global;

import fr.antoninhuaut.projet.controller.global.WindowController;
import javafx.scene.layout.BorderPane;

public class WindowView extends FXView<WindowController> {

    private static final String PATH = "global/Window.fxml";

    private final BorderPane borderPane;

    public WindowView(BorderPane borderPane) {
        super(PATH, new WindowController());
        this.controller.setView(this);
        this.borderPane = borderPane;
    }

    @Override
    public void load() {
        loadAndSetPosition(this.borderPane::setCenter);
    }

    public BorderPane getBorderPane() {
        return this.borderPane;
    }
}