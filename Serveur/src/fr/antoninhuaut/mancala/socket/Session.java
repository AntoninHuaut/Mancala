package fr.antoninhuaut.mancala.socket;

import fr.antoninhuaut.mancala.match.Game;
import fr.antoninhuaut.mancala.model.Player;

import java.io.IOException;

public class Session {

    private static int counter = 0;

    private int sessionId;
    private int nbPlayer = 0;

    private final Game game;

    public Session() {
        this.game = new Game(this);
        this.sessionId = Session.counter++;
    }

    public int getNbPlayer() {
        return nbPlayer;
    }

    public void addPlayer(Player p) throws IOException, ClassNotFoundException {
        this.nbPlayer++;
        game.addPlayer(p);
    }

    public void removePlayer(Player p) {
        this.nbPlayer--;
        game.removePlayer(p);

        if (nbPlayer == 0) {
            SessionHandler.destroySession(this);
        }
    }

    public int getSessionId() {
        return sessionId;
    }
}
