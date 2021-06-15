package fr.antoninhuaut.mancala.match;

import fr.antoninhuaut.mancala.model.Cell;
import fr.antoninhuaut.mancala.model.MoveEnum;
import fr.antoninhuaut.mancala.save.HighscoreManager;
import fr.antoninhuaut.mancala.socket.Player;
import fr.antoninhuaut.mancala.socket.cenum.ServerToClientEnum;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Serializable;
import java.util.*;

public class Round implements Serializable {

    private static final Logger LOGGER = LogManager.getLogger();

    public static final int NB_LINE = 2;
    public static final int NB_COL = 6;

    private transient Game game;
    private Cell[][] cells;

    private boolean[] surrenderVote;

    private Integer playerTurnId = null;
    private Move lastMove;

    public Round() {
        // For save loading
    }

    public Round create() {
        this.cells = new Cell[NB_LINE][NB_COL];
        this.surrenderVote = new boolean[2];
        for (var line = 0; line < NB_LINE; ++line) {
            for (var col = 0; col < NB_COL; ++col) {
                cells[line][col] = new Cell(4);
            }
        }
        return this;
    }

    public Round init(Game game) {
        this.game = game;

        if (lastMove != null) {
            lastMove.init(cells, this);
        }
        return this;
    }

    public void initPostPlayersJoined() {
        if (playerTurnId == null) {
            playerTurnId = new Random().nextInt(2);
        }

        var currentPlayer = getCurrentPlayer();
        if (currentPlayer == null) return;

        var oppositePlayer = game.getSession().getOppositePlayer(currentPlayer);
        if (oppositePlayer == null) return;

        currentPlayer.sendData(ServerToClientEnum.INIT_PLAYER, "YOU");
        oppositePlayer.sendData(ServerToClientEnum.INIT_PLAYER, "OPPONENT");

        sendGameUpdate(currentPlayer);
    }

    public synchronized void play(Player player, int linePlayed, int colPlayed) {
        if (game.isGameFinished()) return;

        var currentPlayer = getCurrentPlayer();

        if (playerTurnId != player.getPlayerId() || currentPlayer == null) {
            throw new IllegalStateException(ServerToClientEnum.MessageEnum.NOT_YOUR_TURN.name());
        } else if (playerTurnId == null || linePlayed != player.getPlayerId()) {
            throw new IllegalStateException(ServerToClientEnum.MessageEnum.NOT_YOUR_CELL.name());
        }

        var pData = game.getSession().getPlayersData()[currentPlayer.getPlayerId()];

        this.lastMove = new Move(cells, pData);
        this.lastMove.init(cells, this);

        var moveEnum = this.lastMove.doMove(linePlayed, colPlayed);
        LOGGER.debug("Move: {} - isSuccess: {}", moveEnum, moveEnum.isSuccess());
        if (!moveEnum.isSuccess()) {
            throw new IllegalStateException(moveEnum.name());
        }

        var nextPlayerTurn = game.getSession().getOppositePlayer(currentPlayer);
        sendGameUpdate(nextPlayerTurn);

        Optional<Integer> optWinner = getWinnerId(moveEnum);
        if (optWinner.isPresent()) {
            processVictory(optWinner.get());
        } else {
            this.playerTurnId = nextPlayerTurn.getPlayerId();
        }
    }

