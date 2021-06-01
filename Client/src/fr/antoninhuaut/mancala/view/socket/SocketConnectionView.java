package fr.antoninhuaut.mancala.view.socket;

import fr.antoninhuaut.mancala.controller.socket.SocketConnectionController;
import fr.antoninhuaut.mancala.view.global.FXMainView;
import fr.antoninhuaut.mancala.view.global.HomeView;

public class SocketConnectionView extends FXMainView<SocketConnectionController> {

    private static final String PATH = "socket/SocketConnection.fxml";

    public SocketConnectionView(HomeView homeView) {
        super(PATH, new SocketConnectionController(homeView), homeView);
    }
}
