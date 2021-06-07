package fr.antoninhuaut.mancala.utils.components.dialog;

import fr.antoninhuaut.mancala.utils.I18NUtils;
import javafx.scene.web.WebView;

public class RuleDialog extends GenericDialog<Void> {

    public RuleDialog(String key) {
        super(key);

        var webView = new WebView();
        this.getDialogPane().setContent(webView);
        webView.getEngine().load(getURLByLang());
        getDialogPane().getButtonTypes().remove(getSubmitButtonType());
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