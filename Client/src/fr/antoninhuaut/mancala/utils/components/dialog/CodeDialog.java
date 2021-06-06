package fr.antoninhuaut.mancala.utils.components.dialog;

import fr.antoninhuaut.mancala.utils.I18NUtils;
import javafx.scene.control.TextInputDialog;

import java.util.Optional;
import java.util.function.Consumer;

public class CodeDialog extends TextInputDialog {

    public CodeDialog(String key, String defaultCode, Consumer<String> consumerIfPresent) {
        super(defaultCode);
        this.titleProperty().bind(I18NUtils.getInstance().bindStr(key + ".title"));
        this.setHeaderText(null);
        this.contentTextProperty().bind(I18NUtils.getInstance().bindStr(key + ".content"));

        Optional<String> result = this.showAndWait();
        result.ifPresent(consumerIfPresent);
    }
}
