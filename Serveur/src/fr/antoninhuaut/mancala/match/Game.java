package fr.antoninhuaut.mancala.match;

import fr.antoninhuaut.mancala.model.PlayerData;
import fr.antoninhuaut.mancala.socket.Player;
import fr.antoninhuaut.mancala.socket.Session;
import fr.antoninhuaut.mancala.socket.cenum.ServerToClientEnum;

import java.io.IOException;
import java.util.Arrays;

public class Game {

    private Player pOne;
    private Player pTwo;

    private final PlayerData[] playersData = new PlayerData[2];
    private final Session session;

    private Round currentRound;
    private int nbRound;
    private boolean isGameFinished = false;

    public Game(Session session) {
        this.session = session;
        this.nbRound = 0;

        for (var i = 0; i < playersData.length; i++) {
            playersData[i] = new PlayerData(i);
        }
        nextRound();
    }

    public void nextRound() {
        resetPlayers();

        if (nbRound >= 6) {
            endGame();
        } else {
            nbRound++;
            this.currentRound = new Round(this);
            sendGlobalUpdate();
        }
    }

    public void endGame() {
        this.isGameFinished = true;
        PlayerData[] pData = getPlayersData();

        int winnerId;
        var roundOneWin = pData[0].getNbRoundWin();
        var roundTwoWin = pData[1].getNbRoundWin();

        if (roundOneWin > roundTwoWin) {
            winnerId = 0;
        } else if (roundTwoWin > roundOneWin) {
            winnerId = 1;
        } else {
            winnerId = -1;
        }

        for (Player p : Arrays.asList(pOne, pTwo)) {
            if (p != null) {
                p.sendData(ServerToClientEnum.END_GAME, "" + winnerId);
            }
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
        if (isGameFinished) {
            endGame();
            return;
        }

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

    public boolean isGameFinished() {
        return isGameFinished;
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
