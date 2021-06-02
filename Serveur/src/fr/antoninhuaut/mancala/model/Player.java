package fr.antoninhuaut.mancala.model;

import fr.antoninhuaut.mancala.match.Game;
import fr.antoninhuaut.mancala.match.Round;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Random;
import java.util.Scanner;

public class Player implements Runnable {

    private String username = "Player" + (new Random().nextInt(899) + 100);

    private final Game game;
    private int nbRoundWin;
    private int currentScore;

    private final Socket socket;
    private Scanner input;
    private ObjectOutputStream output;

    private final boolean isPlayerOne;

    public Player(Socket socket, Game game, boolean isPlayerOne) {
        this.socket = socket;
        this.game = game;
        this.isPlayerOne = isPlayerOne;
        this.nbRoundWin = 0;
    }

    @Override
    public void run() {
        try {
            setup();
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

    private void setup() throws IOException {
        this.input = new Scanner(socket.getInputStream());
        this.output = new ObjectOutputStream(socket.getOutputStream());

        String inputData = input.nextLine();
        String[] data = inputData.split(" ");
        if (data[0].equals("CLIENT_INIT")) {
            this.username = data[1];
        }

        sendData("WELCOME PLAYER_" + (isPlayerOne ? "ONE" : "TWO"));
        sendGameUpdate(game.getCurrentRound().getCells(), 0, 0, 0);

        if (isPlayerOne) {
            game.setPlayerOne(this);
            sendData("MESSAGE WAIT_OPPONENT");
        } else {
            game.setPlayerTwo(this);
            game.initPostPlayersJoined();
        }
    }

    private void processCommands() {
        while (input.hasNextLine()) {
            String command = input.nextLine();
            if (command.startsWith("QUIT")) {
                // TODO, exit game?
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
            sendData("VALID_MOVE"); // TODO utilité?
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