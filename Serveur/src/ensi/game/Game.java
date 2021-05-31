package ensi.game;

import java.util.Optional;
import java.util.Random;

public class Game {

    private static final int NB_LINE = 2;
    private static final int NB_COL = 6;

    private final Cell[][] cells;

    private Player pOne, pTwo;
    private Player currentPlayer;

    public Game() {
        this.cells = new Cell[NB_LINE][NB_COL];

        for (int line = 0; line < NB_LINE; ++line) {
            for (int col = 0; col < NB_COL; ++col) {
                cells[line][col] = new Cell(null, 4);
            }
        }
    }

    public synchronized void play(Player player, int linePlayed, int colPlayed) {
        Cell cellPlayed = cells[linePlayed][colPlayed];

        if (currentPlayer != player) {
            throw new IllegalStateException("NOT_YOUR_TURN");
        } else if (cellPlayed.getOwnPlayerId() != player.getPlayerId()) {
            throw new IllegalStateException("NOT_YOUR_CELL");
        }

        playMove(cellPlayed, linePlayed, colPlayed);
        getOppositePlayer(currentPlayer).sendGameUpdate(cells);

        Optional<Player> optWinner = getWinner();
        if (optWinner.isPresent()) {
            Player winner = optWinner.get();
            winner.sendData("END YOU_WIN");
            getOppositePlayer(winner).sendData("END YOU_LOSE");
        }

        this.currentPlayer = getOppositePlayer(currentPlayer);
    }

    private synchronized void playMove(Cell cellPlayed, int linePlayed, int colPlayed) {
        int nbSeed = cellPlayed.getNbSeed();
        cellPlayed.clearSeed();

        int line = linePlayed;
        int col = colPlayed;
        while (nbSeed > 0) {
            do {
                if (line == 0) {
                    if (col > 0) {
                        --col;
                    } else {
                        line = 1;
                    }
                } else {
                    if (col < NB_COL - 1) {
                        ++col;
                    } else {
                        line = 0;
                    }
                }
            } while (line == linePlayed && col == colPlayed);

            cells[line][col].addSeed();
            --nbSeed;
        }

        if (line != linePlayed) { // Le joueur doit finir sur dans la partie adversaire pour pouvoir récupérer des graines
            boolean canCapture = true;

            while (col >= 0 && col < NB_COL && canCapture) { // Tant qu'on peut capturer et qu'on est dans les colonnes adversaires
                Cell currentCell = cells[line][col];
                nbSeed = currentCell.getNbSeed();
                if (nbSeed == 2 || nbSeed == 3) {
                    currentPlayer.addScore(nbSeed);
                    currentCell.clearSeed();

                    // On ne peut pas changer de ligne sinon cela voudrait dire qu'on va de notre propre côté
                    if (line == 0) {
                        ++col;
                    } else {
                        --col;
                    }
                } else {
                    canCapture = false; // On arrête la vérification de capture
                }
            }
        }
    }

    public void setPlayerOne(Player pOne) {
        this.pOne = pOne;
    }

    public void setPlayerTwo(Player pTwo) {
        this.pTwo = pTwo;
    }

    private Optional<Player> getWinner() {
        if (pOne.hasWin()) return Optional.of(pOne);
        else if (pTwo.hasWin()) return Optional.of(pTwo);
        return Optional.empty();
    }

    public void initPostPlayersJoined() {
        // SET CURRENT PLAYER
        this.currentPlayer = new Random().nextBoolean() ? pOne : pTwo;
        this.currentPlayer.sendData("INIT_PLAYER YOU");
        getOppositePlayer(currentPlayer).sendData("INIT_PLAYER OPPONENT");

        // SET CELLS OWNER
        for (int line = 0; line < NB_LINE; ++line) {
            for (int col = 0; col < NB_COL; ++col) {
                cells[line][col].setOwnPlayerId(line == 0 ? pOne.getPlayerId() : pTwo.getPlayerId());
            }
        }
    }

    public Player getOppositePlayer(Player playerAtm) {
        return playerAtm == pOne ? pTwo : pOne;
    }
}