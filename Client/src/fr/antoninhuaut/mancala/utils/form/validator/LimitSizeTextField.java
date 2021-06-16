package fr.antoninhuaut.mancala.utils.form.validator;

import javafx.scene.control.TextField;

public class LimitSizeTextField extends ValidatorTextField {

    private final int limitIncluded;

    public LimitSizeTextField(TextField textField, int limitIncluded) {
        super(textField);
        this.limitIncluded = limitIncluded;
    }

    public void apply() {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() > limitIncluded) {
                textField.setText(oldValue);
            }
        });
    }
}
