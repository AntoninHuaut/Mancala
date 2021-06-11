package fr.antoninhuaut.mancala.utils;

import fr.antoninhuaut.mancala.model.enums.UserPrefType;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.util.Objects;

public class AudioManager {

    private final MediaPlayer mediaPlayer;

    public AudioManager() {
        var sound = PreferenceUtils.getInstance().getSettingsPrefs().get(UserPrefType.SOUND);
        var music = PreferenceUtils.getInstance().getSettingsPrefs().get(UserPrefType.MUSIC);

        sound.addListener((ob, o, n) -> {
            if (n) startSound();
            else stopSound();
        });

        music.addListener((ob, o, n) -> {
            if (n) startMusic();
            else stopMusic();
        });

        this.mediaPlayer = new MediaPlayer(new Media(getMusicMediaPath()));
        var soundVolume = 70;
        mediaPlayer.setVolume((float) (1 - (Math.log(100 - soundVolume) / Math.log(100))));
        mediaPlayer.setOnEndOfMedia(() -> mediaPlayer.seek(Duration.ZERO));

        if (music.get()) {
            mediaPlayer.setStartTime(Duration.minutes(1.5));
            mediaPlayer.play();
        }
    }

    private void stopMusic() {
        mediaPlayer.stop();
    }

    private void startMusic() {
        mediaPlayer.play();
    }

    private void stopSound() {
    }

    private void startSound() {
    }

    private String getMusicMediaPath() {
        return Objects.requireNonNull(getClass().getResource("/audio/higher-power_compress.mp3")).toString();
    }
}
