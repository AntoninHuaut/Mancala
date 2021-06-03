package fr.antoninhuaut.mancala.controller.game;

import fr.antoninhuaut.mancala.controller.global.FXController;
import fr.antoninhuaut.mancala.controller.socket.MancalaSocket;
import fr.antoninhuaut.mancala.model.Cell;
import fr.antoninhuaut.mancala.utils.I18NUtils;
import fr.antoninhuaut.mancala.view.global.HomeView;
import fr.antoninhuaut.mancala.view.socket.SocketConnectionView;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.Random;

public class GameController extends FXController {

    private static final Logger LOGGER = LogManager.getLogger();

    private final HomeView homeView;
    private final MancalaSocket mancalaSocket;

    @FXML
    public Label infosLabel;

    @FXML
    public GridPane gameGrid;

    @FXML
    public Label pOneScoreLabel, pTwoScoreLabel, playersNameLabel, pOneMatchLabel, pTwoMatchLabel;

    @FXML
    public StackPane stackPlayerOne, stackPlayerTwo;
    @FXML
    public ImageView arrowPlayerOne, arrowPlayerTwo;

    @FXML
    public ImageView bol00, bol01, bol02, bol03, bol04, bol05, bol10, bol11, bol12, bol13, bol14, bol15;

    private int myPlayerId;
    private boolean isYourTurn = false;

    public GameController(MancalaSocket mancalaSocket) {
        this.homeView = mancalaSocket.getHomeView();
        this.mancalaSocket = mancalaSocket;
    }

    @Override
    public void postLoad() {
        LOGGER.debug("GameController postLoad");

        var random = new Random();
        for (ImageView bol : Arrays.asList(bol00, bol01, bol02, bol03, bol04, bol05, bol10, bol11, bol12, bol13, bol14, bol15)) {
            bol.setRotate(random.nextDouble() * 180);
        }

        playersNameLabel.setVisible(false);
        gameGrid.setVisible(false);

        new Thread(() -> {
            try {
                mancalaSocket.start(this);
            } catch (Exception ex) {
                ex.printStackTrace();
                // TODO alert?
                new SocketConnectionView(homeView).load();
            }
        }).start();
    }

    @FXML
    public void onCellClick(MouseEvent event) {
        if (!(event.getSource() instanceof StackPane)) return;

        var stackPane = (StackPane) event.getSource();
        var imageView = (ImageView) stackPane.getChildren().get(0);

        String[] idParts = imageView.getId().split("-");
        int line, col;
        try {
            line = Integer.parseInt(idParts[0]);
            col = Integer.parseInt(idParts[1]);
        } catch (NumberFormatException ignored) {
            return;
        }

        mancalaSocket.sendMove(line, col);
    }

    public void updateGameState(Cell[][] cells, int playerTurnId, int pOneScore, int pTwoScore) {
        var scene = infosLabel.getScene();
//        LOGGER.debug("Updating game state [playerTurnId: {}, pOneScore: {}, pTwoScore: {}]",
//                playerTurnId, pOneScore, pTwoScore);

        for (var line = 0; line < cells.length; ++line) {
            for (var col = 0; col < cells[line].length; ++col) {
                var cell = cells[line][col];
                var cellLabel = (Label) scene.lookup(String.format("#cell-%d-%d", line, col));
                cellLabel.setText("" + cell.getNbSeed());
            }
        }

        pOneScoreLabel.setText("" + pOneScore);
        pTwoScoreLabel.setText("" + pTwoScore);

        this.isYourTurn = playerTurnId == myPlayerId;
        setTurnLabel();
    }

    public void initWelcome(String playerNumberResponse) {
        boolean isPlayerOne = playerNumberResponse.equalsIgnoreCase("player_one");
        if (isPlayerOne) {
            this.myPlayerId = 0;
            this.arrowPlayerOne.setRotate(arrowPlayerOne.getRotate() + 3);
            this.stackPlayerTwo.setVisible(false);
        } else {
            this.myPlayerId = 1;
            this.arrowPlayerTwo.setRotate(arrowPlayerTwo.getRotate() + 3);
            this.stackPlayerOne.setVisible(false);
        }
    }

    public void initPostPlayerJoin(boolean isYourTurn) {
        this.isYourTurn = isYourTurn;
        setTurnLabel();
        gameGrid.setVisible(true);
    }

    public void setTurnLabel() {
        if (isYourTurn) {
            setInfosLabelI18N("game.info.turn_you", "#4caf50");
        } else {
            setInfosLabelI18N("game.info.turn_opponent", "#ee4e0d");
        }
    }

    public void setPlayersName(String opponentName) {
        playersNameLabel.setVisible(true);
        playersNameLabel.setText(mancalaSocket.getUsername() + " VS " + opponentName);
    }

    public void setInfosLabelI18N(String i18nKey, String hexColor) {
        if (infosLabel.textProperty().isBound()) infosLabel.textProperty().unbind();
        infosLabel.textProperty().bind(I18NUtils.getInstance().bindStr(i18nKey));

        if (hexColor != null) {
            infosLabel.setTextFill(Color.web(hexColor));
        }
    }
}