    private void processVictory(Integer winnerId) {
        for (var p : game.getSession().getNoNullPlayers()) {
            p.sendData(ServerToClientEnum.END_ROUND, "" + winnerId);
        }

        if (winnerId != null && winnerId != -1) {
            var playerData = game.getSession().getPlayersData()[winnerId];
            playerData.addWinRound();
            HighscoreManager.getInstance().addHighscore(playerData.getUsername(), playerData.getCurrentScore());
        }
        this.playerTurnId = -1; // Empêche toute futur action sur ce round (qui est terminé)

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                game.nextRound();
            }
        }, 5000);
    }

    public void undo(int playerId) {
        if (game.isGameFinished()) return;

        if (lastMove == null || playerTurnId == -1 || playerId == playerTurnId) {
            throw new IllegalStateException(ServerToClientEnum.MessageEnum.CANT_UNDO_NOW.name());
        }

        lastMove.undoMove();

        var currentPlayer = getCurrentPlayer();
        var nextPlayerTurn = game.getSession().getOppositePlayer(currentPlayer);
        sendGameUpdate(nextPlayerTurn);
        this.playerTurnId = nextPlayerTurn.getPlayerId();
        this.lastMove = null; // Prevent double undo
    }

    private void sendGameUpdate(Player nextPlayerTurn) {
        var pId = nextPlayerTurn == null ? -1 : nextPlayerTurn.getPlayerId();

        for (Player pLoop : game.getSession().getNoNullPlayers()) {
            pLoop.sendGameUpdate(cells, pId, game.getSession().getPlayersData());
        }
    }

    private void resetSurrenderVote() {
        for (var i = 0; i < 2; i++) {
            surrenderVote[i] = false;
        }
    }

    private int getCellRemaining() {
        return Cell.getSeedInCellForPlayer(cells, 0) + Cell.getSeedInCellForPlayer(cells, 1);
    }

    private boolean isPossibleToSurrender() {
        return getCellRemaining() <= 10;
    }

    public void acceptSurrender(Player pAcceptSurrender) {
        if (getGame().getSession().getNbPlayer() != 2 || !isPossibleToSurrender() || playerTurnId == -1) return;

        int pSurrenderId = pAcceptSurrender.getPlayerId();
        surrenderVote[pSurrenderId] = true;

        if (surrenderVote[0] && surrenderVote[1]) {
            int nbSeedRemaining = getCellRemaining();

            game.getSession().getPlayersData()[pSurrenderId].addScore(nbSeedRemaining / 2);
            game.getSession().getPlayersData()[(pSurrenderId + 1) % 2].addScore(nbSeedRemaining / 2);
            Arrays.stream(cells).forEach(css -> Arrays.stream(css).forEach(Cell::clearSeed));

            sendGameUpdate(null);
            processVictory(getWinnerId(null).orElse(-1));

            game.getSession().getNoNullPlayers().forEach(pLoop ->
                    pLoop.sendData(ServerToClientEnum.MESSAGE, ServerToClientEnum.MessageEnum.SUCCESS_SURRENDER.name())
            );
        }

        resetSurrenderVote();
    }

    public void denySurrender() {
        if (getGame().getSession().getNbPlayer() != 2 || !isPossibleToSurrender() || playerTurnId == -1) return;
        resetSurrenderVote();

        game.getSession().getNoNullPlayers().forEach(pLoop ->
                pLoop.sendData(ServerToClientEnum.MESSAGE, ServerToClientEnum.MessageEnum.FAIL_SURRENDER.name())
        );
    }

    public void soloSurrender(Player pSurrender) {
        if (getGame().getSession().getNbPlayer() != 2 || playerTurnId == -1) return;

        var nbSeedRemaining = getCellRemaining();
        var pSurrenderId = pSurrender.getPlayerId();

        game.getSession().getPlayersData()[(pSurrenderId + 1) % 2].addScore(nbSeedRemaining);
        Arrays.stream(cells).forEach(css -> Arrays.stream(css).forEach(Cell::clearSeed));

        sendGameUpdate(null);
        processVictory(getWinnerId(null).orElse(-1));

        game.getSession().getNoNullPlayers().forEach(pLoop ->
                pLoop.sendData(ServerToClientEnum.MESSAGE, ServerToClientEnum.MessageEnum.SUCCESS_SOLO_SURRENDER.name())
        );
    }

    public void handleAskSurrenderVote(Player pAskSurrender) {
        if (getGame().getSession().getNbPlayer() != 2 || !isPossibleToSurrender()) return;
        if (playerTurnId != pAskSurrender.getPlayerId()) {
            throw new IllegalStateException(ServerToClientEnum.MessageEnum.NOT_YOUR_TURN.name());
        }

        resetSurrenderVote();
        surrenderVote[pAskSurrender.getPlayerId()] = true;

        for (Player pLoop : game.getSession().getNoNullPlayers()) {
            if (!pLoop.equals(pAskSurrender)) {
                pLoop.sendData(ServerToClientEnum.ASK_TO_SURRENDER);
            }
        }
    }

    private Optional<Integer> getWinnerId(MoveEnum moveEnum) {
        var playerData = game.getSession().getPlayersData();

        // Gagnant si l'opposant ne peut plus jouer
        if (moveEnum == MoveEnum.SUCCESS_AND_WIN_OPPONENT_CANT_PLAY) {
            var currentPlayer = getCurrentPlayer();
            return currentPlayer != null ? Optional.of(currentPlayer.getPlayerId()) : Optional.of(-1);
        }

        // Gagnant par points
        if (playerData[0].hasWinRound()) return Optional.of(0);
        else if (playerData[1].hasWinRound()) return Optional.of(1);
        return Optional.empty();
    }

    private Player getCurrentPlayer() {
        return getPlayerById(playerTurnId);
    }

    private Player getPlayerById(int id) {
        if (id == 0) return game.getSession().getPOne();
        else if (id == 1) return game.getSession().getPTwo();
        else return null;
    }

    public Game getGame() {
        return game;
    }

}