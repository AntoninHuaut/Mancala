package fr.antoninhuaut.mancala.controller.game;

import fr.antoninhuaut.mancala.controller.global.FXController;
import fr.antoninhuaut.mancala.controller.socket.MancalaSocket;
import fr.antoninhuaut.mancala.model.Cell;
import fr.antoninhuaut.mancala.utils.I18NUtils;
import fr.antoninhuaut.mancala.view.global.HomeView;
import fr.antoninhuaut.mancala.view.socket.SocketConnectionView;
import javafx.beans.binding.StringBinding;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GameController extends FXController {

//    private static final Logger LOGGER = LoggerFactory.getLogger(GameController.class);

    private final HomeView homeView;
    private final MancalaSocket mancalaSocket;

    @FXML
    public Label infosLabel, pPosLabel;

    @FXML
    public Label pOneScoreLabel, pTwoScoreLabel, playersNameLabel, redMatchLabel, blueMatchLabel;

    private int myPlayerId;
    private boolean isYourTurn = false;

    public GameController(MancalaSocket mancalaSocket) {
        this.homeView = mancalaSocket.getHomeView();
        this.mancalaSocket = mancalaSocket;
    }

    @Override
    public void postLoad() {
        playersNameLabel.setVisible(false);
        redMatchLabel.setVisible(false);
        blueMatchLabel.setVisible(false);

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
    public void ellipseClick(MouseEvent event) {
        if (!(event.getSource() instanceof StackPane)) return;

        var stackPane = (StackPane) event.getSource();
        var ellipse = (Ellipse) stackPane.getChildren().get(0);

        String[] idParts = ellipse.getId().split("-");
        int line, col;
        try {
            line = Integer.parseInt(idParts[1]);
            col = Integer.parseInt(idParts[2]);
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
        boolean isRedPlayer = playerNumberResponse.equalsIgnoreCase("player_one");
        StringBinding i18n;
        if (isRedPlayer) {
            i18n = I18NUtils.getInstance().bindStr("game.info.player_one");
            pPosLabel.setTextFill(Color.web("#FF4D1F"));
            this.myPlayerId = 0;
        } else {
            i18n = I18NUtils.getInstance().bindStr("game.info.player_two");
            pPosLabel.setTextFill(Color.web("#1E90FF"));
            this.myPlayerId = 1;
        }

        pPosLabel.textProperty().bind(i18n);
    }

    public void initPostPlayerJoin(boolean isYourTurn) {
        this.isYourTurn = isYourTurn;
        setTurnLabel();

        redMatchLabel.setVisible(true);
        blueMatchLabel.setVisible(true);
    }

    public void setTurnLabel() {
        if (isYourTurn) {
            setInfosLabelI18N("game.info.turn_you");
            infosLabel.setTextFill(Color.web("#0dee36"));
        } else {
            setInfosLabelI18N("game.info.turn_opponent");
            infosLabel.setTextFill(Color.web("#ee4e0d"));
        }
    }

    public void setPlayersName(String opponentName) {
        playersNameLabel.setVisible(true);
        playersNameLabel.setText(mancalaSocket.getUsername() + " VS " + opponentName);
    }

    public void setInfosLabelI18N(String i18nKey) {
        if (infosLabel.textProperty().isBound()) infosLabel.textProperty().unbind();
        infosLabel.textProperty().bind(I18NUtils.getInstance().bindStr(i18nKey));
    }
}