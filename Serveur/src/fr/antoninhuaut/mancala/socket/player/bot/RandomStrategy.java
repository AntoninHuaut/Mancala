package fr.antoninhuaut.mancala.socket.player.bot;

import fr.antoninhuaut.mancala.model.Cell;
import fr.antoninhuaut.mancala.socket.player.ClientBotPlayer;

import java.util.Random;

public class RandomStrategy implements Strategy {

    private final Random random = new Random();

    @Override
    public int play(ClientBotPlayer clientBotPlayer, Cell[][] cells) {
        return random.nextInt(6);
    }
}