package fr.antoninhuaut.mancala.match;

import fr.antoninhuaut.mancala.model.Cell;
import fr.antoninhuaut.mancala.model.Move;
import fr.antoninhuaut.mancala.model.Player;
import fr.antoninhuaut.mancala.socket.cenum.ServerToClientEnum;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.Optional;
import java.util.Random;

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
        var currentPlayer = getCurrentPlayer();
        if (currentPlayer != player) {
            throw new IllegalStateException("NOT_YOUR_TURN"); // TODO CLIENT
        } else if (playerTurnId == null || linePlayed != player.getPlayerId()) {
            throw new IllegalStateException("NOT_YOUR_CELL"); // TODO CLIENT
        }

        lastMove = new Move(this, cells, currentPlayer);
        var moveEnum = lastMove.doMove(linePlayed, colPlayed);
        LOGGER.debug("Move: {}", moveEnum);
        if (!moveEnum.isSuccess()) {
            // TODO CLIENT
            return;
        }

        var nextPlayerTurn = game.getOppositePlayer(currentPlayer);
        sendGameUpdate(nextPlayerTurn);

        Optional<Player> optWinner = getWinner();
        if (optWinner.isPresent()) {
            Player winner = optWinner.get();

            for (Player p : Arrays.asList(winner, game.getOppositePlayer(winner))) {
                p.sendData(ServerToClientEnum.END_ROUND, "" + winner.getPlayerId()); // TODO CLIENT
            }
            // TODO gestion 6 rounds/fin du jeu
        }

        this.playerTurnId = nextPlayerTurn.getPlayerId();
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
            pLoop.sendGameUpdate(cells, nextPlayerTurn.getPlayerId(), game.getPOne().getCurrentScore(), game.getPTwo().getCurrentScore());
        }
    }

    private Optional<Player> getWinner() {
        if (game.getPOne().hasWin()) return Optional.of(game.getPOne());
        else if (game.getPTwo().hasWin()) return Optional.of(game.getPTwo());
        return Optional.empty();
    }

    private Player getCurrentPlayer() {
        return getPlayerById(playerTurnId);
    }

    private Player getPlayerById(int id) {
        if (id == 0) return game.getPOne();
        else return game.getPTwo();
    }

    public Game getGame() {
        return game;
    }
}