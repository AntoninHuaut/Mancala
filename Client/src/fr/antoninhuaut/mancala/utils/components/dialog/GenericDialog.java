package fr.antoninhuaut.mancala.utils.components.dialog;

import fr.antoninhuaut.mancala.utils.I18NUtils;
import fr.antoninhuaut.mancala.utils.UtilsFX;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;

public class GenericDialog<T> extends IconDialog<T> {

    private final ButtonType submitButtonType;
    private final ButtonType cancelButtonType;

    public GenericDialog(String key) {
        super();

        this.titleProperty().bind(I18NUtils.getInstance().bindStr(key + ".title"));
        this.headerTextProperty().bind(I18NUtils.getInstance().bindStr(key + ".header"));
        UtilsFX.scaleNode(this.getDialogPane());

        this.submitButtonType = new ButtonType(I18NUtils.getInstance().get("common.submit"), ButtonBar.ButtonData.OK_DONE);
        this.cancelButtonType = new ButtonType(I18NUtils.getInstance().get("common.cancel"), ButtonBar.ButtonData.CANCEL_CLOSE);
        getDialogPane().getButtonTypes().addAll(this.submitButtonType, this.cancelButtonType);
    }

    public ButtonType getSubmitButtonType() {
        return this.submitButtonType;
    }

    public ButtonType getCancelButtonType() {
        return this.cancelButtonType;
    }

}
