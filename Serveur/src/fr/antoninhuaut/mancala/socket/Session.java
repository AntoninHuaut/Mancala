package fr.antoninhuaut.mancala.socket;

import fr.antoninhuaut.mancala.match.Game;
import fr.antoninhuaut.mancala.model.PlayerData;
import fr.antoninhuaut.mancala.socket.cenum.ServerToClientEnum;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Session {

    private static int counter = 0;
    private final int sessionId;

    private final PlayerData[] playersData = new PlayerData[2];

    private Player pOne;
    private Player pTwo;

    private int nbPlayer = 0;

    private Game game;

    public Session() {
        for (var i = 0; i < playersData.length; i++) {
            playersData[i] = new PlayerData(i);
        }

        this.game = new Game(this);
        this.sessionId = Session.counter++;
    }

    public int getNbPlayer() {
        return nbPlayer;
    }

    public void addPlayer(Player p) throws IOException, ClassNotFoundException {
        this.nbPlayer++;
        boolean isPlayerOne;

        if (pOne == null) {
            pOne = p;
            isPlayerOne = true;
        } else {
            pTwo = p;
            isPlayerOne = false;
        }

        p.waitSessionSetup(this, isPlayerOne);
        game.addPlayer(p);
    }

    public void removePlayer(Player p) {
        this.nbPlayer--;
        if (pOne != null && pOne.equals(p)) {
            pOne = null;
        } else if (pTwo != null && pTwo.equals(p)) {
            pTwo = null;
        }

        game.removePlayer();

        if (nbPlayer == 0) {
            SessionHandler.destroySession(this);
        }
    }

    public PlayerData[] getPlayersData() {
        return playersData;
    }

    public void setPlayerName(int playerId, String username) {
        this.playersData[playerId].setUsername(username);
    }

    public Game getGame() {
        return game;
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

    public List<Player> getNoNullPlayers() {
        return Stream.of(pOne, pTwo).filter(Objects::nonNull).collect(Collectors.toList());
    }

    public int getSessionId() {
        return sessionId;
    }

    public void newGame() {
        game.endGame();
        for (PlayerData playerData: playersData) {
            playerData.reset();
        }
        this.game = new Game(this);

        for (Player p : getNoNullPlayers()) {
            game.addPlayer(p);
        }

        for (Player p : getNoNullPlayers()) {
            p.sendData(ServerToClientEnum.BAD_STATE, ServerToClientEnum.BadStateEnum.NEW_MATCH.name());
        }
    }
}