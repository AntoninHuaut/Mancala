package fr.antoninhuaut.mancala.socket.player.bot;

import fr.antoninhuaut.mancala.model.Cell;
import fr.antoninhuaut.mancala.socket.player.ClientBotPlayer;

public interface Strategy {

    int play(ClientBotPlayer clientBotPlayer, Cell[][] cells);
}
