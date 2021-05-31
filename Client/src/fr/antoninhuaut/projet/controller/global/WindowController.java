package fr.antoninhuaut.projet.controller.global;

import fr.antoninhuaut.projet.view.global.FXView;
import fr.antoninhuaut.projet.view.global.HomeView;
import fr.antoninhuaut.projet.view.global.ToolbarView;
import fr.antoninhuaut.projet.view.global.WindowView;
import fr.antoninhuaut.projet.view.socket.SocketConnectionView;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

public class WindowController extends FXController {

    private static WindowController instance;

    private FXView<?> currentShowView = null;

    private HomeView homeView;
    private ToolbarView toolbarView;
    private WindowView windowView;

    @FXML
    public AnchorPane homeContainer;
    @FXML
    public AnchorPane paneContainer;

    @FXML
    public ImageView homeIcon;
    @FXML
    public ImageView helpIcon;

    public WindowController() {
        instance = this;
    }

    public void setView(WindowView view) {
        this.windowView = view;
    }

    @Override
    public void postLoad() {
        this.homeView = new HomeView(this.homeContainer, this.paneContainer);
        this.homeView.load();
        this.toolbarView = new ToolbarView(this.windowView.getBorderPane());
        this.toolbarView.load();

        new SocketConnectionView(this.homeView).load();

        setImgHoverColor(this.homeIcon, YELLOW);
        setImgHoverColor(this.helpIcon, YELLOW);
    }

    public void setCurrentController(FXView<?> view) {
        this.currentShowView = view;
    }

    public static WindowController getInstance() {
        return instance;
    }
}