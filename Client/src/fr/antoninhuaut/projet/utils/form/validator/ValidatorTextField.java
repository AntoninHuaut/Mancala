package fr.antoninhuaut.projet.utils.form.validator;

import javafx.scene.control.TextField;

public abstract class ValidatorTextField {

    protected final TextField textField;

    public ValidatorTextField(TextField textField) {
        this.textField = textField;
    }

    public abstract void apply();
}
