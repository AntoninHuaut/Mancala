package fr.antoninhuaut.mancala.utils;

import javafx.scene.Node;

import java.util.Locale;

public class UtilsFX {

    private static final double FONT_SCALING = 120.0;

    public static void scaleNode(Node node) {
        node.setStyle(String.format(Locale.US, "-fx-font-size: %f%%", FONT_SCALING)); // US for dot for double value
    }
}
