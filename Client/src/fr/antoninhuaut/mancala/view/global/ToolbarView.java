package fr.antoninhuaut.mancala.view.global;

import fr.antoninhuaut.mancala.controller.global.ToolbarController;
import javafx.scene.layout.BorderPane;

public class ToolbarView extends FXView<ToolbarController> {

    private static final String PATH = "global/Toolbar.fxml";

    private final BorderPane borderPane;

    public ToolbarView(BorderPane borderPane) {
        super(PATH, new ToolbarController());
        this.borderPane = borderPane;
    }

    @Override
    public void load() {
        loadAndSetPosition(this.borderPane::setTop);
    }
}
