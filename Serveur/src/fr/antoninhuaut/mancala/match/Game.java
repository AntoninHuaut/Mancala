package fr.antoninhuaut.mancala.match;

import fr.antoninhuaut.mancala.model.PlayerData;
import fr.antoninhuaut.mancala.socket.Player;
import fr.antoninhuaut.mancala.socket.Session;
import fr.antoninhuaut.mancala.socket.cenum.ServerToClientEnum;

import java.io.IOException;

public class Game {

    private Player pOne;
    private Player pTwo;

    private PlayerData[] playersData = new PlayerData[2];

    private final Session session;

    private Round currentRound;
    private int nbRound;

    public Game(Session session) {
        this.session = session;
        this.nbRound = 0;

        for (var i = 0; i < playersData.length; i++) {
            playersData[i] = new PlayerData(i);
        }
        nextRound();
    }

    public void nextRound() {
        if (nbRound == 6) {
            // TODO
        } else {
            resetPlayers();
            nbRound++;
            this.currentRound = new Round(this);
            sendGlobalUpdate();
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
        sendGlobalUpdate();
    }

    private synchronized void resetPlayers() {
        for (PlayerData pData : playersData) {
            pData.resetScore();
        }
    }

    private synchronized void sendGlobalUpdate() {
        var nbPlayer = session.getNbPlayer();

        if (nbPlayer == 1) {
            (pOne != null ? pOne : pTwo).sendData(ServerToClientEnum.WAIT_OPPONENT);
        } else if (nbPlayer == 2) {
            currentRound.initPostPlayersJoined();
            pOne.sendData(ServerToClientEnum.OPPONENT_NAME, playersData[1].getUsername());
            pTwo.sendData(ServerToClientEnum.OPPONENT_NAME, playersData[0].getUsername());
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

    public PlayerData[] getPlayersData() {
        return playersData;
    }

    public void setPlayerName(int playerId, String username) {
        this.playersData[playerId].setUsername(username);
    }
}
