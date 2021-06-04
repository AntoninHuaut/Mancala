package fr.antoninhuaut.mancala.match;

import fr.antoninhuaut.mancala.model.Cell;
import fr.antoninhuaut.mancala.model.Move;
import fr.antoninhuaut.mancala.model.MoveEnum;
import fr.antoninhuaut.mancala.socket.Player;
import fr.antoninhuaut.mancala.socket.cenum.ServerToClientEnum;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

public class Round {

    private static final Logger LOGGER = LogManager.getLogger();

    public static final int NB_LINE = 2;
    public static final int NB_COL = 6;

    private final Game game;
    private final Cell[][] cells;

    private Integer playerTurnId = null;
    private Move lastMove;

    public Round(Game game) {
        this.game = game;
        this.cells = new Cell[NB_LINE][NB_COL];
        for (var line = 0; line < NB_LINE; ++line) {
            for (var col = 0; col < NB_COL; ++col) {
                cells[line][col] = new Cell(4);
            }
        }
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
            throw new IllegalStateException(ServerToClientEnum.BadStateEnum.NOT_YOUR_TURN.name());
        } else if (playerTurnId == null || linePlayed != player.getPlayerId()) {
            throw new IllegalStateException(ServerToClientEnum.BadStateEnum.NOT_YOUR_CELL.name());
        }

        var pData = game.getSession().getPlayersData()[currentPlayer.getPlayerId()];

        this.lastMove = new Move(this, cells, pData);

        var moveEnum = this.lastMove.doMove(linePlayed, colPlayed);
        LOGGER.debug("Move: {} - isSuccess: {}", moveEnum, moveEnum.isSuccess());
        if (!moveEnum.isSuccess()) {
            throw new IllegalStateException(moveEnum.name());
        }

        var nextPlayerTurn = game.getSession().getOppositePlayer(currentPlayer);
        sendGameUpdate(nextPlayerTurn);

        Optional<Integer> optWinner = getWinnerId(moveEnum);
        if (optWinner.isPresent()) {
            Integer winnerId = optWinner.get();

            for (Player p : game.getSession().getNoNullPlayers()) {
                p.sendData(ServerToClientEnum.END_ROUND, "" + winnerId);
            }

            game.getSession().getPlayersData()[winnerId].addWinRound();
            this.playerTurnId = -1; // Empêche toute futur action sur ce round (qui est terminé)

            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    game.nextRound();
                }
            }, 5000);
        } else {
            this.playerTurnId = nextPlayerTurn.getPlayerId();
        }
    }

    public void undo(int playerId) {
        if (game.isGameFinished()) return;

        if (lastMove == null || playerTurnId == -1 || playerId == playerTurnId) {
            throw new IllegalStateException(ServerToClientEnum.BadStateEnum.CANT_UNDO_NOW.name());
        }

        lastMove.undoMove();

        var currentPlayer = getCurrentPlayer();
        var nextPlayerTurn = game.getSession().getOppositePlayer(currentPlayer);
        sendGameUpdate(nextPlayerTurn);
        this.playerTurnId = nextPlayerTurn.getPlayerId();
        this.lastMove = null; // Prevent double undo
    }

    private void sendGameUpdate(Player nextPlayerTurn) {
        if (nextPlayerTurn == null) return;

        for (Player pLoop : game.getSession().getNoNullPlayers()) {
            pLoop.sendGameUpdate(cells, nextPlayerTurn.getPlayerId(), game.getSession().getPlayersData());
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
}