package fr.antoninhuaut.mancala;

import fr.antoninhuaut.mancala.match.Game;
import fr.antoninhuaut.mancala.model.Player;

import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MancalaServer {

    private static final Logger LOGGER = LogManager.getLogger(MancalaServer.class);

    public static void main(String[] args) throws Exception {
        var port = 3010;

        if (args.length > 1) {
            try {
                port = Integer.parseInt(args[0]);
            } catch(NumberFormatException ex) {
                LOGGER.error("Invalid number");

            }
        }

        LOGGER.info("MancalaServer starting on port {} %n", port);

        try (var listener = new ServerSocket(port)) {
            LOGGER.info("MancalaServer is running...");
            ExecutorService pool = Executors.newFixedThreadPool(200);

            while (true) {
                var game = new Game();
                pool.execute(new Player(listener.accept(), game, true));
                LOGGER.debug("Player one joined");
                pool.execute(new Player(listener.accept(), game, false));
                LOGGER.debug("Player two joined");
            }
        }
    }
}