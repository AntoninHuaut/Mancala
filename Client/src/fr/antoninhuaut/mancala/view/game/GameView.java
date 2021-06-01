package fr.antoninhuaut.mancala.view.game;

import fr.antoninhuaut.mancala.controller.game.GameController;
import fr.antoninhuaut.mancala.controller.socket.MancalaSocket;
import fr.antoninhuaut.mancala.view.global.FXMainView;

public class GameView extends FXMainView<GameController> {

    private static final String PATH = "game/game.fxml";

    public GameView(MancalaSocket mancalaSocket) {
        super(PATH, new GameController(mancalaSocket), mancalaSocket.getHomeView());
    }
}
