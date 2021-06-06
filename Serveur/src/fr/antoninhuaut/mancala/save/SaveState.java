package fr.antoninhuaut.mancala.save;

import fr.antoninhuaut.mancala.match.Game;
import fr.antoninhuaut.mancala.model.PlayerData;

public class SaveState {

    private PlayerData[] playerData;
    private Game game;

    public PlayerData[] getPlayerData() {
        return playerData;
    }

    public SaveState setPlayerData(PlayerData[] playerData) {
        this.playerData = playerData;
        return this;
    }

    public Game getGame() {
        return game;
    }

    public SaveState setGame(Game game) {
        this.game = game;
        return this;
    }
}
