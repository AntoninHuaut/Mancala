package fr.antoninhuaut.mancala.utils;

import fr.antoninhuaut.mancala.model.enums.ClientToServerEnum;
import fr.antoninhuaut.mancala.model.enums.ServerToClientEnum;
import fr.antoninhuaut.mancala.model.enums.UserPrefType;
import javafx.beans.property.BooleanProperty;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Objects;

public class AudioManager {

    private static final Logger LOGGER = LogManager.getLogger();

    private static AudioManager instance;

    private final MediaPlayer musicPlayer;
    private MediaPlayer soundPlayer;
    private final BooleanProperty sound;

    private AudioManager() {
        this.musicPlayer = new MediaPlayer(new Media(getAudioPath("music.mp3")));
        musicPlayer.setVolume(getVolume(60));
        musicPlayer.setOnEndOfMedia(() -> musicPlayer.seek(Duration.ZERO));

        this.sound = PreferenceUtils.getInstance().getSettingsPrefs().get(UserPrefType.SOUND);
        var music = PreferenceUtils.getInstance().getSettingsPrefs().get(UserPrefType.MUSIC);

        if (music.get()) {
            musicPlayer.play();
        }

        music.addListener((ob, o, n) -> {
            if (n) startMusic();
            else stopMusic();
        });
    }

    private void stopMusic() {
        musicPlayer.stop();
    }

    private void startMusic() {
        musicPlayer.play();
    }

    public void playCTOS_Sound(ClientToServerEnum cTOsEnum) {
        try {
            switch (cTOsEnum) {
                case MOVE:
                    playSound(cTOsEnum.name().toLowerCase());
                    break;
                default:
                    break;
            }
        } catch (Exception ignore) {
        }
    }

    public void playSTOC_Sound(ServerToClientEnum sTOcEnum) {
        try {
            switch (sTOcEnum) {
                case WELCOME:
                case MESSAGE:
                case END_ROUND:
                case END_GAME:
                    playSound(sTOcEnum.name().toLowerCase());
                    break;
                default:
                    break;
            }
        } catch (Exception ignore) {
        }
    }

    private void playSound(String soundPath) {
        if (!sound.get()) return;
        if (soundPlayer != null) soundPlayer.stop();

        LOGGER.debug("Playing sound effect: {}", soundPath);
        this.soundPlayer = new MediaPlayer(new Media(getAudioPath("sound_" + soundPath + ".mp3")));
        soundPlayer.setVolume(getVolume(75));
        soundPlayer.play();
    }

    private double getVolume(double volumeWant) {
        return (1 - (Math.log(100 - volumeWant) / Math.log(100)));
    }

    private String getAudioPath(String path) {
        return Objects.requireNonNull(getClass().getResource("/audio/" + path)).toString();
    }

    public static AudioManager getInstance() {
        if (instance == null) instance = new AudioManager();
        return instance;
    }
}
