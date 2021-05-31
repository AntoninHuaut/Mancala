package ensi.game;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Player implements Runnable {

    private final Game game;
    private int score;

    private final Socket socket;
    private Scanner input;
    private PrintWriter output;

    private final boolean isPlayerOne;

    public Player(Socket socket, Game game, boolean isPlayerOne) {
        this.socket = socket;
        this.game = game;
        this.isPlayerOne = isPlayerOne;
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
            } catch (IOException ignored) {
            }
        }
    }

    private void setup() throws IOException {
        this.input = new Scanner(socket.getInputStream());
        this.output = new PrintWriter(socket.getOutputStream(), true);
        sendData("WELCOME PLAYER_" + (isPlayerOne ? "ONE" : "TWO"));

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
            } else if (command.startsWith("MOVE")) { // MOVE <line> <col>
                String[] data = command.split(" ");
                processMoveCommand(Integer.parseInt(data[1]), Integer.parseInt(data[2]));
            }
        }
    }

    private void processMoveCommand(int line, int col) {
        try {
            game.play(this, line, col);
            sendData("VALID_MOVE");
        } catch (IllegalStateException ex) {
            sendData("EXCEPTION " + ex.getMessage());
        }
    }

    public void sendData(String data) {
        output.println(data);
        output.flush();
    }

    public void sendGameUpdate(Cell[][] cells) {
        output.println("GAME_UPDATE");
        output.println((Object) cells); // TODO TEST
        output.flush();
    }

    public boolean hasWin() {
        return score >= 25;
    }

    public void addScore(int addScore) {
        this.score += addScore;
    }

    public int getPlayerId() {
        return isPlayerOne ? 1 : 2;
    }
}