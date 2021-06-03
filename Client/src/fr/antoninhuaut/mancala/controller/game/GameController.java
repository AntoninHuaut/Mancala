package fr.antoninhuaut.mancala.controller.game;

import fr.antoninhuaut.mancala.controller.global.FXController;
import fr.antoninhuaut.mancala.controller.socket.MancalaSocket;
import fr.antoninhuaut.mancala.model.Cell;
import fr.antoninhuaut.mancala.model.views.game.GameData;
import fr.antoninhuaut.mancala.utils.I18NUtils;
import fr.antoninhuaut.mancala.view.global.HomeView;
import fr.antoninhuaut.mancala.view.socket.SocketConnectionView;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
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

    private GameData gameData;

    public GameController(MancalaSocket mancalaSocket) {
        this.homeView = mancalaSocket.getHomeView();
        this.mancalaSocket = mancalaSocket;
    }

    @Override
    public void postLoad() {
        LOGGER.debug("GameController postLoad");

        if (gameData == null) {
            gameData = new GameData();
            waitOpponent();

            new Thread(() -> {
                try {
                    mancalaSocket.start(this);
                } catch (IOException ignored) {
                    new SocketConnectionView(homeView).load();
                }
            }).start();
        }

        bindGameProperties();
    }

    private void bindGameProperties() {
        var random = new Random();
        List<ImageView> bolList = Arrays.asList(bol00, bol01, bol02, bol03, bol04, bol05, bol10, bol11, bol12, bol13, bol14, bol15);
        for (var i = 0; i < bolList.size(); i++) {
            var bol = bolList.get(i);

            if (gameData.getBols()[i] == null) {
                gameData.getBols()[i] = new SimpleDoubleProperty();
                gameData.getBols()[i].set(random.nextDouble() * 180);
            }

            bol.rotateProperty().bind(gameData.getBols()[i]);
        }

        for (ImageView arrowPlayer : Arrays.asList(arrowPlayerOne, arrowPlayerTwo)) {
            arrowPlayer.setRotate(3);
        }

        for (var line = 0; line < 2; ++line) {
            for (var col = 0; col < 6; ++col) {
                var cellLabel = (Label) gameGrid.getScene().lookup(String.format("#cell-%d-%d", line, col));

                if (gameData.getCells()[line][col] == null) gameData.getCells()[line][col] = new SimpleStringProperty();
                cellLabel.textProperty().bind(gameData.getCells()[line][col]);
            }
        }

        gameGrid.visibleProperty().bind(gameData.gameGridVisibilityProperty());
        infosLabel.textFillProperty().bind(gameData.infosLabelColorProperty());

        if (gameData.infosLabelTextProperty() != null) {
            if (infosLabel.textProperty().isBound()) infosLabel.textProperty().unbind();
            infosLabel.textProperty().bind(gameData.infosLabelTextProperty());
        }

        playersNameLabel.visibleProperty().bind(gameData.playersNameLabelVisiblityProperty());
        playersNameLabel.textProperty().bind(gameData.playersNameLabelTextProperty());
        pOneScoreLabel.textProperty().bind(gameData.pOneScoreLabelTextProperty());
        pTwoScoreLabel.textProperty().bind(gameData.pTwoScoreLabelTextProperty());
        
        stackPlayerOne.visibleProperty().bind(gameData.stackPlayerOneVisibilityProperty());
        stackPlayerTwo.visibleProperty().bind(gameData.stackPlayerTwoVisibilityProperty());
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
        LOGGER.debug("Updating game state [playerTurnId: {}, pOneScore: {}, pTwoScore: {}]", playerTurnId, pOneScore, pTwoScore);

        for (var line = 0; line < cells.length; ++line) {
            for (var col = 0; col < cells[line].length; ++col) {
                var cell = cells[line][col];
                gameData.getCells()[line][col].set("" + cell.getNbSeed());
            }
        }

        gameData.pOneScoreLabelTextProperty().set("" + pOneScore);
        gameData.pTwoScoreLabelTextProperty().set("" + pTwoScore);

        this.isYourTurn = playerTurnId == myPlayerId;
        setTurnLabel();
    }

    public void initWelcome(String playerNumberResponse) {
        boolean isPlayerOne = playerNumberResponse.equalsIgnoreCase("player_one");
        if (isPlayerOne) {
            this.myPlayerId = 0;
            gameData.stackPlayerOneVisibilityProperty().set(true);
        } else {
            this.myPlayerId = 1;
            gameData.stackPlayerTwoVisibilityProperty().set(true);
        }
    }

    public void initPostPlayerJoin(boolean isYourTurn) {
        this.isYourTurn = isYourTurn;
        setTurnLabel();
        gameData.gameGridVisibilityProperty().set(true);
    }

    public void setTurnLabel() {
        if (isYourTurn) {
            setInfosLabelI18N("game.info.turn_you", "#4caf50");
        } else {
            setInfosLabelI18N("game.info.turn_opponent", "#ee4e0d");
        }
    }

    public void waitOpponent() {
        gameData.playersNameLabelVisiblityProperty().set(false);
        gameData.gameGridVisibilityProperty().set(false);
        setInfosLabelI18N("game.info.wait_opponent", "#000000");
    }

    public void setPlayersName(String opponentName) {
        gameData.playersNameLabelVisiblityProperty().set(true);
        gameData.playersNameLabelTextProperty().set(mancalaSocket.getUsername() + " VS " + opponentName);
    }

    public void setInfosLabelI18N(String i18nKey, String hexColor) {
        if (infosLabel.textProperty().isBound()) infosLabel.textProperty().unbind();
        gameData.setInfosLabelTextProperty(I18NUtils.getInstance().bindStr(i18nKey));
        infosLabel.textProperty().bind(gameData.infosLabelTextProperty());

        if (hexColor != null) {
            gameData.infosLabelColorProperty().set(Color.web(hexColor));
        }
    }
}