package fr.antoninhuaut.mancala.model;

import fr.antoninhuaut.mancala.match.Round;

import java.io.Serializable;

public class Cell implements Serializable {

    private int nbSeed;

    public Cell(int nbSeed) {
        this.nbSeed = nbSeed;
    }

    public Cell(Cell cell) {
       this.nbSeed = cell.getNbSeed();
    }

    public int getNbSeed() {
        return nbSeed;
    }

    public void clearSeed() {
        this.nbSeed = 0;
    }

    public void addSeed() {
        this.nbSeed++;
    }

    public static int getSeedInCellForPlayer(Cell[][] cells, int playerId) {
        var nbSeedCells = 0;

        for (var col = 0; col < Round.NB_COL; col++) {
            nbSeedCells += cells[playerId][col].getNbSeed();
        }

        return nbSeedCells;
    }
}
