module fr.antoninhuaut.projet {
    requires java.prefs;

    requires javafx.controls;
    requires javafx.fxml;
    requires MaterialFX.materialfx.main;

    opens fr.antoninhuaut.projet.controller.socket;

    exports fr.antoninhuaut.projet;
    exports fr.antoninhuaut.projet.controller.global;
    exports fr.antoninhuaut.projet.controller.game;
}