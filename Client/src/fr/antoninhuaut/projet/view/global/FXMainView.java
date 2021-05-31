package fr.antoninhuaut.projet.view.global;

import fr.antoninhuaut.projet.controller.global.FXController;
import fr.antoninhuaut.projet.controller.global.WindowController;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public abstract class FXMainView<T extends FXController> extends FXView<T> {

    private final HomeView homeView;

    public FXMainView(String path, T controller, HomeView homeView) {
        super(path, controller);
        this.homeView = homeView;
    }

    @Override
    public void load() {
        WindowController.getInstance().setCurrentController(this);

        new Thread(() -> {
            FXMLLoader loader = loadFXML(this.path);
            loader.setController(this.controller);
            try {
                Node node = loader.load();

                Platform.runLater(() -> {
                    setConstraint(node);
                    this.homeView.getPaneContainer().getChildren().clear();
                    this.homeView.getPaneContainer().getChildren().add(node);
                    this.controller.postLoad();
                });
            } catch (IOException ex) {
                // Ignore
                ex.printStackTrace();
            }
        }).start();
    }

    public HomeView getHomeView() {
        return this.homeView;
    }

    private void setConstraint(Node node) {
        AnchorPane.setTopAnchor(node, 0.0);
        AnchorPane.setBottomAnchor(node, 0.0);
        AnchorPane.setRightAnchor(node, 0.0);
        AnchorPane.setLeftAnchor(node, 0.0);
    }
}
