package fr.antoninhuaut.mancala.utils.components.dialog;

import fr.antoninhuaut.mancala.AppFX;
import javafx.scene.control.Dialog;
import javafx.scene.image.Image;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

import java.util.Objects;

public class IconDialog<T> extends Dialog<T> {

    public IconDialog() {
        super();

        Image img = AppFX.getInstance().getStage().getIcons().get(0);
        ((Stage) getDialogPane().getScene().getWindow()).getIcons().add(img);
        getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        getDialogPane().getStylesheets().addAll(
                Objects.requireNonNull(getClass().getResource("/views/popup/AlertIcon.css")).toExternalForm(),
                Objects.requireNonNull(getClass().getResource("/views/GlobalButton.css")).toExternalForm()
        );
    }
}