package fr.antoninhuaut.mancala.utils.components.alert;

import fr.antoninhuaut.mancala.AppFX;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

import java.util.Objects;

public class IconAlert extends Alert {

    public IconAlert(AlertType alertType) {
        super(alertType);

        Image img = AppFX.getInstance().getStage().getIcons().get(0);
        ((Stage) getDialogPane().getScene().getWindow()).getIcons().add(img);
        getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        getDialogPane().getStylesheets().add(
                Objects.requireNonNull(getClass().getResource("/views/popup/AlertIcon.css")).toExternalForm()
        );
    }
}