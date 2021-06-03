package fr.antoninhuaut.mancala;

import fr.antoninhuaut.mancala.controller.global.WindowController;
import fr.antoninhuaut.mancala.utils.FadeUtils;
import fr.antoninhuaut.mancala.utils.I18NUtils;
import fr.antoninhuaut.mancala.view.global.WindowView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.Objects;

public class AppFX extends Application {

    private static AppFX instance;

    private Stage stage;

    public static void main(String[] args) {
        launch(args);
    }

    public static synchronized AppFX getInstance() {
        return instance;
    }

    private static synchronized void setInstance(AppFX instance) {
        AppFX.instance = instance;
    }

    @Override
    public void start(Stage primaryStage) {
        setInstance(this);

        var pane = new BorderPane();

        primaryStage.setScene(new Scene(pane));
        new WindowView(pane).load();

        primaryStage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/img/logo.png"))));
        primaryStage.titleProperty().bind(I18NUtils.getInstance().bindStr("window.title"));
        primaryStage.initStyle(StageStyle.UNDECORATED);

        primaryStage.sizeToScene();
        primaryStage.show();
        primaryStage.setMinWidth(primaryStage.getWidth());
        primaryStage.setMinHeight(primaryStage.getHeight());

        this.stage = primaryStage;
        this.stage.iconifiedProperty().addListener((ob, o, n) -> {
            if (!n) {
                FadeUtils.fade(this.stage, true);
            }
        });
    }

    public Stage getStage() {
        return this.stage;
    }
}