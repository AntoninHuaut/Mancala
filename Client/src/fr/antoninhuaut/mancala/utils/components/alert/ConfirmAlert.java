package fr.antoninhuaut.mancala.utils.components.alert;

import fr.antoninhuaut.mancala.utils.I18NUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;

public class ConfirmAlert extends GenericAlert {

    public ConfirmAlert(String key, Runnable okRun, Runnable noRun) {
        super(AlertType.CONFIRMATION, key);

        var yesBtnType = new ButtonType(I18NUtils.getInstance().get("common.yes"), ButtonBar.ButtonData.YES);
        var noBtnType = new ButtonType(I18NUtils.getInstance().get("common.no"), ButtonBar.ButtonData.NO);

        this.getButtonTypes().setAll(yesBtnType, noBtnType);

        getDialogPane().lookupButton(yesBtnType).getStyleClass().add("yesBtn");
        getDialogPane().lookupButton(noBtnType).getStyleClass().add("noBtn");

        setActionBtn(yesBtnType, okRun);
        setActionBtn(noBtnType, noRun);
    }

    public ConfirmAlert(String key, Runnable okRun) {
        this(key, okRun, () -> {
        });
    }

    private void setActionBtn(ButtonType btnType, Runnable btnRun) {
        ((Button) this.getDialogPane().lookupButton(btnType)).setOnAction(getHandlerActionBtn(btnRun));
    }

    private EventHandler<ActionEvent> getHandlerActionBtn(Runnable btnRun) {
        return event -> btnRun.run();
    }
}
