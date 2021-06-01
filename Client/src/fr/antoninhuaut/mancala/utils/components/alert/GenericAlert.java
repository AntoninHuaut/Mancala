package fr.antoninhuaut.mancala.utils.components.alert;

import fr.antoninhuaut.mancala.utils.I18NUtils;
import fr.antoninhuaut.mancala.utils.UtilsFX;

public class GenericAlert extends IconAlert {

    public GenericAlert(AlertType alertType, String key, String... contentArgs) {
        super(alertType);

        this.titleProperty().bind(I18NUtils.getInstance().bindStr(key + ".title"));
        this.setHeaderText(null);
        this.contentTextProperty().bind(I18NUtils.getInstance().bindStr(key + ".content", (Object[]) contentArgs));
        UtilsFX.scaleNode(this.getDialogPane());
    }

}
