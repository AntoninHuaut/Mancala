package fr.antoninhuaut.mancala.save;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class SaveManager {

    private static SaveManager instance;
    private static final String EXT = ".json";
    private static final char[] invalidChars = new char[]{' ', '/', '\\', '>', '<', ':', '|', '"', '?', '*'};

    private final Gson gson;
    private final File fileFolder;

    public SaveManager() {
        var gsonBuilder = new GsonBuilder();
        gsonBuilder.setPrettyPrinting();
        this.gson = gsonBuilder.create();

        this.fileFolder = new File("saves");
        if (!fileFolder.exists())
            fileFolder.mkdir();
    }

    public SaveState loadSave(String saveName) throws SaveLoadException {
        var saveFile = getFile(saveName);

        if (!saveFile.exists()) throw new SaveLoadException();

        String json;

        try {
            json = new String(Files.readAllBytes(Paths.get(saveFile.toURI())));
        } catch (IOException e) {
            e.printStackTrace();
            throw new SaveLoadException();
        }

        try {
            return gson.fromJson(json, SaveState.class);
        } catch (JsonSyntaxException ex) {
            ex.printStackTrace();
            throw new SaveLoadException();
        }
    }

    public void saveGame(String saveName, SaveState saveState) throws SaveException {
        var saveFile = getFile(saveName);

        try {
            Files.write(Paths.get(saveFile.toURI()), gson.toJson(saveState).getBytes());
        } catch (IOException ex) {
            ex.printStackTrace();
            throw new SaveException();
        }
    }

    private File getFile(String saveName) {
        return new File(fileFolder + File.separator + convertSaveName(saveName) + EXT);
    }

    private String convertSaveName(String saveName) {
        for (char c : invalidChars) {
            saveName = saveName.replace(c, '_');
        }

        return saveName;
    }

    public static SaveManager getInstance() {
        if (instance == null) instance = new SaveManager();
        return instance;
    }
}
