package fr.antoninhuaut.mancala.model;

import fr.antoninhuaut.mancala.match.Round;

public class Cell implements Cloneable {

    private Integer ownPlayerId;
    private int nbSeed;

    public Cell(Integer ownPlayerId, int nbSeed) {
        this.ownPlayerId = ownPlayerId;
        this.nbSeed = nbSeed;
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

    @Override
    protected Cell clone() throws CloneNotSupportedException {
        super.clone();
        return new Cell(ownPlayerId, nbSeed);
    }

    public static int getSeedInCellForPlayer(Cell[][] cells, Player player) {
        int nbSeedCells = 0;

        for (int col = 0; col < Round.NB_COL; col++) {
            nbSeedCells += cells[player.getPlayerId()][col].getNbSeed();
        }

        return nbSeedCells;
    }
}
