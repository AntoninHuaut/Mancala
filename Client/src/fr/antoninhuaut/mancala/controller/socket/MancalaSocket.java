package fr.antoninhuaut.mancala.controller.socket;

import fr.antoninhuaut.mancala.controller.game.GameController;
import fr.antoninhuaut.mancala.model.Cell;
import fr.antoninhuaut.mancala.model.enums.SocketExchangeEnum;
import fr.antoninhuaut.mancala.model.enums.SocketMessageEnum;
import fr.antoninhuaut.mancala.model.views.socket.SocketConnectionData;
import fr.antoninhuaut.mancala.view.global.HomeView;
import fr.antoninhuaut.mancala.view.socket.SocketConnectionView;
import javafx.application.Platform;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class MancalaSocket {

    private static final Logger LOGGER = LogManager.getLogger();

    public static final int NB_LINE = 2;
    public static final int NB_COL = 6;

    private final Socket socket;
    private final ObjectInputStream in;
    private final ObjectOutputStream out;

    private final HomeView homeView;
    private GameController gameController;
    private final String username;

    public MancalaSocket(SocketConnectionData socketConnectionData, HomeView homeView) throws IOException {
        this.socket = new Socket(socketConnectionData.getHost(), socketConnectionData.getPort());
        this.out = new ObjectOutputStream(socket.getOutputStream());
        this.in = new ObjectInputStream(socket.getInputStream());
        this.username = socketConnectionData.getUsername();
        this.homeView = homeView;

        sendData("CLIENT_INIT " + socketConnectionData.getUsername());
        this.homeView.getController().setMancalaSocket(this);
    }

    public void start(GameController gameController) throws IOException {
        this.gameController = gameController;
        try {
            do {
                String res = (String) in.readObject();
                String[] arguments = res.split(" ");
                LOGGER.debug("Receive: {}", res);

                analyseRequest(SocketExchangeEnum.extractFromCommand(arguments[0]), arguments);
            } while (true);
        } catch (Exception ignored) {
        } finally {
            socket.close();
            new SocketConnectionView(homeView).load();
        }
    }

    private void analyseRequest(SocketExchangeEnum sEnum, String[] args) throws IOException {
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
            var playerTurnId = in.readInt();
            var pOneScore = in.readInt();
            var pTwoScore = in.readInt();

            var cells = new Cell[NB_LINE][NB_COL];
            for (var line = 0; line < NB_LINE; ++line) {
                for (var col = 0; col < NB_COL; ++col) {
                    var cellLine = in.readInt();
                    var cellCol = in.readInt();
                    var cellNbSeed = in.readInt();
                    cells[cellLine][cellCol] = new Cell(cellNbSeed);
                }
            }

            fx(() -> gameController.updateGameState(cells, playerTurnId, pOneScore, pTwoScore));
        } //
    }

    private void analyseMessage(SocketMessageEnum msgEnum, String[] arguments) {
        if (msgEnum == SocketMessageEnum.WAIT_OPPONENT) {
            fx(() -> gameController.waitOpponent());
        } //
    }

    public void disconnect() {
        try {
            socket.close();
        } catch (IOException ignored) {
        }
    }

    public void sendMove(int line, int col) {
        sendData(String.format("MOVE %d %d", line, col));
    }

    public void sendData(String data) {
        try {
            out.writeObject(data);
        } catch (IOException ignored) {
        }
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
