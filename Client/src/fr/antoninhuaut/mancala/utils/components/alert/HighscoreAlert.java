package fr.antoninhuaut.mancala.utils.components.alert;

import fr.antoninhuaut.mancala.utils.I18NUtils;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

public class HighscoreAlert extends GenericAlert {

    private static final String KEY = "home.highscore";

    public HighscoreAlert(String highscore) {
        super(AlertType.INFORMATION, KEY);
        highscore = formatHighscore(highscore);

        var textArea = new TextArea(highscore.isEmpty() ? "NA" : highscore);
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setMaxWidth(333);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        var content = new Label();
        content.textProperty().bind(I18NUtils.getInstance().bindStr(KEY + ".content"));

        var gridPane = new GridPane();
        gridPane.setMaxWidth(Double.MAX_VALUE);
        gridPane.add(content, 0, 0);
        gridPane.add(textArea, 0, 1);

        this.getDialogPane().setContent(gridPane);
    }

    private String formatHighscore(String highscore) {
        var listHS = Arrays.stream(highscore.split(System.getProperty("line.separator"))).map(el -> {
            try {
                return new Highscore(el);
            } catch (IllegalStateException ignore) {
                return null;
            }
        }).filter(Objects::nonNull).collect(Collectors.toList());
        var listStr = listHS.stream().map(high -> I18NUtils.getInstance().get("home.highscore.format", high.getUsername(), high.getNbSeed())).collect(Collectors.toList());
        return listStr.stream().collect(Collectors.joining(System.getProperty("line.separator")));
    }

    static class Highscore {

        private final int nbSeed;
        private final String username;

        public Highscore(String strSerialize) throws IllegalStateException {
            String[] parts = strSerialize.split(" ");

            try {
                this.nbSeed = Integer.parseInt(parts[0]);
            } catch (NumberFormatException ignore) {
                throw new IllegalStateException();
            }
            try {
                this.username = parts[1];
            } catch (ArrayIndexOutOfBoundsException ignore) {
                throw new IllegalStateException();
            }
        }

        public int getNbSeed() {
            return nbSeed;
        }

        public String getUsername() {
            return username;
        }

        @Override
        public String toString() {
            return String.format("%d %s", nbSeed, username);
        }

    }
}
