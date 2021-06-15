package fr.antoninhuaut.mancala.controller.game;

import fr.antoninhuaut.mancala.controller.global.FXController;
import fr.antoninhuaut.mancala.controller.socket.MancalaSocket;
import fr.antoninhuaut.mancala.model.Cell;
import fr.antoninhuaut.mancala.model.enums.ClientToServerEnum;
import fr.antoninhuaut.mancala.model.enums.UserPrefType;
import fr.antoninhuaut.mancala.model.views.game.CellData;
import fr.antoninhuaut.mancala.model.views.game.GameData;
import fr.antoninhuaut.mancala.utils.I18NUtils;
import fr.antoninhuaut.mancala.utils.PreferenceUtils;
import fr.antoninhuaut.mancala.view.global.HomeView;
import fr.antoninhuaut.mancala.view.socket.SocketConnectionView;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.*;

public class GameController extends FXController {

    private static final String BLACK_COLOR = "#000000";
    private static final String GREEN_COLOR = "#4caf50";
    private static final String RED_COLOR = "#FF0000";
    private static final String BLUE_COLOR = "#00B2EE";
    private static final String HBOXTURN_CLASS = "hboxTurn";
    private static final Logger LOGGER = LogManager.getLogger();

    private final HomeView homeView;
    private final MancalaSocket mancalaSocket;

    @FXML
    public Label infosLabel, errorLabel;

    @FXML
    public GridPane gameGrid;

    @FXML
    public Label pOneScoreLabel, pTwoScoreLabel, playersNameLabel, pOneMatchLabel, pTwoMatchLabel;

    @FXML
    public StackPane stackPlayerOne, stackPlayerTwo;
    @FXML
    public ImageView arrowPlayerOne, arrowPlayerTwo;

    @FXML
    public HBox pOneBox, pTwoBox;

    @FXML
    public Button surrenderBtn;

    @FXML
    public ImageView bol00, bol01, bol02, bol03, bol04, bol05, bol10, bol11, bol12, bol13, bol14, bol15;

    private int myPlayerId;
    private Boolean isYourTurn = null;

    private GameData gameData;
    private final Timer errorTimer = new Timer();
    private TimerTask errorTimerTask;

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

