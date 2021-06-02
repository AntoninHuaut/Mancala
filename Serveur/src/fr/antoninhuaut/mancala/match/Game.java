package fr.antoninhuaut.mancala.match;

import fr.antoninhuaut.mancala.model.Player;

public class Game {

    private Player pOne;
    private Player pTwo;

    private Round currentRound;
    private int nbRound;

    public Game() {
        this.nbRound = 0;
        startGame();
    }

    public void startGame() {
        for (var i = 0; i < 6; i++) {
            this.currentRound = new Round(this);
        }
    }

    public void initPostPlayersJoined() {
        currentRound.initPostPlayersJoined();
    }

    public void sendPlayersName() {
        pOne.sendData("OPPONENT_NAME " + pTwo.getUsername());
        pTwo.sendData("OPPONENT_NAME " + pOne.getUsername());
    }

    public void setPlayerOne(Player pOne) {
        this.pOne = pOne;
    }

    public void setPlayerTwo(Player pTwo) {
        this.pTwo = pTwo;
    }

    public Player getPOne() {
        return pOne;
    }

    public Player getPTwo() {
        return pTwo;
    }

    public Player getOppositePlayer(Player playerAtm) {
        return playerAtm == getPOne() ? getPTwo() : getPOne();
    }

    public Round getCurrentRound() {
        return currentRound;
    }
}
