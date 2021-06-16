package fr.antoninhuaut.mancala.socket.player;

import fr.antoninhuaut.mancala.match.Round;
import fr.antoninhuaut.mancala.model.Cell;
import fr.antoninhuaut.mancala.model.PlayerData;
import fr.antoninhuaut.mancala.save.HighscoreManager;
import fr.antoninhuaut.mancala.socket.Session;
import fr.antoninhuaut.mancala.socket.cenum.ClientToServerEnum;
import fr.antoninhuaut.mancala.socket.cenum.ServerToClientEnum;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Objects;

public class ServerPlayer {

    protected static final Logger LOGGER = LogManager.getLogger();

    protected final Socket serverSocket;
    protected ObjectInputStream serverInput;
    protected ObjectOutputStream serverOutput;

    /* Init by Game */
    protected boolean isPlayerOne;
    protected Session session;

    public ServerPlayer(Socket serverSocket) throws IOException {
        this.serverSocket = serverSocket;
        this.serverInput = new ObjectInputStream(serverSocket.getInputStream());
        this.serverOutput = new ObjectOutputStream(serverSocket.getOutputStream());
    }

    public String waitSessionId() throws IOException, ClassNotFoundException {
        try {
            String inputData = (String) serverInput.readObject();
            String[] arguments = inputData.split(" ");
            LOGGER.debug("SessionId: {}", inputData);

            var clientCommand = ClientToServerEnum.extractFromCommand(arguments[0]);
            if (clientCommand == ClientToServerEnum.CLIENT_SESSION) {
                var sessionId = arguments[1];
                sessionId = sessionId == null || sessionId.isEmpty() || sessionId.isBlank()
                        || sessionId.equalsIgnoreCase("null")
                        ? null : sessionId.toUpperCase();

                LOGGER.debug("Converted sessionId: {}", sessionId);
                return sessionId;
            }
        } catch (ArrayIndexOutOfBoundsException | NumberFormatException ignored) {
        }
        return null;
    }

    public void waitSessionSetup(Session session, boolean isPlayerOne) throws IOException, ClassNotFoundException {
        this.session = session;
        this.isPlayerOne = isPlayerOne;

        try {
            String inputData = (String) serverInput.readObject();
            String[] arguments = inputData.split(" ");
            LOGGER.debug("SessionSetup: {}", inputData);

            var clientCommand = ClientToServerEnum.extractFromCommand(arguments[0]);
            if (clientCommand == ClientToServerEnum.CLIENT_NAME) {
                this.session.setPlayerName(getPlayerId(), arguments[1]);
            }
        } catch (ArrayIndexOutOfBoundsException | NumberFormatException ignored) {
        }
    }

    public void listenClient() {
        try {
            processCommands();
        } catch (IOException | ClassNotFoundException ignored) {
        } finally {
            LOGGER.debug("{}/{} quit", session.getPlayersData()[getPlayerId()].getUsername(), getPlayerId());
            session.removePlayer(this);

            try {
                serverSocket.close();
            } catch (IOException ignored) {
            }
        }
    }

    private void processCommands() throws IOException, ClassNotFoundException {
        while (true) {
            try {
                String inputData = (String) serverInput.readObject();
                String[] arguments = inputData.split(" ");
                LOGGER.debug("Receive from {}/{}: {}", session.getPlayersData()[getPlayerId()].getUsername(), getPlayerId(), inputData);

                var clientCommand = ClientToServerEnum.extractFromCommand(arguments[0]);
                switch (clientCommand) {
                    case MOVE:
                        session.getGame().getCurrentRound().play(this, Integer.parseInt(arguments[1]), Integer.parseInt(arguments[2]));
                        break;
                    case PLAY_WITH_BOT:
                        session.addBot(arguments[1]);
                        break;
                    case NEW_MATCH:
                        session.stopAndStartNewGame();
                        break;
                    case SAVE_MATCH:
                        session.saveGame(this);
                        break;
                    case LOAD_MATCH:
                        var loadData = inputData.split(" ");
                        session.loadGame(this, loadData[1]);
                        break;
                    case STOP_MATCH:
                        session.getGame().forceStopMatch();
                        break;
                    case ASK_FOR_SURRENDER_VOTE:
                        session.getGame().getCurrentRound().handleAskSurrenderVote(this);
                        break;
                    case ACCEPT_SURRENDER:
                        session.getGame().getCurrentRound().acceptSurrender(this);
                        break;
                    case REFUSE_SURRENDER:
                        session.getGame().getCurrentRound().denySurrender();
                        break;
                    case SOLO_SURRENDER:
                        session.getGame().getCurrentRound().soloSurrender(this);
                        break;
                    case UNDO:
                        session.getGame().getCurrentRound().undo(getPlayerId());
                        break;
                    case ASK_HIGHSCORE:
                        sendData(ServerToClientEnum.RESPONSE_HIGHSCORE, HighscoreManager.getInstance().getHighscoreSerialize());
                        break;
                    default:
                        break;
                }
            } catch (ArrayIndexOutOfBoundsException | NumberFormatException ignored) {
            } catch (IllegalStateException ex) {
                sendData(ServerToClientEnum.MESSAGE, ex.getMessage());
            }
        }
    }

    public void gameSetup() {
        sendData(ServerToClientEnum.WELCOME, "PLAYER_" + (isPlayerOne ? "ONE" : "TWO"), session.getSessionId());
    }

    public void sendData(ServerToClientEnum data, String... args) {
        try {
            LOGGER.debug("Send data: {}, {}", data, args);
            var strArgs = String.join(" ", args);
            serverOutput.writeObject(data.name() + (strArgs.isEmpty() ? "" : " " + strArgs));
        } catch (IOException ignored) {
        }
    }

    public void sendGameUpdate(Cell[][] cells, int turnPlayerId, PlayerData[] playerData) {
        try {
            serverOutput.writeObject(ServerToClientEnum.GAME_UPDATE.name());
            serverOutput.writeInt(turnPlayerId);
            serverOutput.writeInt(playerData[0].getCurrentScore());
            serverOutput.writeInt(playerData[0].getNbRoundWin());
            serverOutput.writeInt(playerData[1].getCurrentScore());
            serverOutput.writeInt(playerData[1].getNbRoundWin());

            for (var line = 0; line < Round.NB_LINE; ++line) {
                for (var col = 0; col < Round.NB_COL; ++col) {
                    serverOutput.writeInt(line);
                    serverOutput.writeInt(col);
                    serverOutput.writeInt(cells[line][col].getNbSeed());
                }
            }
            serverOutput.flush();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public int getPlayerId() {
        return isPlayerOne ? 0 : 1;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        var player = (ServerPlayer) o;
        return this.isPlayerOne == player.isPlayerOne;
    }

    @Override
    public int hashCode() {
        return Objects.hash(isPlayerOne);
    }
}
