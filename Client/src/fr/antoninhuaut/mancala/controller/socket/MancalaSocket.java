package fr.antoninhuaut.mancala.controller.socket;

import fr.antoninhuaut.mancala.controller.game.GameController;
import fr.antoninhuaut.mancala.model.Cell;
import fr.antoninhuaut.mancala.model.enums.SocketExchangeEnum;
import fr.antoninhuaut.mancala.model.enums.SocketMessageEnum;
import fr.antoninhuaut.mancala.model.views.socket.SocketConnectionData;
import fr.antoninhuaut.mancala.view.global.HomeView;
import fr.antoninhuaut.mancala.view.socket.SocketConnectionView;
import javafx.application.Platform;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class MancalaSocket {

    public static final int NB_LINE = 2;
    public static final int NB_COL = 6;

    private final Socket socket;
    private final ObjectInputStream in;
    private final PrintWriter out;

    private final HomeView homeView;
    private GameController gameController;
    private final String username;

    public MancalaSocket(SocketConnectionData socketConnectionData, HomeView homeView) throws Exception {
        this.socket = new Socket(socketConnectionData.getHost(), socketConnectionData.getPort());
        this.in = new ObjectInputStream(socket.getInputStream());
        this.out = new PrintWriter(socket.getOutputStream(), true);
        this.username = socketConnectionData.getUsername();
        this.homeView = homeView;

        this.out.println("CLIENT_INIT " + socketConnectionData.getUsername());
    }

    public void start(GameController gameController) throws Exception {
        this.gameController = gameController;
        try {
            do {
                String res = (String) in.readObject();
                String[] arguments = res.split(" ");
                System.out.println(res);

                try {
                    analyseRequest(SocketExchangeEnum.valueOf(arguments[0]), arguments);
                } catch (IllegalArgumentException ignored) {
                }
            } while (true); // TODO SERVER MSG TO EXIT
        } catch (Exception ex) {
            ex.printStackTrace();
            // TODO alert?
        } finally {
            socket.close();
            new SocketConnectionView(homeView).load();
        }
    }

    private void analyseRequest(SocketExchangeEnum sEnum, String[] args) throws IOException, ClassNotFoundException {
        if (sEnum == SocketExchangeEnum.WELCOME) {
            final String playerNumber = args[1];
            fx(() -> gameController.initWelcome(playerNumber));
        } //
        else if (sEnum == SocketExchangeEnum.MESSAGE) {
            try {
                analyseMessage(SocketMessageEnum.valueOf(args[1]), args);
            } catch (IllegalArgumentException ignored) {
            }
        } //
        else if (sEnum == SocketExchangeEnum.INIT_PLAYER) {
            final boolean isYourTurn = args[1].equals("YOU");
            fx(() -> gameController.initPostPlayerJoin(isYourTurn));
        } //
        else if (sEnum == SocketExchangeEnum.OPPONENT_NAME) {
            final String opponentName = args[1];
            fx(() -> gameController.setPlayersName(opponentName));
        } //
        else if (sEnum == SocketExchangeEnum.GAME_UPDATE) {
            int pOneScore = in.readInt();
            int pTwoScore = in.readInt();

            Cell[][] cells = new Cell[NB_LINE][NB_COL];
            for (int line = 0; line < NB_LINE; ++line) {
                for (int col = 0; col < NB_COL; ++col) {
                    int cellLine = in.readInt();
                    int cellCol = in.readInt();
                    int cellNbSeed = in.readInt();
                    cells[cellLine][cellCol] = new Cell(cellNbSeed);
                }
            }

            fx(() -> gameController.updateCells_Score(cells, pOneScore, pTwoScore));
        } //
    }

    private void analyseMessage(SocketMessageEnum msgEnum, String[] arguments) {
        if (msgEnum == SocketMessageEnum.WAIT_OPPONENT) {
            fx(() -> gameController.setInfosLabelI18N("game.info.wait_opponent"));
        } //
        else if (msgEnum == SocketMessageEnum.SWITCH_TURN) {
            fx(() -> gameController.switchTurn());
        }
    }

    public void sendMove(int line, int col) {
        out.println(String.format("MOVE %d %d", line, col));
    }

    private void fx(Runnable run) {
        Platform.runLater(run);
    }

    public String getUsername() {
        return username;
    }

    public HomeView getHomeView() {
        return homeView;
    }
}
