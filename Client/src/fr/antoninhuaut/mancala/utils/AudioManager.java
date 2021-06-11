package fr.antoninhuaut.mancala.utils;

import fr.antoninhuaut.mancala.model.enums.ServerToClientEnum;
import fr.antoninhuaut.mancala.model.enums.UserPrefType;
import javafx.beans.property.BooleanProperty;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.util.Objects;

public class AudioManager {

    private static AudioManager instance;

    private final MediaPlayer mediaPlayer;
    private final BooleanProperty sound;

    private AudioManager() {
        this.mediaPlayer = new MediaPlayer(new Media(getAudioPath("music.mp3")));
        mediaPlayer.setVolume(getVolume(60));
        mediaPlayer.setOnEndOfMedia(() -> mediaPlayer.seek(Duration.ZERO));

        this.sound = PreferenceUtils.getInstance().getSettingsPrefs().get(UserPrefType.SOUND);
        var music = PreferenceUtils.getInstance().getSettingsPrefs().get(UserPrefType.MUSIC);

        if (music.get()) {
            mediaPlayer.play();
        }

        music.addListener((ob, o, n) -> {
            if (n) startMusic();
            else stopMusic();
        });
    }

    private void stopMusic() {
        mediaPlayer.stop();
    }

    private void startMusic() {
        mediaPlayer.play();
    }

    public void playSound(ServerToClientEnum sTOcEnum) {
        if (!sound.get()) return;

        try {
            switch (sTOcEnum) {
                case WELCOME:
                case MESSAGE:
                case END_ROUND:
                case END_GAME:
                    var soundPlayer = new MediaPlayer(new Media(getAudioPath("sound_" + sTOcEnum.name().toLowerCase() + ".mp3")));
                    soundPlayer.setVolume(getVolume(75));
                    soundPlayer.play();
                    break;
                default:
                    break;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
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
