package fr.antoninhuaut.projet.view.game;

import fr.antoninhuaut.projet.controller.game.GameController;
import fr.antoninhuaut.projet.controller.socket.MancalaSocket;
import fr.antoninhuaut.projet.controller.socket.SocketConnectionController;
import fr.antoninhuaut.projet.view.global.FXMainView;
import fr.antoninhuaut.projet.view.global.HomeView;

public class GameView extends FXMainView<GameController> {

    private static final String PATH = "game/game.fxml";

    public GameView(MancalaSocket mancalaSocket) {
        super(PATH, new GameController(mancalaSocket), mancalaSocket.getHomeView());
    }
}
