package fr.antoninhuaut.mancala.utils.components.alert;

import fr.antoninhuaut.mancala.utils.I18NUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;

import java.util.Arrays;
import java.util.function.Consumer;

public class BotStrategyAlert extends GenericAlert {

    public BotStrategyAlert(String key, Consumer<String> run) {
        super(AlertType.CONFIRMATION, key);

        var randomBtnType = new ButtonType(I18NUtils.getInstance().get(key + ".random"), ButtonBar.ButtonData.OK_DONE);
        var mostSeedBtnType = new ButtonType(I18NUtils.getInstance().get(key + ".mostseed"), ButtonBar.ButtonData.OK_DONE);

        this.getButtonTypes().setAll(randomBtnType, mostSeedBtnType);

        for (var btnType : Arrays.asList(randomBtnType, mostSeedBtnType)) {
            getDialogPane().lookupButton(btnType).getStyleClass().add("default");
        }

        setActionBtn(randomBtnType, () -> run.accept("RANDOM"));
        setActionBtn(mostSeedBtnType, () -> run.accept("mostseedcell"));
    }

    private void setActionBtn(ButtonType btnType, Runnable btnRun) {
        ((Button) this.getDialogPane().lookupButton(btnType)).setOnAction(getHandlerActionBtn(btnRun));
    }

    private EventHandler<ActionEvent> getHandlerActionBtn(Runnable btnRun) {
        return event -> btnRun.run();
    }
}
