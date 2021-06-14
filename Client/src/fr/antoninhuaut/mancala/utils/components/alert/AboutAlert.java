package fr.antoninhuaut.mancala.utils.components.alert;

import fr.antoninhuaut.mancala.AppFX;
import fr.antoninhuaut.mancala.utils.I18NUtils;
import fr.antoninhuaut.mancala.utils.UtilsFX;
import javafx.scene.control.ButtonType;

import java.util.Optional;

public class AboutAlert extends IconAlert {

    public AboutAlert() {
        super(AlertType.INFORMATION);

        setHeaderText(null);
        titleProperty().bind(I18NUtils.getInstance().bindStr("alert.about"));
        contentTextProperty().bind(I18NUtils.getInstance().bindStr("alert.about.content"));

        var link = I18NUtils.getInstance().get("alert.about.btnlink.link");
        var website = new ButtonType(I18NUtils.getInstance().get("alert.about.btnlink.content"));

        getButtonTypes().add(website);

        UtilsFX.scaleNode(getDialogPane());

        Optional<ButtonType> result = showAndWait();
        if (result.isEmpty()) return;

        if (result.get() == website) {
            new Thread(() -> AppFX.getInstance().getHostServices().showDocument(link)).start();
        }
    }
}