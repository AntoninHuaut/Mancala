package fr.antoninhuaut.mancala.controller.global;

import fr.antoninhuaut.mancala.utils.FadeUtils;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class ToolbarController extends FXController {

    @FXML
    public AnchorPane anchorPane;

    @FXML
    public ImageView reduceImg;
    @FXML
    public ImageView exitImg;

    private double xOffset = 0;
    private double yOffset = 0;
    private Stage stage;

    @Override
    public void postLoad() {
        this.stage = (Stage) this.anchorPane.getScene().getWindow();

        this.anchorPane.setOnMousePressed((event) -> {
            this.xOffset = event.getSceneX();
            this.yOffset = event.getSceneY();
        });

        this.anchorPane.setOnMouseDragged((event) -> {
            this.stage.setX(event.getScreenX() - this.xOffset);
            this.stage.setY(event.getScreenY() - this.yOffset);
        });

        setImgHoverColor(this.reduceImg, YELLOW);
        setImgHoverColor(this.exitImg, Color.RED);
    }

    public void reduceApp() {
        FadeUtils.fade(this.stage, false);
    }

    public void exitApp() {
        System.exit(0);
    }
}