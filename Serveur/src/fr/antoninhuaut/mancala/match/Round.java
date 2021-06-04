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
        // SET CURRENT PLAYER
        if (playerTurnId == null) {
            playerTurnId = new Random().nextInt(2); // Between 0 & 1
        }
        var currentPlayer = getCurrentPlayer();
        currentPlayer.sendData(ServerToClientEnum.INIT_PLAYER, "YOU");
        game.getOppositePlayer(currentPlayer).sendData(ServerToClientEnum.INIT_PLAYER, "OPPONENT");

        sendGameUpdate(currentPlayer);
    }

    public synchronized void play(Player player, int linePlayed, int colPlayed) {
        if (playerTurnId != player.getPlayerId()) {
            throw new IllegalStateException(ServerToClientEnum.BadStateEnum.NOT_YOUR_TURN.name());
        } else if (playerTurnId == null || linePlayed != player.getPlayerId()) {
            throw new IllegalStateException(ServerToClientEnum.BadStateEnum.NOT_YOUR_CELL.name());
        }

        var currentPlayer = getCurrentPlayer();
        var pData = game.getPlayersData()[currentPlayer.getPlayerId()];

        this.lastMove = new Move(this, cells, pData);

        var moveEnum = this.lastMove.doMove(linePlayed, colPlayed);
        LOGGER.debug("Move: {} - isSuccess: {}", moveEnum, moveEnum.isSuccess());
        if (!moveEnum.isSuccess()) {
            throw new IllegalStateException(moveEnum.name());
        }

        var nextPlayerTurn = game.getOppositePlayer(currentPlayer);
        sendGameUpdate(nextPlayerTurn);

        Optional<Player> optWinner = getWinner(moveEnum);
        if (optWinner.isPresent()) {
            Player winner = optWinner.get();

            for (Player p : Arrays.asList(winner, game.getOppositePlayer(winner))) {
                p.sendData(ServerToClientEnum.END_ROUND, "" + winner.getPlayerId());
            }

            game.getPlayersData()[winner.getPlayerId()].addWinRound();
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

    public void undo() {
        lastMove.undoMove();

        var currentPlayer = getCurrentPlayer();
        var nextPlayerTurn = game.getOppositePlayer(currentPlayer);
        sendGameUpdate(nextPlayerTurn);
        this.playerTurnId = nextPlayerTurn.getPlayerId();
    }

    private void sendGameUpdate(Player nextPlayerTurn) {
        for (Player pLoop : Arrays.asList(nextPlayerTurn, game.getOppositePlayer(nextPlayerTurn))) {
            pLoop.sendGameUpdate(cells, nextPlayerTurn.getPlayerId(), game.getPlayersData());
        }
    }

    private Optional<Player> getWinner(MoveEnum moveEnum) {
        var playerData = game.getPlayersData();

        // Gagnant si l'opposant ne peut plus jouer
        if (moveEnum == MoveEnum.SUCCESS_AND_WIN_OPPONENT_CANT_PLAY) {
            return Optional.of(getCurrentPlayer());
        }

        // Gagnant par points
        if (playerData[0].hasWinRound()) return Optional.of(game.getPOne());
        else if (playerData[1].hasWinRound()) return Optional.of(game.getPTwo());
        return Optional.empty();
    }

    private Player getCurrentPlayer() {
        return getPlayerById(playerTurnId);
    }

    private Player getPlayerById(int id) {
        if (id == 0) return game.getPOne();
        else return game.getPTwo();
    }
}