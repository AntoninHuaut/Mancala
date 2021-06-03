package fr.antoninhuaut.mancala.match;

import fr.antoninhuaut.mancala.model.Cell;
import fr.antoninhuaut.mancala.model.Move;
import fr.antoninhuaut.mancala.model.Player;
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

    private Player currentPlayer;
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
        this.currentPlayer = new Random().nextBoolean() ? game.getPOne() : game.getPTwo();
        this.currentPlayer.sendData("INIT_PLAYER YOU");
        game.getOppositePlayer(currentPlayer).sendData("INIT_PLAYER OPPONENT");

        sendGameUpdate(this.currentPlayer);
    }

    public synchronized void play(Player player, int linePlayed, int colPlayed) {
        if (currentPlayer != player) {
            throw new IllegalStateException("NOT_YOUR_TURN");
        } else if (linePlayed != player.getPlayerId()) {
            throw new IllegalStateException("NOT_YOUR_CELL");
        }

        lastMove = new Move(this, cells, currentPlayer);
        var moveEnum = lastMove.doMove(linePlayed, colPlayed);
        LOGGER.debug("Move: {}", moveEnum);
        if (!moveEnum.isSuccess()) {
            // TODO message client
            return;
        }

        var nextPlayerTurn = game.getOppositePlayer(currentPlayer);
        sendGameUpdate(nextPlayerTurn);

        Optional<Player> optWinner = getWinner();
        if (optWinner.isPresent()) {
            Player winner = optWinner.get();
            winner.sendData("END YOU_WIN");
            game.getOppositePlayer(winner).sendData("END YOU_LOSE");
            // TODO gestion 6 rounds/fin du jeu
        }

        this.currentPlayer = nextPlayerTurn;
    }

    public void undo() {
        lastMove.undoMove();

        var nextPlayerTurn = game.getOppositePlayer(currentPlayer);
        sendGameUpdate(nextPlayerTurn);
        this.currentPlayer = nextPlayerTurn;
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

    public Game getGame() {
        return game;
    }
}