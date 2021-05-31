package fr.antoninhuaut.projet.controller.socket;

import fr.antoninhuaut.projet.controller.global.FXController;
import fr.antoninhuaut.projet.model.enums.UserPrefType;
import fr.antoninhuaut.projet.model.views.socket.SocketConnectionData;
import fr.antoninhuaut.projet.model.views.socket.SocketConnectionModel;
import fr.antoninhuaut.projet.utils.PreferenceUtils;
import fr.antoninhuaut.projet.utils.form.validator.IntegerTextField;
import fr.antoninhuaut.projet.utils.form.misc.StringNumberConverter;
import fr.antoninhuaut.projet.utils.form.validator.NoSpaceTextField;
import fr.antoninhuaut.projet.view.game.GameView;
import fr.antoninhuaut.projet.view.global.HomeView;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXProgressSpinner;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;

public class SocketConnectionController extends FXController {

    @FXML
    private MFXProgressSpinner loadingSpinner;

    @FXML
    private MFXButton connectBtn;

    @FXML
    private MFXTextField hostField;
    @FXML
    private MFXTextField portField;

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
        hostField.disableProperty().bind(loadSpinProp);
        portField.disableProperty().bind(loadSpinProp);

        errorMsg.visibleProperty().bind(socketConnectionModel.errorMsgVisibilityProperty());

        hostField.textProperty().bindBidirectional(socketConnectionData.hostProperty());
        hostField.textProperty().addListener((ev) -> setErrorMsgVisibility(false));
        portField.textProperty().addListener((ev) -> setErrorMsgVisibility(false));
        Bindings.bindBidirectional(portField.textProperty(), socketConnectionData.portProperty(), new StringNumberConverter());
        new NoSpaceTextField(hostField).apply();
        new IntegerTextField(portField).apply();
    }

    @FXML
    private void tryConnection() {
        setErrorMsgVisibility(false);
        setLoadingSpinnerVisibility(true);

        PreferenceUtils.getInstance().setPref(UserPrefType.SOCKET_HOST, socketConnectionData.getHost());
        PreferenceUtils.getInstance().setPref(UserPrefType.SOCKET_PORT, "" + socketConnectionData.getPort());

        new Thread(() -> {
            try {
                MancalaSocket mancalaSocket = new MancalaSocket(socketConnectionData, homeView);
                new GameView(mancalaSocket).load();
            } catch (Exception ex) {
                ex.printStackTrace();
                callback(true);
            }
        }).start();
    }
    //

    private void callback(boolean hasError) {
        setErrorMsgVisibility(hasError);
        setLoadingSpinnerVisibility(false);
    }

    private void setErrorMsgVisibility(boolean state) {
        socketConnectionModel.setErrorMsgVisibility(state);
    }

    private void setLoadingSpinnerVisibility(boolean state) {
        socketConnectionModel.setLoadingSpinnerVisibility(state);
    }
}
