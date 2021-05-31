package ensi.game;

import java.io.Serializable;

public class Cell implements Serializable {

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
}
