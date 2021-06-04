package fr.antoninhuaut.mancala.model;

import java.util.Random;

public class PlayerData {

    private final int playerId;

    private String username = "Player" + (new Random().nextInt(899) + 100);

    private int nbRoundWin = 0;
    private int currentScore = 0;

    public PlayerData(int playerId) {
        this.playerId = playerId;
    }

    public int getPlayerId() {
        return playerId;
    }

    public boolean hasWin() {
        return currentScore >= 25;
    }

    public void addScore(int addScore) {
        this.currentScore += addScore;
    }

    public void removeScore(int addScore) {
        this.currentScore -= addScore;
    }

    public void resetScore() {
        this.currentScore = 0;
    }

    public int getCurrentScore() {
        return currentScore;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public int getNbRoundWin() {
        return nbRoundWin;
    }

    public void addWinRound() {
        nbRoundWin++;
    }
}
