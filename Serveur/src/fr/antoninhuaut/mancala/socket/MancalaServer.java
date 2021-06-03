package fr.antoninhuaut.mancala.socket;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MancalaServer {

    private static final Logger LOGGER = LogManager.getLogger();

    public MancalaServer(int port) throws IOException {
        LOGGER.info("MancalaServer starting on port {}", port);

        try (var listener = new ServerSocket(port)) {
            LOGGER.info("MancalaServer is running...");
            ExecutorService pool = Executors.newFixedThreadPool(200);

            while (true) {
                pool.execute(new SessionHandler(listener.accept()));
                LOGGER.debug("Player joined");
            }
        }
    }

    public static void main(String[] args) throws Exception {
        var port = 3010;

        if (args.length > 1) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException ex) {
                LOGGER.error("Invalid number");
            }
        }

        new MancalaServer(port);
    }
}