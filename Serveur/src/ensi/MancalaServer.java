package ensi;

import ensi.game.Game;
import ensi.game.Player;

import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MancalaServer {

    public static void main(String[] args) throws Exception {
        int port = 3010;

        if (args.length > 1) {
            try {
                port = Integer.parseInt(args[0]);
            } catch(NumberFormatException ex) {
                System.err.println("Invalid number");
            }
        }

        System.out.printf("MancalaServer starting on port %d\n", port);

        try (ServerSocket listener = new ServerSocket(port)) {
            System.out.println("MancalaServer is running...");
            ExecutorService pool = Executors.newFixedThreadPool(200);

            while (true) {
                Game game = new Game();
                pool.execute(new Player(listener.accept(), game, true));
                System.out.println("Player one joined");
                pool.execute(new Player(listener.accept(), game, false));
                System.out.println("Player two joined");
            }
        }
    }
}