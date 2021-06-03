package fr.antoninhuaut.mancala.controller.global;

import javafx.beans.binding.Bindings;
import javafx.scene.effect.Effect;
import javafx.scene.effect.Light;
import javafx.scene.effect.Lighting;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;

public abstract class FXController {

    public static final Color YELLOW = Color.web("#FFD200");

    public abstract void postLoad();

    public void setImgHoverColor(ImageView imgView, Color color) {
        var lighting = new Lighting();
        lighting.setDiffuseConstant(1.0);
        lighting.setSpecularConstant(0.0);
        lighting.setSpecularExponent(0.0);
        lighting.setSurfaceScale(0.0);
        lighting.setLight(new Light.Distant(100, 100, color));

        imgView.effectProperty().bind(Bindings.when(imgView.hoverProperty())
                .then((Effect) lighting)
                .otherwise((Effect) null)
        );
    }

    public void unload() {}
}
