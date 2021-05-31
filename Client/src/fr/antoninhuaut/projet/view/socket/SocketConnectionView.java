package fr.antoninhuaut.projet.view.socket;

import fr.antoninhuaut.projet.controller.socket.SocketConnectionController;
import fr.antoninhuaut.projet.view.global.FXMainView;
import fr.antoninhuaut.projet.view.global.HomeView;

public class SocketConnectionView extends FXMainView<SocketConnectionController> {

    private static final String PATH = "socket/SocketConnection.fxml";

    public SocketConnectionView(HomeView homeView) {
        super(PATH, new SocketConnectionController(homeView), homeView);
    }
}
