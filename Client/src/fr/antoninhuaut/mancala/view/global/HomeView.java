package fr.antoninhuaut.mancala.view.global;

import fr.antoninhuaut.mancala.controller.global.HomeController;
import javafx.scene.layout.AnchorPane;

public class HomeView extends FXView<HomeController> {

    private static final String PATH = "global/Home.fxml";

    private final AnchorPane parent;
    private final AnchorPane paneContainer;

    public HomeView(AnchorPane parent, AnchorPane paneContainer) {
        super(PATH, new HomeController());
        this.controller.setView(this);
        this.parent = parent;
        this.paneContainer = paneContainer;
    }

    @Override
    public void load() {
        loadAndSetPosition(this.parent.getChildren()::add);
    }

    public AnchorPane getPaneContainer() {
        return this.paneContainer;
    }
}