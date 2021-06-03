package fr.antoninhuaut.mancala.controller.global;

import fr.antoninhuaut.mancala.controller.socket.MancalaSocket;
import fr.antoninhuaut.mancala.view.global.HomeView;

public class HomeController extends FXController {

    private HomeView homeView;
    private MancalaSocket mancalaSocket;

    public void setView(HomeView homeView) {
        this.homeView = homeView;
    }

    @Override
    public void postLoad() {
    }

    public void setMancalaSocket(MancalaSocket mancalaSocket) {
        this.mancalaSocket = mancalaSocket;
    }

    protected MancalaSocket getMancalaSocket() {
        return mancalaSocket;
    }
}