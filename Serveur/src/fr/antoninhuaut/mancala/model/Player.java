package fr.antoninhuaut.mancala.model;

import fr.antoninhuaut.mancala.match.Game;
import fr.antoninhuaut.mancala.match.Round;
import fr.antoninhuaut.mancala.socket.cenum.ClientToServerEnum;
import fr.antoninhuaut.mancala.socket.cenum.ServerToClientEnum;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Objects;
import java.util.Random;
import java.util.Scanner;

public class Player {

    private static final Logger LOGGER = LogManager.getLogger();

    private String username = "Player" + (new Random().nextInt(899) + 100);

    private int nbRoundWin;
    private int currentScore;

    private final Socket socket;
    private ObjectInputStream input;
    private ObjectOutputStream output;

    /* Init by Game */
    private boolean isPlayerOne;
    private Game game;

    public Player(Socket socket) {
        this.socket = socket;
        this.nbRoundWin = 0;
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
                this.username = arguments[1];
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
            LOGGER.debug("Player {} quit", username);
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
                LOGGER.debug("Receive from {}: {}", username, inputData);

                var clientCommand = ClientToServerEnum.extractFromCommand(arguments[0]);
                if (clientCommand == ClientToServerEnum.MOVE) { // MOVE <line> <col>
                    String[] data = inputData.split(" ");
                    processMoveCommand(Integer.parseInt(data[1]), Integer.parseInt(data[2]));
                } //
            } catch (ArrayIndexOutOfBoundsException | NumberFormatException ignored) {
                ignored.printStackTrace();
            }
        }
    }

    private void processMoveCommand(int line, int col) {
        try {
            game.getCurrentRound().play(this, line, col);
        } catch (IllegalStateException ex) {
            sendData(ServerToClientEnum.BAD_STATE, ex.getMessage());
        }
    }

    public void sendData(ServerToClientEnum data, String... args) {
        try {
            var strArgs = String.join(" ", args);
            output.writeObject(data.name() + (strArgs.isEmpty() ? "" : " " + strArgs));
        } catch (IOException ignored) {
        }
    }

    public void sendGameUpdate(Cell[][] cells, int turnPlayerId, int pOneScore, int pTwoScore) {
        try {
            output.writeObject(ServerToClientEnum.GAME_UPDATE.name());
            output.writeInt(turnPlayerId);
            output.writeInt(pOneScore);
            output.writeInt(pTwoScore);

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

    public boolean hasWin() {
        return currentScore >= 25;
    }

    public void addScore(int addScore) {
        this.currentScore += addScore;
    }

    public int getCurrentScore() {
        return currentScore;
    }

    public int getPlayerId() {
        return isPlayerOne ? 0 : 1;
    }

    public String getUsername() {
        return username;
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