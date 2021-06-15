module fr.antoninhuaut.projet {
    requires java.prefs;

    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;
    requires org.apache.logging.log4j;

    opens fr.antoninhuaut.mancala.controller.socket;

    exports fr.antoninhuaut.mancala.controller.global;
    exports fr.antoninhuaut.mancala.controller.game;
    exports fr.antoninhuaut.mancala.controller.socket;
    exports fr.antoninhuaut.mancala.model;
    exports fr.antoninhuaut.mancala.model.enums;
    exports fr.antoninhuaut.mancala.model.views.socket;
    exports fr.antoninhuaut.mancala.view.global;
    exports fr.antoninhuaut.mancala;
}