package fr.antoninhuaut.mancala.controller.socket;

import fr.antoninhuaut.mancala.controller.global.FXController;
import fr.antoninhuaut.mancala.model.enums.UserPrefType;
import fr.antoninhuaut.mancala.model.views.socket.SocketConnectionData;
import fr.antoninhuaut.mancala.model.views.socket.SocketConnectionModel;
import fr.antoninhuaut.mancala.utils.PreferenceUtils;
import fr.antoninhuaut.mancala.utils.form.misc.StringNumberConverter;
import fr.antoninhuaut.mancala.utils.form.validator.IntegerTextField;
import fr.antoninhuaut.mancala.utils.form.validator.NoSpaceTextField;
import fr.antoninhuaut.mancala.view.game.GameView;
import fr.antoninhuaut.mancala.view.global.HomeView;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.util.Random;

public class SocketConnectionController extends FXController {

    @FXML
    private ProgressIndicator loadingSpinner;

    @FXML
    private Button connectBtn;

    @FXML
    private TextField usernameField, hostField, portField;

    @FXML
    private Label errorMsg;

    private final HomeView homeView;

    private final SocketConnectionData socketConnectionData = new SocketConnectionData();
    private final SocketConnectionModel socketConnectionModel = new SocketConnectionModel();

    public SocketConnectionController(HomeView homeView) {
        this.homeView = homeView;
    }

    @Override
    public void postLoad() {
        SimpleBooleanProperty loadSpinProp = socketConnectionModel.loadingSpinnerVisibilityProperty();
        loadingSpinner.visibleProperty().bind(loadSpinProp);
        connectBtn.disableProperty().bind(loadSpinProp);
        usernameField.disableProperty().bind(loadSpinProp);
        hostField.disableProperty().bind(loadSpinProp);
        portField.disableProperty().bind(loadSpinProp);

        errorMsg.visibleProperty().bind(socketConnectionModel.errorMsgVisibilityProperty());

        usernameField.textProperty().bindBidirectional(socketConnectionData.usernameProperty());
        usernameField.textProperty().addListener((ev) -> setErrorMsgVisibility(false));
        hostField.textProperty().bindBidirectional(socketConnectionData.hostProperty());
        hostField.textProperty().addListener((ev) -> setErrorMsgVisibility(false));
        portField.textProperty().addListener((ev) -> setErrorMsgVisibility(false));
        Bindings.bindBidirectional(portField.textProperty(), socketConnectionData.portProperty(), new StringNumberConverter());

        new NoSpaceTextField(hostField).apply();
        new NoSpaceTextField(usernameField).apply();
        new IntegerTextField(portField).apply();
    }

    @FXML
    private void tryConnection() {
        if (socketConnectionData.getUsername().isEmpty()) {
            socketConnectionData.usernameProperty().set("Player" + (new Random().nextInt(899) + 100));
        }

        setErrorMsgVisibility(false);
        setLoadingSpinnerVisibility(true);

        PreferenceUtils.getInstance().setPref(UserPrefType.USERNAME, socketConnectionData.getUsername());
        PreferenceUtils.getInstance().setPref(UserPrefType.SOCKET_HOST, socketConnectionData.getHost());
        PreferenceUtils.getInstance().setPref(UserPrefType.SOCKET_PORT, "" + socketConnectionData.getPort());

        new Thread(() -> {
            try {
                var mancalaSocket = new MancalaSocket(socketConnectionData, homeView);
                new GameView(mancalaSocket).load();
            } catch (IOException ex) {
                setErrorMsgVisibility(true);
                setLoadingSpinnerVisibility(false);
            }
        }).start();
    }

    private void setErrorMsgVisibility(boolean state) {
        socketConnectionModel.setErrorMsgVisibility(state);
    }

    private void setLoadingSpinnerVisibility(boolean state) {
        socketConnectionModel.setLoadingSpinnerVisibility(state);
    }
}
