package fr.antoninhuaut.projet.controller.game;

import fr.antoninhuaut.projet.controller.global.FXController;
import fr.antoninhuaut.projet.controller.socket.MancalaSocket;
import fr.antoninhuaut.projet.utils.I18NUtils;
import fr.antoninhuaut.projet.view.global.HomeView;
import fr.antoninhuaut.projet.view.socket.SocketConnectionView;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class GameController extends FXController {

    private final HomeView homeView;
    private final MancalaSocket mancalaSocket;

    @FXML
    public Label infosLabel;
    @FXML
    public Label pPosLabel;

    private boolean isYourTurn = false;

    public GameController(MancalaSocket mancalaSocket) {
        this.homeView = mancalaSocket.getHomeView();
        this.mancalaSocket = mancalaSocket;
    }

    @Override
    public void postLoad() {
        new Thread(() -> {
            try {
                mancalaSocket.start(this);
            } catch (Exception ex) {
                ex.printStackTrace();
                // TODO alert?
                new SocketConnectionView(homeView).load();
            }
        }).start();
    }

    public void initWelcome(String playerNumberResponse) {
        String i18n = "game.info." + playerNumberResponse.toLowerCase();
        pPosLabel.textProperty().bind(I18NUtils.getInstance().bindStr(i18n)); // ex: game.info.player_two
    }

    public void initPlayer(boolean isYourTurn) {
        this.isYourTurn = isYourTurn;
        // TODO waiting opponent
        if (isYourTurn) {
            setInfosLabelI18N("game.info.turn_you");
        } else {
            setInfosLabelI18N("game.info.turn_opponent");
        }
    }

    public void setInfosLabelI18N(String i18nKey) {
        if (infosLabel.textProperty().isBound()) infosLabel.textProperty().unbind();
        infosLabel.textProperty().bind(I18NUtils.getInstance().bindStr(i18nKey));
    }
}