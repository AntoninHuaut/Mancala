package fr.antoninhuaut.mancala.model.views.game;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.Objects;

public class CellData {

    private ImageView imageView;
    private StringProperty seedProperty = new SimpleStringProperty();
    private int nbSeed;

    public CellData setImageView(ImageView imageView) {
        this.imageView = imageView;
        return this;
    }

    public StringProperty seedProperty() {
        return seedProperty;
    }

    public void setNbSeed(int nbSeed) {
        this.nbSeed = nbSeed;
        apply();
    }

    public void apply() {
        seedProperty.set("" + nbSeed);
        var seedImage = Math.min(nbSeed, 20);
        imageView.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/img/seed/cell_" + seedImage + ".png"))));
    }
}
