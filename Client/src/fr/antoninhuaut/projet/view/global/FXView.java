package fr.antoninhuaut.projet.view.global;

import fr.antoninhuaut.projet.controller.global.FXController;
import fr.antoninhuaut.projet.controller.global.HomeController;
import fr.antoninhuaut.projet.utils.I18NUtils;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;

import java.io.IOException;
import java.util.function.Consumer;

public abstract class FXView<T extends FXController> {

    protected final T controller;
    protected final String path;

    public FXView(String path, T controller) {
        this.path = path;
        this.controller = controller;
    }

    public T getController() {
        return this.controller;
    }

    public static FXMLLoader loadFXML(String path) {
        return new FXMLLoader(HomeController.class.getResource("/views/" + path), I18NUtils.getInstance().getResourceBundle());
    }

    public abstract void load();

    public void loadAndSetPosition(Consumer<Node> setNodePosition) {
        FXMLLoader loader = loadFXML(this.path);
        loader.setController(this.controller);
        try {
            Node node = loader.load();

            setNodePosition.accept(node);
            this.controller.postLoad();
        } catch (IOException ex) {
            // Ignore
            ex.printStackTrace();
        }
    }
}
