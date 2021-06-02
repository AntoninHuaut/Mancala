package fr.antoninhuaut.mancala.model;

import fr.antoninhuaut.mancala.match.Round;

public class Move {

    private final Round round;
    private final Cell[][] roundCells;
    private final Cell[][] initialCells;
    private Cell[][] finalCells;

    private final Player currentPlayer;
    private int playerAddScore = 0;

    private boolean isDo = false;

    public Move(Round round, Cell[][] cells, Player currentPlayer) {
        this.round = round;
        this.roundCells = cells;
        this.currentPlayer = currentPlayer;
        this.initialCells = deepCopy(cells);
        this.finalCells = deepCopy(cells);
    }

    private synchronized void playMove(int linePlayed, int colPlayed, boolean allowSeedCapture) {
        Cell cellPlayed = finalCells[linePlayed][colPlayed];
        int nbSeed = cellPlayed.getNbSeed();
        if (nbSeed <= 0) return;

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

        if (line != linePlayed) { // Le joueur doit finir sur dans la partie adversaire pour pouvoir récupérer des graines
            boolean canCapture = true;

            while (col >= 0 && col < Round.NB_COL && canCapture) { // Tant qu'on peut capturer et qu'on est dans les colonnes adversaires
                Cell currentCell = finalCells[line][col];
                nbSeed = currentCell.getNbSeed();

                if (allowSeedCapture && (nbSeed == 2 || nbSeed == 3)) {
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
    }

    public synchronized MoveEnum doMove(int linePlayed, int colPlayed) {
        if (this.isDo) return MoveEnum.FAILED_MOVE_ALREADY_DONE;

        MoveEnum moveResult = MoveEnum.SUCCESS;

        int opponentSeedsInCells = getOpponentSeedInCells();
        /* REGLE 6 (Part 1) */
        if (opponentSeedsInCells == 0) {
            int colPossible = getMoveToFeedOpponent();
            if (colPossible == -1) { // TODO A faire après un move
                // Fin du jeu
            } else if (colPossible != colPlayed) {
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
        if (opponentSeedsInCells == 0 && getMoveToFeedOpponent() == -1) {
            int currentPlayerSeedsInCells = Cell.getSeedInCellForPlayer(finalCells, currentPlayer);
            for (int col = 0; col < Round.NB_COL; col++) {
                finalCells[currentPlayer.getPlayerId()][col].clearSeed();
            }
            playerAddScore += currentPlayerSeedsInCells;
            moveResult = MoveEnum.SUCCESS_AND_WIN__OPPONENT_CANT_PLAY;
        }
        /* */

        for (int line = 0; line < Round.NB_LINE; ++line) {
            System.arraycopy(finalCells[line], 0, roundCells[line], 0, Round.NB_COL);
        }
        currentPlayer.addScore(playerAddScore);

        this.isDo = true;
        return moveResult;
    }

    public synchronized void undoMove() {
        if (!this.isDo) return;

        for (int line = 0; line < Round.NB_LINE; ++line) {
            System.arraycopy(initialCells[line], 0, roundCells[line], 0, Round.NB_COL);
        }

        currentPlayer.addScore(-playerAddScore);

        this.isDo = false;
    }

    private int getOpponentSeedInCells() {
        return Cell.getSeedInCellForPlayer(finalCells, round.getGame().getOppositePlayer(currentPlayer));
    }

    private int getMoveToFeedOpponent() {
        Cell[][] originalFinalCells = deepCopy(finalCells);

        for (int col = 0; col < Round.NB_COL; col++) {
            playMove(currentPlayer.getPlayerId(), col, true);
            if (getOpponentSeedInCells() > 0) {
                return col;
            }
            finalCells = deepCopy(originalFinalCells);
        }

        return -1;
    }

    private Cell[][] deepCopy(Cell[][] cells) {
        Cell[][] copyCells = new Cell[Round.NB_LINE][Round.NB_COL];

        try {
            for (int line = 0; line < Round.NB_LINE; line++) {
                for (int col = 0; col < Round.NB_COL; col++) {
                    copyCells[line][col] = cells[line][col].clone();
                }
            }
        } catch (CloneNotSupportedException ignored) {
        }

        return copyCells;
    }
}
