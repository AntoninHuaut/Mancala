package fr.antoninhuaut.mancala.model.views.game;

import javafx.beans.binding.StringBinding;
import javafx.beans.property.*;
import javafx.scene.paint.Color;

public class GameData {

    private final BooleanProperty stackPlayerOneVisibility = new SimpleBooleanProperty(false);
    private final BooleanProperty stackPlayerTwoVisibility = new SimpleBooleanProperty(false);
    private final BooleanProperty gameGridVisibility = new SimpleBooleanProperty(false);
    private final BooleanProperty playersNameLabelVisiblity = new SimpleBooleanProperty(false);

    private final StringProperty playersNameLabelText = new SimpleStringProperty();
    private final StringProperty pOneScoreLabelText = new SimpleStringProperty();
    private final StringProperty pTwoScoreLabelText = new SimpleStringProperty();

    private final DoubleProperty[] bols = new DoubleProperty[16];
    private final StringProperty[][] cells = new StringProperty[2][6];

    private final ObjectProperty<Color> infosLabelColor = new SimpleObjectProperty<>();
    private StringBinding infosLabelText;

    public BooleanProperty stackPlayerOneVisibilityProperty() {
        return stackPlayerOneVisibility;
    }

    public BooleanProperty stackPlayerTwoVisibilityProperty() {
        return stackPlayerTwoVisibility;
    }

    public BooleanProperty gameGridVisibilityProperty() {
        return gameGridVisibility;
    }


    public BooleanProperty playersNameLabelVisiblityProperty() {
        return playersNameLabelVisiblity;
    }


    public StringProperty playersNameLabelTextProperty() {
        return playersNameLabelText;
    }


    public StringProperty pOneScoreLabelTextProperty() {
        return pOneScoreLabelText;
    }


    public StringProperty pTwoScoreLabelTextProperty() {
        return pTwoScoreLabelText;
    }

    public StringProperty[][] getCells() {
        return cells;
    }

    public DoubleProperty[] getBols() {
        return bols;
    }

    public ObjectProperty<Color> infosLabelColorProperty() {
        return infosLabelColor;
    }

    public StringBinding infosLabelTextProperty() {
        return infosLabelText;
    }

    public void setInfosLabelTextProperty(StringBinding stringBind) {
        this.infosLabelText = stringBind;
    }

}
