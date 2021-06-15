package fr.antoninhuaut.mancala.utils.components.alert;

import fr.antoninhuaut.mancala.AppFX;
import fr.antoninhuaut.mancala.utils.I18NUtils;
import javafx.application.Platform;

public class RuleAlert extends GenericAlert {

    public RuleAlert(String key) {
        super(AlertType.INFORMATION, key);

        Platform.runLater(() -> AppFX.getInstance().getHostServices().showDocument(getURLByLang()));
    }

    private String getURLByLang() {
        var lang = I18NUtils.SupportLanguage.getLang(I18NUtils.getInstance().getLocale().getLanguage());
        switch (lang) {
            case FR:
                return "https://fr.wikipedia.org/wiki/Awal%C3%A9#Les_r%C3%A8gles";
            default:
                return "https://en.wikipedia.org/wiki/Oware#Rules";
        }
    }
}