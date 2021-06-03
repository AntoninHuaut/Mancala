package fr.antoninhuaut.mancala.utils;

import fr.antoninhuaut.mancala.AppFX;
import fr.antoninhuaut.mancala.model.enums.UserPrefType;

import java.util.Locale;
import java.util.prefs.Preferences;

public class PreferenceUtils {

    private static PreferenceUtils instance;

    private final Preferences prefs = Preferences.userNodeForPackage(AppFX.class);

    public void setPref(UserPrefType userPref, String str) {
        this.prefs.put(userPref.name(), str);
    }

    public String getPref(UserPrefType userPref, String defaultValue) {
        return this.prefs.get(userPref.name(), defaultValue);
    }

    public Locale getLocalePref() {
        String localePref = getPref(UserPrefType.LOCALE_PREF, "EN");
        if (localePref == null) {
            return Locale.forLanguageTag(Locale.getDefault().getLanguage()); // Allows to have xy_*
        } else {
            return Locale.forLanguageTag(localePref);
        }
    }

    public static PreferenceUtils getInstance() {
        if (instance == null) {
            instance = new PreferenceUtils();
        }

        return instance;
    }

    public String getUsername() {
        return getPref(UserPrefType.USERNAME, "Player007");
    }

    public String getSocketHost() {
        return getPref(UserPrefType.SOCKET_HOST, "localhost");
    }

    public int getSocketPort() {
        try {
            return Integer.parseInt(getPref(UserPrefType.SOCKET_PORT, null));
        } catch (NumberFormatException ex) {
            return 3010;
        }
    }
}
