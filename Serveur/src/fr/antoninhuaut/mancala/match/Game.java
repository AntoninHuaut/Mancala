package fr.antoninhuaut.mancala.match;

import fr.antoninhuaut.mancala.model.Player;
import fr.antoninhuaut.mancala.socket.Session;

import java.io.IOException;

public class Game {

    private Player pOne;
    private Player pTwo;

    private final Session session;

    private Round currentRound;
    private int nbRound;

    public Game(Session session) {
        this.session = session;
        this.nbRound = 0;
        startGame();
    }

    public void startGame() {
        for (var i = 0; i < 6; i++) {
            this.currentRound = new Round(this);
        }
    }

    public synchronized void addPlayer(Player p) throws IOException, ClassNotFoundException {
        boolean isPlayerOne;
        if (pOne == null) {
            pOne = p;
            isPlayerOne = true;
        } else {
            pTwo = p;
            isPlayerOne = false;
        }

        p.setup(this, isPlayerOne);

        if (session.getNbPlayer() == 1) {
            p.sendData("MESSAGE WAIT_OPPONENT");
        } else {
            currentRound.initPostPlayersJoined();
            pOne.sendData("OPPONENT_NAME " + pTwo.getUsername());
            pTwo.sendData("OPPONENT_NAME " + pOne.getUsername());
        }
    }

    public synchronized void removePlayer(Player p) {
        if (pOne != null && pOne.getPlayerId() == p.getPlayerId()) {
            pOne = null;

            if (pTwo != null) {
                pTwo.sendData("MESSAGE WAIT_OPPONENT");
            }
        } else if (pTwo != null && pTwo.getPlayerId() == p.getPlayerId()) {
            pTwo = null;

            if (pOne != null) {
                pOne.sendData("MESSAGE WAIT_OPPONENT");
            }
        }
    }

    public Player getPOne() {
        return pOne;
    }

    public Player getPTwo() {
        return pTwo;
    }

    public Player getOppositePlayer(Player playerAtm) {
        return playerAtm.getPlayerId() == getPOne().getPlayerId() ? getPTwo() : getPOne();
    }

    public Round getCurrentRound() {
        return currentRound;
    }

    public Session getSession() {
        return session;
    }
}
