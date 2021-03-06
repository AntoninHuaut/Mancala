package fr.antoninhuaut.mancala.utils;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.stage.Stage;

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class UtilsFX {

    private static final double FONT_SCALING = 120.0;

    public static void scaleNode(Node node) {
        node.setStyle(String.format(Locale.US, "-fx-font-size: %f%%", FONT_SCALING)); // US for dot for double value
    }

    private static final int TIME = 200;
    private static final int MILLIS_PER_FRAME = 5;
    private static final float DELTA = MILLIS_PER_FRAME / (float) TIME;

    public static void fade(Stage stage, boolean show) {
        stage.setOpacity(show ? 0f : 1f);

        if (show) {
            stage.setIconified(false);
        }

        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            final float delta = show ? DELTA : -DELTA;
            float opacity = show ? 0f : 1f;

            @Override
            public void run() {
                Platform.runLater(() -> {
                    this.opacity += this.delta;
                    if (this.opacity < 0) {
                        stage.setIconified(true);
                        stage.setOpacity(1f);
                        timer.cancel();
                    } else if (this.opacity > 1) {
                        stage.setOpacity(1f);
                        timer.cancel();
                    } else {
                        stage.setOpacity(this.opacity);
                    }
                });
            }
        };
        timer.scheduleAtFixedRate(timerTask, MILLIS_PER_FRAME, MILLIS_PER_FRAME);
    }
}
