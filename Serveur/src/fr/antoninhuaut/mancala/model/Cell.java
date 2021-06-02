package fr.antoninhuaut.mancala.model;

import fr.antoninhuaut.mancala.match.Round;

public class Cell {

    private Integer ownPlayerId;
    private int nbSeed;

    public Cell(Integer ownPlayerId, int nbSeed) {
        this.ownPlayerId = ownPlayerId;
        this.nbSeed = nbSeed;
    }

    public Cell(Cell cell) {
       this.ownPlayerId = cell.getOwnPlayerId();
       this.nbSeed = cell.getNbSeed();
    }

    public Integer getOwnPlayerId() {
        return ownPlayerId;
    }

    public void setOwnPlayerId(Integer ownPlayerId) {
        this.ownPlayerId = ownPlayerId;
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

    public static int getSeedInCellForPlayer(Cell[][] cells, Player player) {
        var nbSeedCells = 0;

        for (var col = 0; col < Round.NB_COL; col++) {
            nbSeedCells += cells[player.getPlayerId()][col].getNbSeed();
        }

        return nbSeedCells;
    }
}
