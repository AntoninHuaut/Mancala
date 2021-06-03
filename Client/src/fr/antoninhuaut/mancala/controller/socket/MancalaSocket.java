package fr.antoninhuaut.mancala.controller.socket;

import fr.antoninhuaut.mancala.controller.game.GameController;
import fr.antoninhuaut.mancala.model.Cell;
import fr.antoninhuaut.mancala.model.enums.ServerToClientEnum;
import fr.antoninhuaut.mancala.model.enums.ClientToServerEnum;
import fr.antoninhuaut.mancala.model.views.socket.SocketConnectionData;
import fr.antoninhuaut.mancala.view.global.HomeView;
import fr.antoninhuaut.mancala.view.socket.SocketConnectionView;
import javafx.application.Platform;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Locale;

public class MancalaSocket {

    private static final Logger LOGGER = LogManager.getLogger();

    public static final int NB_LINE = 2;
    public static final int NB_COL = 6;

    private final Socket socket;
    private final ObjectInputStream input;
    private final ObjectOutputStream output;

    private final HomeView homeView;
    private GameController gameController;
    private final String username;

    public MancalaSocket(SocketConnectionData socketConnectionData, HomeView homeView) throws IOException {
        this.socket = new Socket(socketConnectionData.getHost(), socketConnectionData.getPort());
        this.output = new ObjectOutputStream(socket.getOutputStream());
        this.input = new ObjectInputStream(socket.getInputStream());
        this.username = socketConnectionData.getUsername();
        this.homeView = homeView;

        sendData(ClientToServerEnum.CLIENT_INIT, socketConnectionData.getUsername());
        this.homeView.getController().setMancalaSocket(this);
    }

    public void start(GameController gameController) throws IOException {
        this.gameController = gameController;
        try {
            do {
                String res = (String) input.readObject();
                String[] arguments = res.split(" ");
                LOGGER.debug("Receive: {}", res);

                analyseRequest(ServerToClientEnum.extractFromCommand(arguments[0]), arguments);
            } while (true);
        } catch (Exception ignored) {
        } finally {
            socket.close();
            new SocketConnectionView(homeView).load();
        }
    }

    private void analyseRequest(ServerToClientEnum sEnum, String[] args) throws IOException {
        if (sEnum == ServerToClientEnum.WELCOME) {
            final String playerNumber = args[1];
            fx(() -> gameController.initWelcome(playerNumber));
        } //
        else if (sEnum == ServerToClientEnum.WAIT_OPPONENT) {
            fx(() -> gameController.waitOpponent());
        } //
        else if (sEnum == ServerToClientEnum.INIT_PLAYER) {
            final boolean isYourTurn = args[1].equals("YOU");
            fx(() -> gameController.initPostPlayerJoin(isYourTurn));
        } //
        else if (sEnum == ServerToClientEnum.OPPONENT_NAME) {
            final String opponentName = args[1];
            fx(() -> gameController.setPlayersName(opponentName));
        } //
        else if (sEnum == ServerToClientEnum.BAD_STATE) {
            final String errorKey = args[1].toLowerCase();
            fx(() -> gameController.setErrorLabel(errorKey));
        } //
        else if (sEnum == ServerToClientEnum.GAME_UPDATE) {
            var playerTurnId = input.readInt();
            var pOneScore = input.readInt();
            var pTwoScore = input.readInt();

            var cells = new Cell[NB_LINE][NB_COL];
            for (var line = 0; line < NB_LINE; ++line) {
                for (var col = 0; col < NB_COL; ++col) {
                    var cellLine = input.readInt();
                    var cellCol = input.readInt();
                    var cellNbSeed = input.readInt();
                    cells[cellLine][cellCol] = new Cell(cellNbSeed);
                }
            }

            fx(() -> gameController.updateGameState(cells, playerTurnId, pOneScore, pTwoScore));
        } //
    }

    public void disconnect() {
        try {
            socket.close();
        } catch (IOException ignored) {
        }
    }

    public void sendMove(int line, int col) {
        sendData(ClientToServerEnum.MOVE, "" + line, "" + col);
    }

    public void sendData(ClientToServerEnum data, String... args) {
        try {
            var strArgs = String.join(" ", args);
            output.writeObject(data.name() + (strArgs.isEmpty() ? "" : " " + strArgs));
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
