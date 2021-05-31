package fr.antoninhuaut.projet.utils.form.validator;

import javafx.scene.control.TextField;

public class IntegerTextField extends ValidatorTextField {

    public IntegerTextField(TextField textField) {
        super(textField);
    }

    public void apply() {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.matches("\\d*")) return;
            textField.setText(newValue.replaceAll("[^\\d]", ""));
        });
    }
}
