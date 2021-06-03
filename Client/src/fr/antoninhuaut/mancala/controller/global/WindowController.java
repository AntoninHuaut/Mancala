package fr.antoninhuaut.mancala.controller.global;

import fr.antoninhuaut.mancala.controller.socket.MancalaSocket;
import fr.antoninhuaut.mancala.utils.components.alert.ConfirmAlert;
import fr.antoninhuaut.mancala.view.global.FXView;
import fr.antoninhuaut.mancala.view.global.HomeView;
import fr.antoninhuaut.mancala.view.global.ToolbarView;
import fr.antoninhuaut.mancala.view.global.WindowView;
import fr.antoninhuaut.mancala.view.socket.SocketConnectionView;
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
        WindowController.instance = this;
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

    @FXML
    public void onHomeMenuClick() {
        var mancalaSocket = homeView.getController().getMancalaSocket();
        if (mancalaSocket == null) {
            new SocketConnectionView(this.homeView).load();
        } else {
            new ConfirmAlert("window.home.ingame", () -> {
                mancalaSocket.disconnect(); // Will load to SocketConnectionView
                homeView.getController().setMancalaSocket(null);
            }).showAndWait();
        }
    }

    public void disconnectForClose() {
        var mancalaSocket = homeView.getController().getMancalaSocket();
        if (mancalaSocket != null) {
            mancalaSocket.disconnect();
            homeView.getController().setMancalaSocket(null);
        }
    }

    public void setCurrentController(FXView<?> view) {
        this.currentShowView = view;
    }

    public static WindowController getInstance() {
        return instance;
    }
}