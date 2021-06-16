package fr.antoninhuaut.mancala.socket.player.bot;

import fr.antoninhuaut.mancala.model.Cell;
import fr.antoninhuaut.mancala.socket.player.ClientBotPlayer;

public class CellMostSeedStrategy implements Strategy {

    @Override
    public int play(ClientBotPlayer clientBotPlayer, Cell[][] cells) {
        var line = clientBotPlayer.getMyPlayerId();
        var bestCol = -1;
        var bestNbSeed = -1;

        for (var col = 0; col < 6; col++) {
            var cell = cells[line][col];
            if (cell.getNbSeed() >= bestNbSeed) {
                bestNbSeed = cell.getNbSeed();
                bestCol = col;
            }
        }

        return bestCol;
    }
}
