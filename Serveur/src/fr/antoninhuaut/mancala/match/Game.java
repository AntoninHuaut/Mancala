package fr.antoninhuaut.mancala.match;

import fr.antoninhuaut.mancala.model.PlayerData;
import fr.antoninhuaut.mancala.socket.Player;
import fr.antoninhuaut.mancala.socket.Session;
import fr.antoninhuaut.mancala.socket.cenum.ServerToClientEnum;

import java.util.Arrays;

public class Game {

    private transient Session session;

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

        if (getNbRound() >= 6) {
            endGame();
        } else {
            this.currentRound = new Round().create().init(this);
            sendGlobalUpdate();
        }
    }

    public void reloadRound() {
        if (getNbRound() >= 6) {
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

        for (Player p : session.getNoNullPlayers()) {
            p.sendData(ServerToClientEnum.END_GAME, "" + winnerId);
        }
    }

    public void forceStopMatch() {
        for (Player pLoop : session.getNoNullPlayers()) {
            pLoop.sendData(ServerToClientEnum.MESSAGE, ServerToClientEnum.MessageEnum.STOP_MATCH.name());
        }
        endGame();
    }

    public synchronized void addPlayer(Player p) {
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
            for (Player pLoop : session.getNoNullPlayers()) {
                pLoop.sendData(ServerToClientEnum.WAIT_OPPONENT);
            }
        } else if (nbPlayer == 2) {
            currentRound.initPostPlayersJoined();
            session.getPOne().sendData(ServerToClientEnum.OPPONENT_NAME, session.getPlayersData()[1].getUsername());
            session.getPTwo().sendData(ServerToClientEnum.OPPONENT_NAME, session.getPlayersData()[0].getUsername());
        }
    }

    public int getNbRound() {
        return Arrays.stream(session.getPlayersData()).mapToInt(PlayerData::getNbRoundWin).reduce(Integer::sum).getAsInt();
    }

    public synchronized void removePlayer() {
        for (Player pLoop : session.getNoNullPlayers()) {
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
