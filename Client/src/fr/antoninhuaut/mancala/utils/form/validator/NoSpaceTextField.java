package fr.antoninhuaut.mancala.utils.form.validator;

import javafx.scene.control.TextField;

public class NoSpaceTextField extends ValidatorTextField {

    public NoSpaceTextField(TextField textField) {
       super(textField);
    }

    public void apply() {
        textField.textProperty().addListener((observable, oldValue, newValue) -> textField.setText(newValue.replaceAll(" ", "")));
    }
}
