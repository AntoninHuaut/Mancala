package fr.antoninhuaut.mancala.utils;

import fr.antoninhuaut.mancala.AppFX;
import fr.antoninhuaut.mancala.model.enums.UserPrefType;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import java.util.*;
import java.util.prefs.Preferences;

import static fr.antoninhuaut.mancala.model.enums.UserPrefType.*;

public class PreferenceUtils {

    private static PreferenceUtils instance;

    private final Preferences prefs = Preferences.userNodeForPackage(AppFX.class);
    private final Map<UserPrefType, BooleanProperty> settingsPrefsMap = new HashMap<>();
    private final List<UserPrefType> settingPrefsList = Arrays.asList(SEE_SEED_HOVER, SEE_SEED, SOUND, MUSIC);

    public PreferenceUtils() {
        for (UserPrefType prefType : settingPrefsList) {
            settingsPrefsMap.put(prefType, new SimpleBooleanProperty(getBoolean(prefType)));
        }
    }

    public void setPref(UserPrefType userPref, String str) {
        this.prefs.put(userPref.name(), str);

        if (settingPrefsList.contains(userPref)) {
            settingsPrefsMap.get(userPref).set(getBoolean(userPref));
        }
    }

    public String getPref(UserPrefType userPref, String defaultValue) {
        return this.prefs.get(userPref.name(), defaultValue);
    }

    public Locale getLocalePref() {
        String localePref = getPref(LOCALE_PREF, "EN");
        if (localePref == null) {
            return Locale.forLanguageTag(Locale.getDefault().getLanguage()); // Allows to have xy_*
        } else {
            return Locale.forLanguageTag(localePref);
        }
    }

    public Map<UserPrefType, BooleanProperty> getSettingsPrefs() {
        return settingsPrefsMap;
    }

    public static PreferenceUtils getInstance() {
        if (instance == null) {
            instance = new PreferenceUtils();
        }

        return instance;
    }

    private boolean getBoolean(UserPrefType pref) {
        return Boolean.parseBoolean(getPref(pref, "false"));
    }

    public String getUsername() {
        return getPref(USERNAME, "Player007");
    }

    public String getSocketHost() {
        return getPref(SOCKET_HOST, "localhost");
    }

    public int getSocketPort() {
        try {
            return Integer.parseInt(getPref(SOCKET_PORT, null));
        } catch (NumberFormatException ex) {
            return 3010;
        }
    }
}
