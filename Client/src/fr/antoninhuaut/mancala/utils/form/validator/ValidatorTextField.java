package fr.antoninhuaut.mancala.utils.form.validator;

import javafx.scene.control.TextField;

public abstract class ValidatorTextField {

    protected final TextField textField;

    public ValidatorTextField(TextField textField) {
        this.textField = textField;
    }

    public abstract void apply();
}
