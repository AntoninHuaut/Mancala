package fr.antoninhuaut.mancala.socket;

import fr.antoninhuaut.mancala.match.Game;
import fr.antoninhuaut.mancala.match.Round;
import fr.antoninhuaut.mancala.model.Cell;
import fr.antoninhuaut.mancala.model.PlayerData;
import fr.antoninhuaut.mancala.socket.cenum.ClientToServerEnum;
import fr.antoninhuaut.mancala.socket.cenum.ServerToClientEnum;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Objects;

public class Player {

    private static final Logger LOGGER = LogManager.getLogger();

    private final Socket socket;
    private ObjectInputStream input;
    private ObjectOutputStream output;

    /* Init by Game */
    private boolean isPlayerOne;
    private Game game;

    public Player(Socket socket) {
        this.socket = socket;
    }

    public void waitSetup(Game game, boolean isPlayerOne) throws IOException, ClassNotFoundException {
        this.game = game;
        this.isPlayerOne = isPlayerOne;

        this.input = new ObjectInputStream(socket.getInputStream());
        this.output = new ObjectOutputStream(socket.getOutputStream());

        try {
            String inputData = (String) input.readObject();
            String[] arguments = inputData.split(" ");
            LOGGER.debug("SETUP: {}", inputData);

            var clientCommand = ClientToServerEnum.extractFromCommand(arguments[0]);
            if (clientCommand == ClientToServerEnum.CLIENT_INIT) {
                this.game.setPlayerName(getPlayerId(), arguments[1]);
            }
        } catch (ArrayIndexOutOfBoundsException | NumberFormatException ignored) {
        }

        sendData(ServerToClientEnum.WELCOME, "PLAYER_" + (isPlayerOne ? "ONE" : "TWO"));
    }

    public void listenClient() {
        try {
            processCommands();
        } catch (IOException | ClassNotFoundException ignored) {
        } finally {
            LOGGER.debug("Player{} quit", getPlayerId());
            game.getSession().removePlayer(this);

            try {
                socket.close();
            } catch (IOException ignored) {
            }
        }
    }

    private void processCommands() throws IOException, ClassNotFoundException {
        while (true) {
            try {
                String inputData = (String) input.readObject();
                String[] arguments = inputData.split(" ");
                LOGGER.debug("Receive from Player{}: {}", getPlayerId(), inputData);

                var clientCommand = ClientToServerEnum.extractFromCommand(arguments[0]);
                if (clientCommand == ClientToServerEnum.MOVE) { // MOVE <line> <col>
                    String[] data = inputData.split(" ");
                    game.getCurrentRound().play(this, Integer.parseInt(data[1]), Integer.parseInt(data[2]));
                } //
                else if (clientCommand == ClientToServerEnum.STOP_MATCH) {
                    game.endGame();
                } //
                else if (clientCommand == ClientToServerEnum.UNDO) {
                    game.getCurrentRound().undo(getPlayerId());
                } //
            } catch (ArrayIndexOutOfBoundsException | NumberFormatException ignored) {
            } catch (IllegalStateException ex) {
                sendData(ServerToClientEnum.BAD_STATE, ex.getMessage());
            }
        }
    }

    public void sendData(ServerToClientEnum data, String... args) {
        try {
            var strArgs = String.join(" ", args);
            output.writeObject(data.name() + (strArgs.isEmpty() ? "" : " " + strArgs));
        } catch (IOException ignored) {
        }
    }

    public void sendGameUpdate(Cell[][] cells, int turnPlayerId, PlayerData[] playerData) {
        try {
            output.writeObject(ServerToClientEnum.GAME_UPDATE.name());
            output.writeInt(turnPlayerId);
            output.writeInt(playerData[0].getCurrentScore());
            output.writeInt(playerData[0].getNbRoundWin());
            output.writeInt(playerData[1].getCurrentScore());
            output.writeInt(playerData[1].getNbRoundWin());

            for (var line = 0; line < Round.NB_LINE; ++line) {
                for (var col = 0; col < Round.NB_COL; ++col) {
                    output.writeInt(line);
                    output.writeInt(col);
                    output.writeInt(cells[line][col].getNbSeed());
                }
            }
            output.flush();
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
        var player = (Player) o;
        return this.isPlayerOne == player.isPlayerOne;
    }

    @Override
    public int hashCode() {
        return Objects.hash(isPlayerOne);
    }
}