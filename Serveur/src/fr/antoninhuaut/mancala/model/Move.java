package fr.antoninhuaut.mancala.model;

import fr.antoninhuaut.mancala.match.Round;

import java.util.ArrayList;
import java.util.List;

public class Move {

    private final Round round;
    private final Cell[][] roundCells;
    private final Cell[][] initialCells;
    private Cell[][] finalCells;

    private final PlayerData currentPData;
    private int playerAddScore = 0;

    private boolean isDo = false;

    public Move(Round round, Cell[][] cells, PlayerData currentPData) {
        this.round = round;
        this.roundCells = cells;
        this.currentPData = currentPData;
        this.initialCells = deepCopy(cells);
        this.finalCells = deepCopy(cells);
    }

    private synchronized void playMove(int linePlayed, int colPlayed, boolean allowSeedCapture) {
        var cellPlayed = finalCells[linePlayed][colPlayed];
        int nbSeed = cellPlayed.getNbSeed();
        if (nbSeed <= 0) return;

        cellPlayed.clearSeed();

        int[] res = putSeedOnOtherCell(nbSeed, linePlayed, colPlayed);
        var line = res[0];
        var col = res[1];

        if (allowSeedCapture && line != linePlayed) { // Le joueur doit finir sur dans la partie adversaire pour pouvoir récupérer des graines
            captureSeed(line, col);
        }
    }

    private synchronized int[] putSeedOnOtherCell(int nbSeed, int linePlayed, int colPlayed) {
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
                    if (col < Round.NB_COL - 1) {
                        ++col;
                    } else {
                        line = 0;
                    }
                }
            } while (line == linePlayed && col == colPlayed); // REGLE 5 (Pas de graine sur la case qu'on a cliqué)

            finalCells[line][col].addSeed();
            --nbSeed;
        }

        return new int[] { line, col };
    }

    private synchronized void captureSeed(Integer line, Integer col) {
        var canCapture = true;

        while (col >= 0 && col < Round.NB_COL && canCapture) { // Tant qu'on peut capturer et qu'on est dans les colonnes adversaires
            var currentCell = finalCells[line][col];
            int nbSeed = currentCell.getNbSeed();

            if (nbSeed == 2 || nbSeed == 3) {
                playerAddScore += nbSeed;
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

    public synchronized MoveEnum doMove(int linePlayed, int colPlayed) {
        if (this.isDo) return MoveEnum.FAILED_MOVE_ALREADY_DONE;

        MoveEnum moveResult = MoveEnum.SUCCESS;

        int opponentSeedsInCells = getOpponentSeedInCells();
        /* REGLE 6 (Part 1) */
        if (opponentSeedsInCells == 0) {
            List<Integer> colPossible = getMoveToFeedOpponent();
            if (!colPossible.contains(colPlayed)) {
                return MoveEnum.FAILED_FEED_OPPONENT_POSSIBLE;
            }
        }
        /* */

        playMove(linePlayed, colPlayed, true);

        /* REGLE 7 */
        opponentSeedsInCells = getOpponentSeedInCells();
        if (opponentSeedsInCells == 0) {
            finalCells = deepCopy(roundCells);
            playMove(linePlayed, colPlayed, false);
            moveResult = MoveEnum.SUCCESS_FORBIDDEN_CAPTURE;
        }
        /* */

        opponentSeedsInCells = getOpponentSeedInCells();
        /* REGLE 6 (Part 2) */
        if (opponentSeedsInCells == 0 && getMoveToFeedOpponent().isEmpty()) {
            int currentPlayerSeedsInCells = Cell.getSeedInCellForPlayer(finalCells, currentPData.getPlayerId());
            for (var col = 0; col < Round.NB_COL; col++) {
                finalCells[currentPData.getPlayerId()][col].clearSeed();
            }
            playerAddScore += currentPlayerSeedsInCells;
            moveResult = MoveEnum.SUCCESS_AND_WIN_OPPONENT_CANT_PLAY;
        }
        /* */

        for (var line = 0; line < Round.NB_LINE; ++line) {
            System.arraycopy(finalCells[line], 0, roundCells[line], 0, Round.NB_COL);
        }
        currentPData.addScore(playerAddScore);

        this.isDo = true;
        return moveResult;
    }

    public synchronized void undoMove() {
        if (!this.isDo) return;

        for (var line = 0; line < Round.NB_LINE; ++line) {
            System.arraycopy(initialCells[line], 0, roundCells[line], 0, Round.NB_COL);
        }

        currentPData.removeScore(playerAddScore);

        this.isDo = false;
    }

    private int getOpponentSeedInCells() {
        return Cell.getSeedInCellForPlayer(finalCells, (currentPData.getPlayerId() + 1) % 2);
    }

    private List<Integer> getMoveToFeedOpponent() {
        Cell[][] originalFinalCells = deepCopy(finalCells);
        List<Integer> possibleMove = new ArrayList<>();

        for (var col = 0; col < Round.NB_COL; col++) {
            playMove(currentPData.getPlayerId(), col, true);
            if (getOpponentSeedInCells() > 0) {
                possibleMove.add(col);
            }
            finalCells = deepCopy(originalFinalCells);
        }

        return possibleMove;
    }

    private Cell[][] deepCopy(Cell[][] cells) {
        var copyCells = new Cell[Round.NB_LINE][Round.NB_COL];

        for (var line = 0; line < Round.NB_LINE; line++) {
            for (var col = 0; col < Round.NB_COL; col++) {
                copyCells[line][col] = new Cell(cells[line][col]);
            }
        }

        return copyCells;
    }
}
