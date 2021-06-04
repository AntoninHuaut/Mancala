package fr.antoninhuaut.mancala.controller.global;

import fr.antoninhuaut.mancala.controller.game.GameController;
import fr.antoninhuaut.mancala.controller.socket.MancalaSocket;
import fr.antoninhuaut.mancala.model.enums.ClientToServerEnum;
import fr.antoninhuaut.mancala.view.global.HomeView;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;

public class HomeController extends FXController {

    private HomeView homeView;
    private ObjectProperty<MancalaSocket> mancalaSocket = new SimpleObjectProperty<>();

    @FXML
    public VBox mancalaMenu;

    public void setView(HomeView homeView) {
        this.homeView = homeView;
    }

    @Override
    public void postLoad() {
        mancalaMenu.visibleProperty().bind(mancalaSocket.isNotNull());
    }

    @FXML
    public void undo() {
        mancalaSocket.get().sendData(ClientToServerEnum.UNDO);
    }

    public void setMancalaSocket(MancalaSocket mancalaSocket) {
        this.mancalaSocket.set(mancalaSocket);
    }

    protected MancalaSocket getMancalaSocket() {
        return mancalaSocket.get();
    }

    private GameController getGameController() {
        return mancalaSocket.get().getGameController();
    }
}