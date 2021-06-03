package fr.antoninhuaut.mancala.controller.global;

import fr.antoninhuaut.mancala.controller.socket.MancalaSocket;
import fr.antoninhuaut.mancala.utils.I18NUtils;
import fr.antoninhuaut.mancala.utils.components.alert.AboutAlert;
import fr.antoninhuaut.mancala.utils.components.alert.ConfirmAlert;
import fr.antoninhuaut.mancala.view.global.FXView;
import fr.antoninhuaut.mancala.view.global.HomeView;
import fr.antoninhuaut.mancala.view.global.ToolbarView;
import fr.antoninhuaut.mancala.view.global.WindowView;
import fr.antoninhuaut.mancala.view.socket.SocketConnectionView;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

import java.util.Locale;

public class WindowController extends FXController {

    private static WindowController instance;

    private FXView<?> currentShowView = null;

    private HomeView homeView;
    private ToolbarView toolbarView;
    private WindowView windowView;

    @FXML
    public AnchorPane homeContainer, paneContainer;

    @FXML
    public ImageView enFlag, frFlag;

    @FXML
    public ImageView homeIcon, helpIcon;

    public WindowController() {
        WindowController.instance = this;
    }

    public void setView(WindowView view) {
        this.windowView = view;
    }

    @Override
    public void postLoad() {
        setFlagVisibility();

        this.homeView = new HomeView(this.homeContainer, this.paneContainer);
        this.homeView.load();
        this.toolbarView = new ToolbarView(this.windowView.getBorderPane());
        this.toolbarView.load();

        new SocketConnectionView(this.homeView).load();

        setImgHoverColor(this.homeIcon, YELLOW);
        setImgHoverColor(this.helpIcon, YELLOW);
    }

    private void loadLang(Locale locale) {
        I18NUtils.getInstance().setLocale(locale);

        setFlagVisibility();
        this.homeView.load();
        this.toolbarView.load();

        if (this.currentShowView != null) {
            this.currentShowView.load();
        }
    }

    private void setFlagVisibility() {
        this.enFlag.setVisible(!I18NUtils.getInstance().getLocale().equals(getLocale(I18NUtils.SupportLanguage.EN)));
        this.frFlag.setVisible(!I18NUtils.getInstance().getLocale().equals(getLocale(I18NUtils.SupportLanguage.FR)));
    }

    private Locale getLocale(I18NUtils.SupportLanguage lang) {
        return Locale.forLanguageTag(lang.toString().toLowerCase());
    }

    @FXML
    public void onENFlagClick() {
        loadLang(getLocale(I18NUtils.SupportLanguage.EN));
    }

    @FXML
    public void onFRFlagClick() {
        loadLang(getLocale(I18NUtils.SupportLanguage.FR));
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

    @FXML
    public void onAboutClick() {
        new AboutAlert();
    }

    public void setCurrentController(FXView<?> view) {
        this.currentShowView = view;
    }

    public static WindowController getInstance() {
        return instance;
    }
}