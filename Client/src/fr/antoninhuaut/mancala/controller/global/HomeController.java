package fr.antoninhuaut.mancala.controller.global;

import fr.antoninhuaut.mancala.controller.socket.MancalaSocket;
import fr.antoninhuaut.mancala.model.enums.ClientToServerEnum;
import fr.antoninhuaut.mancala.utils.components.dialog.CodeDialog;
import fr.antoninhuaut.mancala.utils.components.alert.RuleAlert;
import fr.antoninhuaut.mancala.utils.components.dialog.SettingsDialog;
import fr.antoninhuaut.mancala.view.global.HomeView;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

public class HomeController extends FXController {

    private HomeView homeView;
    private ObjectProperty<MancalaSocket> mancalaSocket = new SimpleObjectProperty<>();

    @FXML
    public VBox mancalaMenu;

    @FXML
    public Button highscoreBtn;

    public void setView(HomeView homeView) {
        this.homeView = homeView;
    }

    @Override
    public void postLoad() {
        mancalaMenu.visibleProperty().bind(mancalaSocket.isNotNull());
        mancalaMenu.managedProperty().bind(mancalaMenu.visibleProperty());
        highscoreBtn.disableProperty().bind(mancalaSocket.isNull());
    }

    @FXML
    public void saveMatch() {
        mancalaSocket.get().sendData(ClientToServerEnum.SAVE_MATCH);
    }

    @FXML
    public void loadMatch() {
        new CodeDialog("game.savemanager.load_save", mancalaSocket.get().getSessionId(),
                code -> mancalaSocket.get().sendData(ClientToServerEnum.LOAD_MATCH, code));
    }

    @FXML
    public void surrender() {
        mancalaSocket.get().sendData(ClientToServerEnum.SOLO_SURRENDER);
    }

    @FXML
    public void newMatch() {
        mancalaSocket.get().sendData(ClientToServerEnum.NEW_MATCH);
    }

    @FXML
    public void stopMatch() {
        mancalaSocket.get().sendData(ClientToServerEnum.STOP_MATCH);
    }

    @FXML
    public void undo() {
        mancalaSocket.get().sendData(ClientToServerEnum.UNDO);
    }

    @FXML
    public void seeRule() {
        new RuleAlert("home.rule").showAndWait();
    }

    @FXML
    public void highscore() {
        mancalaSocket.get().sendData(ClientToServerEnum.ASK_HIGHSCORE);
    }

    @FXML
    public void settings() {
        new SettingsDialog("home.settings").showAndWait();
    }

    public void setMancalaSocket(MancalaSocket mancalaSocket) {
        this.mancalaSocket.set(mancalaSocket);
    }

    protected MancalaSocket getMancalaSocket() {
        return mancalaSocket.get();
    }
}