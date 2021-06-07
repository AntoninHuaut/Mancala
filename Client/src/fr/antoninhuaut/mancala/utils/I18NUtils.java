package fr.antoninhuaut.mancala.utils;

import fr.antoninhuaut.mancala.model.enums.UserPrefType;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public final class I18NUtils {

    private static I18NUtils instance;
    private final List<Locale> supportedLocales = new ArrayList<>();
    private final ObjectProperty<Locale> locale;

    private I18NUtils() {
        for (SupportLanguage lang : SupportLanguage.values()) {
            this.supportedLocales.add(Locale.forLanguageTag(lang.toString().toLowerCase()));
        }

        this.locale = new SimpleObjectProperty<>(getDefaultLocale());
        this.locale.addListener((observable, oldValue, newValue) -> Locale.setDefault(newValue));
    }

    public static I18NUtils getInstance() {
        if (instance == null) {
            instance = new I18NUtils();
        }

        return instance;
    }

    public List<Locale> getSupportedLocales() {
        return this.supportedLocales;
    }

    public Locale getDefaultLocale() {
        var defaultLocale = PreferenceUtils.getInstance().getLocalePref();
        return getSupportedLocales().contains(defaultLocale) ? defaultLocale : this.supportedLocales.get(0);
    }

    public Locale getLocale() {
        return this.locale.get();
    }

    public void setLocale(Locale locale) {
        localeProperty().set(locale);
        Locale.setDefault(locale);
        PreferenceUtils.getInstance().setPref(UserPrefType.LOCALE_PREF, locale.getLanguage());
    }

    public ObjectProperty<Locale> localeProperty() {
        return this.locale;
    }

    public String get(final String key, final Object... args) {
        return MessageFormat.format(getResourceBundle().getString(key), args);
    }

    public boolean has(final String key) {
        return getResourceBundle().containsKey(key);
    }

    public ResourceBundle getResourceBundle() {
        return ResourceBundle.getBundle("i18n/messages", getLocale());
    }

    public StringBinding bindStr(final String key, Object... args) {
        return Bindings.createStringBinding(() -> get(key, args), this.locale);
    }

    public enum SupportLanguage {
        EN,
        FR;

        public static SupportLanguage getLang(String lang) {
            try {
                return SupportLanguage.valueOf(lang.toUpperCase());
            } catch(IllegalArgumentException ignore) {
                return SupportLanguage.EN;
            }
        }
    }
}