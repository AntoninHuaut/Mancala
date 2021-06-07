package fr.antoninhuaut.mancala.save;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class HighscoreManager {

    private static final int RECORD_LIMIT = 100;
    private static HighscoreManager instance;

    private final File highscoreFile;

    public HighscoreManager() {
        this.highscoreFile = new File("highscore.txt");
        if (!highscoreFile.exists()) {
            try {
                highscoreFile.createNewFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public String getHighscoreSerialize() {
        return serializeHighscoreList(getHighscores());
    }

    public String serializeHighscoreList(List<Highscore> highscores) {
        return highscores.stream().map(Highscore::toString).collect(Collectors.joining(System.getProperty("line.separator")));
    }

    public List<Highscore> getHighscores() {
        List<Highscore> lines = new ArrayList<>();
        try {
            List<String> strList = Files.readAllLines(highscoreFile.toPath());
            strList.forEach(str -> lines.add(new Highscore(str)));
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return lines.stream().limit(RECORD_LIMIT).collect(Collectors.toList());
    }

    public void addHighscore(String username, int nbSeed) {
        var toAdd = new Highscore(nbSeed, username);
        try {
            var highscoreList = getHighscores();
            highscoreList.add(0, toAdd);
            highscoreList = highscoreList.stream().limit(RECORD_LIMIT).collect(Collectors.toList());

            var serialize100Max = serializeHighscoreList(highscoreList);
            Files.write(highscoreFile.toPath(), serialize100Max.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.WRITE);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static HighscoreManager getInstance() {
        if (instance == null) instance = new HighscoreManager();
        return instance;
    }

    static class Highscore {

        private final int nbSeed;
        private final String username;

        public Highscore(String strSerialize) {
            String[] parts = strSerialize.split(" ");
            this.nbSeed = Integer.parseInt(parts[0]);
            this.username = parts[1];
        }

        public Highscore(int nbSeed, String username) {
            this.nbSeed = nbSeed;
            this.username = username;
        }

        @Override
        public String toString() {
            return String.format("%d %s", nbSeed, username);
        }

    }
}
