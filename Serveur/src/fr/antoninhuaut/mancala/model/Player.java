package fr.antoninhuaut.mancala.model;

import fr.antoninhuaut.mancala.match.Game;
import fr.antoninhuaut.mancala.match.Round;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Random;
import java.util.Scanner;

public class Player {

    private static final Logger LOGGER = LogManager.getLogger();

    private String username = "Player" + (new Random().nextInt(899) + 100);

    private int nbRoundWin;
    private int currentScore;

    private final Socket socket;
    private Scanner input;
    private ObjectOutputStream output;

    /* Init by Game */
    private boolean isPlayerOne;
    private Game game;

    public Player(Socket socket) {
        this.socket = socket;
        this.nbRoundWin = 0;
    }

    public void runStart() {
        try {
            processCommands();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException ex) {
                ex.printStackTrace();
                // TODO
            }
        }
    }

    public void setup(Game game, boolean isPlayerOne) throws IOException {
        this.game = game;
        this.isPlayerOne = isPlayerOne;

        this.input = new Scanner(socket.getInputStream());
        this.output = new ObjectOutputStream(socket.getOutputStream());

        String inputData = input.nextLine();
        String[] data = inputData.split(" ");
        if (data[0].equals("CLIENT_INIT")) {
            this.username = data[1];
            LOGGER.debug("Receive CLIENT_INIT: {}", username);
        }

        sendData("WELCOME PLAYER_" + (isPlayerOne ? "ONE" : "TWO"));
    }

    private void processCommands() {
        while (input.hasNextLine()) {
            String command = input.nextLine();
            LOGGER.debug("Receive from {}: {}", username, command);

            if (command.startsWith("QUIT")) {
                game.getSession().removePlayer(this);
                return;
            } //
            else if (command.startsWith("MOVE")) { // MOVE <line> <col>
                String[] data = command.split(" ");
                processMoveCommand(Integer.parseInt(data[1]), Integer.parseInt(data[2]));
            } //
        }
    }

    private void processMoveCommand(int line, int col) {
        try {
            game.getCurrentRound().play(this, line, col);
        } catch (IllegalStateException ex) {
            sendData("MESSAGE " + ex.getMessage());
        }
    }

    public void sendData(String data) {
        try {
            output.writeObject(data);
            output.flush();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void sendGameUpdate(Cell[][] cells, int turnPlayerId, int pOneScore, int pTwoScore) {
        try {
            output.writeObject("GAME_UPDATE");
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
}