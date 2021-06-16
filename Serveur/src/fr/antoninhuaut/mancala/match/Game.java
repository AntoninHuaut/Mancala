package fr.antoninhuaut.mancala.match;

import fr.antoninhuaut.mancala.model.PlayerData;
import fr.antoninhuaut.mancala.socket.player.ServerPlayer;
import fr.antoninhuaut.mancala.socket.Session;
import fr.antoninhuaut.mancala.socket.cenum.ServerToClientEnum;

import java.io.Serializable;

public class Game implements Serializable {

    private transient Session session;

    private int nbRound = 0;
    private Round currentRound;
    private boolean isGameFinished = false;

    public Game() {
        // For save loading
    }

    public Game init(Session session) {
        this.session = session;

        if (currentRound != null) {
            currentRound.init(this);
        }
        return this;
    }

    public void nextRound() {
        resetPlayers();

        if (nbRound >= 6) {
            endGame();
        } else {
            nbRound++;
            this.currentRound = new Round().create().init(this);
            sendGlobalUpdate();
        }
    }

    public void reloadRound() {
        if (nbRound >= 6) {
            endGame();
        } else {
            sendGlobalUpdate();
        }
    }

    public void endGame() {
        this.isGameFinished = true;
        PlayerData[] pData = session.getPlayersData();

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

        for (ServerPlayer p : session.getNoNullPlayers()) {
            p.sendData(ServerToClientEnum.END_GAME, "" + winnerId);
        }
    }

    public void forceStopMatch() {
        for (ServerPlayer pLoop : session.getNoNullPlayers()) {
            pLoop.sendData(ServerToClientEnum.MESSAGE, ServerToClientEnum.MessageEnum.STOP_MATCH.name());
        }
        endGame();
    }

    public synchronized void addPlayer(ServerPlayer p) {
        p.gameSetup();
        sendGlobalUpdate();
    }

    private synchronized void resetPlayers() {
        for (PlayerData pData : session.getPlayersData()) {
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
            for (ServerPlayer pLoop : session.getNoNullPlayers()) {
                pLoop.sendData(ServerToClientEnum.WAIT_OPPONENT);
            }
        } else if (nbPlayer == 2) {
            currentRound.initPostPlayersJoined();
            session.getPOne().sendData(ServerToClientEnum.OPPONENT_NAME, session.getPlayersData()[1].getUsername());
            session.getPTwo().sendData(ServerToClientEnum.OPPONENT_NAME, session.getPlayersData()[0].getUsername());
        }
    }

    public synchronized void removePlayer() {
        for (ServerPlayer pLoop : session.getNoNullPlayers()) {
            pLoop.sendData(ServerToClientEnum.WAIT_OPPONENT);
        }
    }

    public boolean isGameFinished() {
        return isGameFinished;
    }

    public Round getCurrentRound() {
        return currentRound;
    }

    public Session getSession() {
        return session;
    }
}
