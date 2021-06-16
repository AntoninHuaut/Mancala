package fr.antoninhuaut.mancala.socket.player;

import fr.antoninhuaut.mancala.match.Round;
import fr.antoninhuaut.mancala.model.Cell;
import fr.antoninhuaut.mancala.socket.cenum.ClientToServerEnum;
import fr.antoninhuaut.mancala.socket.cenum.ServerToClientEnum;
import fr.antoninhuaut.mancala.socket.player.bot.Strategy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Random;

import static fr.antoninhuaut.mancala.match.Round.NB_LINE;

public class ClientBotPlayer implements Runnable {

    private static final Logger LOGGER = LogManager.getLogger();

    private final Strategy strategy;
    private final Socket clientSocket;
    private final ObjectInputStream clientInput;
    private final ObjectOutputStream clientOutput;

    private boolean isYourTurn;
    private int myPlayerId;
    private Cell[][] cells;

    public ClientBotPlayer(Strategy strategy, String sessionId, int port) throws IOException {
        this.strategy = strategy;
        this.clientSocket = createClientSocket(port);
        this.clientOutput = new ObjectOutputStream(clientSocket.getOutputStream());
        this.clientInput = new ObjectInputStream(clientSocket.getInputStream());

        var username = "Bot" + (new Random().nextInt(899) + 100);
        sendData(ClientToServerEnum.CLIENT_SESSION, sessionId);
        sendData(ClientToServerEnum.CLIENT_NAME, username);
    }

    @Override
    public void run() {
        try {
            do {
                String res = (String) clientInput.readObject();
                String[] arguments = res.split(" ");
                LOGGER.debug("Receive: {}", res);

                analyseRequest(ServerToClientEnum.extractFromCommand(arguments[0]), arguments);
            } while (true);
        } catch (Exception ignored) {
        } finally {
            disconnect();
        }
    }

    private void analyseRequest(ServerToClientEnum sEnum, String[] args) throws IOException {
        switch (sEnum) {
            case WELCOME:
                boolean isPlayerOne = args[1].equalsIgnoreCase("player_one");
                this.myPlayerId = isPlayerOne ? 0 : 1;
                break;
            case INIT_PLAYER:
                this.isYourTurn = args[1].equals("YOU");
                play(true);
                break;
            case ASK_TO_SURRENDER:
                sendData(ClientToServerEnum.ACCEPT_SURRENDER);
                break;
            case MESSAGE:
                final var errorKey = args[1].toLowerCase();
                if (errorKey.startsWith("FAILED")) {
                    play(false);
                }
                break;
            case GAME_UPDATE:
                var playerTurnId = clientInput.readInt();
                clientInput.readInt(); // pOneScore
                clientInput.readInt(); // pOneRoundWin
                clientInput.readInt(); // pTwoScore
                clientInput.readInt(); // pTwoRoundWin

                this.isYourTurn = playerTurnId == myPlayerId;

                this.cells = new Cell[Round.NB_LINE][Round.NB_COL];
                for (var line = 0; line < NB_LINE; ++line) {
                    for (var col = 0; col < Round.NB_COL; ++col) {
                        var cellLine = clientInput.readInt();
                        var cellCol = clientInput.readInt();
                        var cellNbSeed = clientInput.readInt();
                        cells[cellLine][cellCol] = new Cell(cellNbSeed);
                    }
                }

                play(true);
                break;
            default:
                break;
        }
    }

    private void play(boolean waitDelay) {
        if (!isYourTurn) return;
        if (cells == null) return;

        if (waitDelay) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException ignored) {
            }
        }

        var col = strategy.play(this, cells);
        sendMove(col);
    }

    public void disconnect() {
        try {
            clientSocket.close();
        } catch (IOException ignored) {
        }
    }

    public void sendMove(int col) {
        sendData(ClientToServerEnum.MOVE, "" + myPlayerId, "" + col);
    }

    public void sendData(ClientToServerEnum data, String... args) {
        try {
            var strArgs = String.join(" ", args);
            clientOutput.writeObject(data.name() + (strArgs.isEmpty() ? "" : " " + strArgs));
        } catch (IOException ignored) {
        }
    }

    private Socket createClientSocket(int port) throws IOException {
        return new Socket("127.0.0.1", port);
    }

    public int getMyPlayerId() {
        return myPlayerId;
    }
}