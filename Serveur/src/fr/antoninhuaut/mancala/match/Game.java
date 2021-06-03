package fr.antoninhuaut.mancala.match;

import fr.antoninhuaut.mancala.model.Player;
import fr.antoninhuaut.mancala.socket.Session;
import fr.antoninhuaut.mancala.socket.cenum.ServerToClientEnum;

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

        p.waitSetup(this, isPlayerOne);

        if (session.getNbPlayer() == 1) {
            p.sendData(ServerToClientEnum.WAIT_OPPONENT);
        } else {
            currentRound.initPostPlayersJoined();
            pOne.sendData(ServerToClientEnum.OPPONENT_NAME, pTwo.getUsername());
            pTwo.sendData(ServerToClientEnum.OPPONENT_NAME, pOne.getUsername());
        }
    }

    public synchronized void removePlayer(Player p) {
        if (pOne != null && pOne.equals(p)) {
            pOne = null;

            if (pTwo != null) {
                pTwo.sendData(ServerToClientEnum.WAIT_OPPONENT);
            }
        } else if (pTwo != null && pTwo.equals(p)) {
            pTwo = null;

            if (pOne != null) {
                pOne.sendData(ServerToClientEnum.WAIT_OPPONENT);
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
        return playerAtm.equals(getPOne()) ? getPTwo() : getPOne();
    }

    public Round getCurrentRound() {
        return currentRound;
    }

    public Session getSession() {
        return session;
    }
}