    @Override
    public void unload() {
        cancelTimerTask();
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
                var cellStack = (StackPane) gameGrid.getScene().lookup(String.format("#cell-%d-%d", line, col));
                var cellImage = (ImageView) cellStack.getChildren().get(0);
                var cellLabel = (Label) cellStack.getChildren().get(1);

                CellData cellData;

                if (gameData.getCells()[line][col] == null) {
                    cellData = new CellData();
                    gameData.getCells()[line][col] = cellData;
                } else {
                    cellData = gameData.getCells()[line][col];
                }

                BooleanProperty seeSeed = PreferenceUtils.getInstance().getSettingsPrefs().get(UserPrefType.SEE_SEED);
                BooleanProperty seeSeedHover = PreferenceUtils.getInstance().getSettingsPrefs().get(UserPrefType.SEE_SEED_HOVER);

                // seeSeed OR (seeSeedHover AND hoverProperty)
                cellLabel.visibleProperty().bind(Bindings.or(seeSeed, Bindings.and(seeSeedHover, cellStack.hoverProperty())));
                cellLabel.textProperty().bind(cellData.seedProperty());

                cellData.setImageView(cellImage);
                cellData.apply();
            }
        }

        gameGrid.visibleProperty().bind(gameData.gameGridVisibilityProperty());
        errorLabel.visibleProperty().bind(gameData.errorLabelVisibilityProperty());
        infosLabel.textFillProperty().bind(gameData.infosLabelColorProperty());

        if (gameData.infosLabelTextProperty() != null) {
            infosLabel.textProperty().bind(gameData.infosLabelTextProperty());
        }
        if (gameData.errorLabelTextProperty() != null) {
            errorLabel.textProperty().bind(gameData.errorLabelTextProperty());
        }

        playersNameLabel.visibleProperty().bind(gameData.playersNameLabelVisiblityProperty());
        playersNameLabel.textProperty().bind(gameData.playersNameLabelTextProperty());

        pOneScoreLabel.textProperty().bind(gameData.pOneScoreLabelTextProperty());
        pTwoScoreLabel.textProperty().bind(gameData.pTwoScoreLabelTextProperty());
        pOneMatchLabel.textProperty().bind(Bindings.concat(gameData.pOneMatchLabelTextProperty(), "/6"));
        pTwoMatchLabel.textProperty().bind(Bindings.concat(gameData.pTwoMatchLabelTextProperty(), "/6"));

        stackPlayerOne.visibleProperty().bind(gameData.stackPlayerOneVisibilityProperty());
        stackPlayerTwo.visibleProperty().bind(gameData.stackPlayerTwoVisibilityProperty());

        surrenderBtn.disableProperty().bind(gameData.surrenderBtnDisableProperty());
        surrenderBtn.visibleProperty().bind(gameData.gameGridVisibilityProperty());

        setTurnLabel();

        if (isYourTurn == null) {
            waitOpponent();
        }
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

    @FXML
    public void suggestSurender() {
        mancalaSocket.sendData(ClientToServerEnum.ASK_FOR_SURRENDER_VOTE);
    }

    public void updateGameState(Cell[][] cells, int playerTurnId, int pOneScore, int pTwoScore) {
        LOGGER.debug("Updating game state [playerTurnId: {}, pOneScore: {}, pTwoScore: {}]", playerTurnId, pOneScore, pTwoScore);

        var nbCellsRemaining = 0;

        for (var line = 0; line < cells.length; ++line) {
            for (var col = 0; col < cells[line].length; ++col) {
                var cell = cells[line][col];
                gameData.getCells()[line][col].setNbSeed(cell.getNbSeed());
                nbCellsRemaining += cell.getNbSeed();
            }
        }

        gameData.surrenderBtnDisableProperty().set(nbCellsRemaining > 10);
        gameData.pOneScoreLabelTextProperty().set("" + pOneScore);
        gameData.pTwoScoreLabelTextProperty().set("" + pTwoScore);

        this.isYourTurn = playerTurnId == myPlayerId;
        setTurnLabel();
        cancelTimerTask();
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

    private void resetHBox() {
        pOneBox.getStyleClass().clear();
        pTwoBox.getStyleClass().clear();
    }

    public void setTurnLabel() {
        resetHBox();
        HBox myBox = myPlayerId == 0 ? pOneBox : pTwoBox;
        if (Boolean.TRUE.equals(isYourTurn)) {
            setInfosLabel("game.info.turn_you", GREEN_COLOR);
            myBox.getStyleClass().add(HBOXTURN_CLASS);
        } else {
            setInfosLabel("game.info.turn_opponent", BLUE_COLOR);
        }
    }

    public void waitOpponent() {
        gameData.playersNameLabelVisiblityProperty().set(false);
        gameData.gameGridVisibilityProperty().set(false);
        isYourTurn = null;
        setInfosLabel("game.info.wait_opponent", BLACK_COLOR);
    }

    public void setPlayersName(String opponentName) {
        gameData.playersNameLabelVisiblityProperty().set(true);
        gameData.playersNameLabelTextProperty().set(mancalaSocket.getUsername() + " VS " + opponentName);
    }

    public void setInfosLabel(String i18nKey, String hexColor) {
        if (infosLabel.textProperty().isBound()) infosLabel.textProperty().unbind();
        gameData.setInfosLabelTextProperty(I18NUtils.getInstance().bindStr(i18nKey));
        infosLabel.textProperty().bind(gameData.infosLabelTextProperty());

        if (hexColor != null) {
            gameData.infosLabelColorProperty().set(Color.web(hexColor));
        }
    }

    public void setErrorLabel(String i18nKey) {
        if (errorLabel.textProperty().isBound()) errorLabel.textProperty().unbind();

        gameData.setErrorLabelTextProperty(I18NUtils.getInstance().bindStr("game.error." + i18nKey));
        errorLabel.textProperty().bind(gameData.errorLabelTextProperty());

        cancelTimerTask();
        gameData.errorLabelVisibilityProperty().set(true);
        errorTimerTask = new TimerTask() {
            @Override
            public void run() {
                gameData.errorLabelVisibilityProperty().set(false);
            }
        };
        errorTimer.schedule(errorTimerTask, 2500);
    }

    public void setMatchLabel(int pOneMatchWin, int pTwoMatchWin) {
        gameData.pOneMatchLabelTextProperty().set("" + pOneMatchWin);
        gameData.pTwoMatchLabelTextProperty().set("" + pTwoMatchWin);
    }

    public void setWinnerRound(int winnerId) {
        resetHBox();
        if (winnerId == -1) {
            setInfosLabel("game.end.round.tie", BLUE_COLOR);
        } else if (myPlayerId == winnerId) {
            setInfosLabel("game.end.round.win", GREEN_COLOR);
        } else {
            setInfosLabel("game.end.round.lose", RED_COLOR);
        }
    }

    public void setWinnerMatch(int winnerId) {
        resetHBox();
        gameData.gameGridVisibilityProperty().set(false);
        if (winnerId == -1) {
            setInfosLabel("game.end.match.tie", BLUE_COLOR);
        } else if (myPlayerId == winnerId) {
            setInfosLabel("game.end.match.win", GREEN_COLOR);
        } else {
            setInfosLabel("game.end.match.lose", RED_COLOR);
        }
    }

    private void cancelTimerTask() {
        if (errorTimerTask != null) {
            gameData.errorLabelVisibilityProperty().set(false);
            errorTimerTask.cancel();
        }
    }
}