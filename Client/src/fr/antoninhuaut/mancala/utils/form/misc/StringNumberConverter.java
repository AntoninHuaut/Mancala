package fr.antoninhuaut.mancala.utils.form.misc;

import javafx.util.StringConverter;

public class StringNumberConverter extends StringConverter<Number> {

    @Override
    public String toString(Number number) {
        return "" + number;
    }

    @Override
    public Integer fromString(String string) {
        try {
            return Integer.parseInt(string);
        } catch (NumberFormatException ex) {
            return -1;
        }
    }
}
