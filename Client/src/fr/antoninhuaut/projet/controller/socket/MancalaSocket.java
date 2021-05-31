package fr.antoninhuaut.projet.controller.socket;

import fr.antoninhuaut.projet.controller.game.GameController;
import fr.antoninhuaut.projet.model.views.socket.SocketConnectionData;
import fr.antoninhuaut.projet.view.global.HomeView;
import fr.antoninhuaut.projet.view.socket.SocketConnectionView;
import javafx.application.Platform;

import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class MancalaSocket {

    private final Socket socket;
    private final Scanner in;
    private final PrintWriter out;

    private final HomeView homeView;
    private GameController gameController;

    public MancalaSocket(SocketConnectionData socketConnectionData, HomeView homeView) throws Exception {
        this.socket = new Socket(socketConnectionData.getHost(), socketConnectionData.getPort());
        this.in = new Scanner(socket.getInputStream());
        this.out = new PrintWriter(socket.getOutputStream(), true);

        this.homeView = homeView;
    }

    public void start(GameController gameController) throws Exception {
        this.gameController = gameController;
        try {
            String response = in.nextLine();
            System.out.println(response);
            if (!response.startsWith("WELCOME")) {
                new SocketConnectionView(homeView).load();
                return;
            }

            final String playerNumber = response.split(" ")[1];
            fx(() -> gameController.initWelcome(playerNumber));

            while (in.hasNextLine()) {
                response = in.nextLine();
                System.out.println(response);

                if (response.startsWith("INIT_PLAYER")) {
                    /* STEP 2 */
                    final boolean isYourTurn = response.split(" ")[1].equals("YOU");
                    fx(() -> gameController.initPlayer(isYourTurn));
                } else if (response.startsWith("MESSAGE")) {
                    /* STEP 1 */
                    String subMessage = response.substring(response.indexOf(" ") + 1);
                    if (subMessage.equals("WAIT_OPPONENT")) {
                        fx(() -> gameController.setInfosLabelI18N("game.info.wait_opponent"));
                    }
                }
            }
            out.println("QUIT");
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            socket.close();
            new SocketConnectionView(homeView).load();
        }
    }

    private void fx(Runnable run) {
        Platform.runLater(run);
    }

    public HomeView getHomeView() {
        return homeView;
    }
}
