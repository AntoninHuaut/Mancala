package fr.antoninhuaut.mancala.utils.components;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

public class SwitchButton extends StackPane {

    private static final String CLASS_SWITCH = "switch";
    private static final String CLASS_ON = "on";
    private static final String CLASS_OFF = "off";

    private final Rectangle back = new Rectangle(30, 10, Color.RED);
    private final Button button = new Button();
    private final BooleanProperty state = new SimpleBooleanProperty(false);

    public SwitchButton(boolean initState) {
        state.set(initState);

        init();
        EventHandler<Event> click = ignore -> state.set(state.not().get());
        state.addListener((ob, o, n) -> updateVisual());

        button.getStyleClass().add(CLASS_SWITCH);
        button.setFocusTraversable(false);
        setOnMouseClicked(click);
        button.setOnMouseClicked(click);
    }

    private void updateVisual() {
        if (state.get()) {
            button.getStyleClass().remove(CLASS_OFF);
            button.getStyleClass().add(CLASS_ON);
            back.setFill(Color.valueOf("#80C49E"));
            setAlignment(button, Pos.CENTER_RIGHT);
        } else {
            button.getStyleClass().remove(CLASS_ON);
            button.getStyleClass().add(CLASS_OFF);
            back.setFill(Color.valueOf("#ced5da"));
            setAlignment(button, Pos.CENTER_LEFT);
        }
    }

    public BooleanProperty stateProperty() {
        return state;
    }

    private void init() {
        getChildren().addAll(back, button);
        setMinSize(30, 15);
        back.maxWidth(30);
        back.minWidth(30);
        back.maxHeight(10);
        back.minHeight(10);
        back.setArcHeight(back.getHeight());
        back.setArcWidth(back.getHeight());
        var r = 2.0;
        button.setShape(new Circle(r));
        button.setMaxSize(15, 15);
        button.setMinSize(15, 15);
        updateVisual();
    }
}