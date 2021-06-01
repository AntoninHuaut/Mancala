package fr.antoninhuaut.mancala.controller.game;

import fr.antoninhuaut.mancala.controller.global.FXController;
import fr.antoninhuaut.mancala.controller.socket.MancalaSocket;
import fr.antoninhuaut.mancala.model.Cell;
import fr.antoninhuaut.mancala.utils.I18NUtils;
import fr.antoninhuaut.mancala.view.global.HomeView;
import fr.antoninhuaut.mancala.view.socket.SocketConnectionView;
import javafx.beans.binding.StringBinding;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;

public class GameController extends FXController {

    private final HomeView homeView;
    private final MancalaSocket mancalaSocket;

    @FXML
    public Label infosLabel, pPosLabel;

    @FXML
    public Label pOneScoreLabel, pTwoScoreLabel, playersNameLabel, redMatchLabel, blueMatchLabel;

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
        // TODO game pas lancée (crée nouv msg) ou pas au tour du joueur

        StackPane stackPane = (StackPane) event.getSource();
        Ellipse ellipse = (Ellipse) stackPane.getChildren().get(0);
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

    public void updateCells_Score(Cell[][] cells, int pOneScore, int pTwoScore) {
        Scene scene = infosLabel.getScene();
        System.out.println("Updating cells & score");

        for (int line = 0; line < cells.length; ++line) {
            for (int col = 0; col < cells[line].length; ++col) {
                Cell cell = cells[line][col];
                Label cellLabel = (Label) scene.lookup(String.format("#cell-%d-%d", line, col));
                cellLabel.setText("" + cell.getNbSeed());
            }
        }

        pOneScoreLabel.setText("" + pOneScore);
        pTwoScoreLabel.setText("" + pTwoScore);
    }

    public void initWelcome(String playerNumberResponse) {
        boolean isRedPlayer = playerNumberResponse.equalsIgnoreCase("player_one");
        StringBinding i18n;
        if (isRedPlayer) {
            i18n = I18NUtils.getInstance().bindStr("game.info.player_one");
            pPosLabel.setTextFill(Color.web("#FF4D1F"));
        } else {
            i18n = I18NUtils.getInstance().bindStr("game.info.player_two");
            pPosLabel.setTextFill(Color.web("#1E90FF"));
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

    public void switchTurn() {
        this.isYourTurn = !isYourTurn;
        setTurnLabel();
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