package fr.antoninhuaut.mancala.utils;

import fr.antoninhuaut.mancala.model.enums.ClientToServerEnum;
import fr.antoninhuaut.mancala.model.enums.ServerToClientEnum;
import fr.antoninhuaut.mancala.model.enums.UserPrefType;
import javafx.beans.property.BooleanProperty;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Objects;
import java.util.Random;

public class AudioManager {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final int nbAvailableMusic = 3;
    private static final long noActionDelayMs = 300;
    private static AudioManager instance;

    private MediaPlayer soundPlayer;
    private final BooleanProperty sound;

    private MediaPlayer musicPlayer;
    private long lastAction = 0;
    private int currentMusic;

    private AudioManager() {
        this.sound = PreferenceUtils.getInstance().getSettingsPrefs().get(UserPrefType.SOUND);
        this.currentMusic = new Random().nextInt(nbAvailableMusic) + 1;
        LOGGER.debug("currentMusic: {}", currentMusic);
        startMusic();
    }

    private void stopMusic() {
        if (musicPlayer == null) return;

        LOGGER.debug("stopMusic");
        musicPlayer.stop();
    }

    private void startMusic() {
        var music = PreferenceUtils.getInstance().getSettingsPrefs().get(UserPrefType.MUSIC);
        music.addListener((ob, o, n) -> {
            if (lastAction + noActionDelayMs > System.currentTimeMillis()) return; // Fix the double call of the javafx observer
            lastAction = System.currentTimeMillis();

            if (n) startMusic();
            else stopMusic();
        });

        if (!music.get()) return;
        stopMusic();

        var musicPath = String.format("music%d.mp3", currentMusic);
        LOGGER.debug("startMusic: {}", musicPath);

        musicPlayer = new MediaPlayer(new Media(getAudioPath(musicPath)));
        musicPlayer.setVolume(getVolume(60));
        musicPlayer.setOnEndOfMedia(this::startMusic);

        currentMusic = currentMusic + 1 > nbAvailableMusic ? 1 : currentMusic + 1;

        if (music.get()) {
            musicPlayer.play();
        }
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
