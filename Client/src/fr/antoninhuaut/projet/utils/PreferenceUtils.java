package fr.antoninhuaut.projet.utils;

import fr.antoninhuaut.projet.AppFX;
import fr.antoninhuaut.projet.model.enums.UserPrefType;

import java.io.File;
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

    public static PreferenceUtils getInstance() {
        if (instance == null) {
            instance = new PreferenceUtils();
        }

        return instance;
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
