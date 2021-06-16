package fr.antoninhuaut.mancala.model.views.game;

import javafx.beans.binding.StringBinding;
import javafx.beans.property.*;
import javafx.scene.paint.Color;

public class GameData {

    private final BooleanProperty stackPlayerOneVisibility = new SimpleBooleanProperty(false);
    private final BooleanProperty stackPlayerTwoVisibility = new SimpleBooleanProperty(false);
    private final BooleanProperty gameGridVisibility = new SimpleBooleanProperty(false);
    private final BooleanProperty playersNameLabelVisiblity = new SimpleBooleanProperty(false);
    private final BooleanProperty errorLabelVisibility = new SimpleBooleanProperty(false);
    private final BooleanProperty surrenderBtnDisable = new SimpleBooleanProperty(true);
    private final BooleanProperty playWithBotBtnVisibility = new SimpleBooleanProperty(false);

    private final StringProperty sessionIdLabelText = new SimpleStringProperty();
    private final StringProperty playersNameLabelText = new SimpleStringProperty();
    private final StringProperty pOneScoreLabelText = new SimpleStringProperty();
    private final StringProperty pTwoScoreLabelText = new SimpleStringProperty();
    private final StringProperty pOneMatchLabelText = new SimpleStringProperty();
    private final StringProperty pTwoMatchLabelText = new SimpleStringProperty();

    private final DoubleProperty[] bols = new DoubleProperty[16];
    private final CellData[][] cells = new CellData[2][6];

    private final ObjectProperty<Color> infosLabelColor = new SimpleObjectProperty<>();
    private StringBinding infosLabelText, errorLabelText;

    public StringProperty sessionIdLabelTextProperty() {
        return sessionIdLabelText;
    }

    public BooleanProperty surrenderBtnDisableProperty() {
        return surrenderBtnDisable;
    }

    public BooleanProperty playWithBotBtnVisibilityProperty() {
        return playWithBotBtnVisibility;
    }

    public StringProperty pOneMatchLabelTextProperty() {
        return pOneMatchLabelText;
    }

    public StringProperty pTwoMatchLabelTextProperty() {
        return pTwoMatchLabelText;
    }

    public BooleanProperty errorLabelVisibilityProperty() {
        return errorLabelVisibility;
    }

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

    public CellData[][] getCells() {
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

    public StringBinding errorLabelTextProperty() {
        return errorLabelText;
    }

    public void setInfosLabelTextProperty(StringBinding stringBind) {
        this.infosLabelText = stringBind;
    }

    public void setErrorLabelTextProperty(StringBinding stringBind) {
        this.errorLabelText = stringBind;
    }

}
