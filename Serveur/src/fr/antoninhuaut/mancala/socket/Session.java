package fr.antoninhuaut.mancala.socket;

import fr.antoninhuaut.mancala.match.Game;
import fr.antoninhuaut.mancala.model.PlayerData;
import fr.antoninhuaut.mancala.save.SaveManager;
import fr.antoninhuaut.mancala.save.SaveState;
import fr.antoninhuaut.mancala.save.exception.SaveException;
import fr.antoninhuaut.mancala.save.exception.SaveLoadException;
import fr.antoninhuaut.mancala.socket.cenum.ServerToClientEnum;
import fr.antoninhuaut.mancala.socket.player.ClientBotPlayer;
import fr.antoninhuaut.mancala.socket.player.ServerPlayer;
import fr.antoninhuaut.mancala.socket.player.bot.CellMostSeedStrategy;
import fr.antoninhuaut.mancala.socket.player.bot.RandomStrategy;
import fr.antoninhuaut.mancala.socket.player.bot.Strategy;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Session {

    private static final Random random = new Random();
    private static final int SESSION_ID_LENGTH = 5;

    private final String sessionId;

    private final PlayerData[] playersData = new PlayerData[2];

    private Game game;

    private ServerPlayer pOne;
    private ServerPlayer pTwo;

    public Session(String sessionId) {
        for (var i = 0; i < playersData.length; i++) {
            playersData[i] = new PlayerData(i);
        }

        this.sessionId = sessionId == null ? generateSessionId() : sessionId;
        this.game = new Game().init(this);
        this.game.nextRound();
    }

    public int getNbPlayer() {
        var nbPlayer = 0;
        if (pOne != null) nbPlayer++;
        if (pTwo != null) nbPlayer++;
        return nbPlayer;
    }

    public void addBot(String strStrategy) {
        if (getNbPlayer() >= 2) return;

        var server = MancalaServer.getInstance();
        try {
            Strategy strategy = null;
            if (strStrategy.equalsIgnoreCase("random")) {
                strategy = new RandomStrategy();
            } else if (strStrategy.equalsIgnoreCase("mostseedcell")) {
                strategy = new CellMostSeedStrategy();
            }

            if (strategy == null) return;

            server.getPool().execute(new ClientBotPlayer(strategy, getSessionId(), server.getPort()));
        } catch (IOException ignored) {
        }
    }

    public void addPlayer(ServerPlayer p) throws IOException, ClassNotFoundException {
        if (getNbPlayer() >= 2) return;

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

    public void removePlayer(ServerPlayer p) {
        if (pOne != null && pOne.equals(p)) {
            pOne = null;
        } else if (pTwo != null && pTwo.equals(p)) {
            pTwo = null;
        }

        game.removePlayer();

        if (getNbPlayer() == 0) {
            SessionHandler.destroySession(this);
        }
    }

    public void stopAndStartNewGame() {
        stopGame();
        reLaunchGame(new Game(), ServerToClientEnum.MessageEnum.NEW_MATCH);
    }

    private void stopGame() {
        game.endGame();
        Arrays.stream(playersData).forEach(PlayerData::reset);
    }

    private void reLaunchGame(Game game, ServerToClientEnum.MessageEnum request) {
        this.game = game.init(this);

        if (request == ServerToClientEnum.MessageEnum.NEW_MATCH) {
            this.game.nextRound();
        } else if (request == ServerToClientEnum.MessageEnum.MATCH_LOAD_FROM_SAVE) {
            this.game.reloadRound();
        }

        getNoNullPlayers().forEach(game::addPlayer);
        getNoNullPlayers().forEach(p -> p.sendData(ServerToClientEnum.MESSAGE, request.name()));
    }

    public void saveGame(ServerPlayer p) {
        var saveState = new SaveState().setPlayerData(playersData).setGame(getGame());
        try {
            SaveManager.getInstance().saveGame(getSessionId(), saveState);
            p.sendData(ServerToClientEnum.SAVE_SUCCESS, getSessionId());
        } catch (SaveException ignore) {
            p.sendData(ServerToClientEnum.SAVE_FAILED);
        }
    }

    public void loadGame(ServerPlayer p, String saveName) {
        try {
            stopGame();
            var saveState = SaveManager.getInstance().loadSave(saveName);

            for (var i = 0; i < saveState.getPlayerData().length; i++) {
                var currentPlayerData = getPlayersData()[i];
                var loadPlayerData = saveState.getPlayerData()[i];
                currentPlayerData.restoreSaved(loadPlayerData);
            }

            reLaunchGame(saveState.getGame(), ServerToClientEnum.MessageEnum.MATCH_LOAD_FROM_SAVE);
            p.sendData(ServerToClientEnum.LOAD_SAVE_SUCCESS);
        } catch (SaveLoadException ignore) {
            p.sendData(ServerToClientEnum.LOAD_SAVE_FAILED);
        }
    }

    // Alphanumeric session id
    private String generateSessionId() {
        return random.ints(48, 123)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(SESSION_ID_LENGTH)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString().toUpperCase();
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

    public ServerPlayer getPOne() {
        return pOne;
    }

    public ServerPlayer getPTwo() {
        return pTwo;
    }

    public ServerPlayer getOppositePlayer(ServerPlayer playerAtm) {
        return playerAtm.equals(getPOne()) ? getPTwo() : getPOne();
    }

    public List<ServerPlayer> getNoNullPlayers() {
        return Stream.of(pOne, pTwo).filter(Objects::nonNull).collect(Collectors.toList());
    }

    public String getSessionId() {
        return sessionId;
    }
}