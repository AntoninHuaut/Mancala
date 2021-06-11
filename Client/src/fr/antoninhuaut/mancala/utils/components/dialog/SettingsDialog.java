package fr.antoninhuaut.mancala.utils.components.dialog;

import fr.antoninhuaut.mancala.model.enums.UserPrefType;
import fr.antoninhuaut.mancala.utils.I18NUtils;
import fr.antoninhuaut.mancala.utils.PreferenceUtils;
import fr.antoninhuaut.mancala.utils.components.SwitchButton;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

import static fr.antoninhuaut.mancala.model.enums.UserPrefType.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class SettingsDialog extends GenericDialog<Void> {

    private final GridPane grid;
    private int line = 0;

    private final HashMap<UserPrefType, SwitchButton> prefMap = new HashMap<>();

    public SettingsDialog(String key) {
        super(key);

        this.grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        getDialogPane().setContent(grid);

        List<UserPrefType> settingsPref = Arrays.asList(SEE_SEED_HOVER, SEE_SEED, SOUND, MUSIC);
        for (UserPrefType prefType : settingsPref) {
            addConfigBoolean(prefType);
        }

        createLimits();

        var submit = (Button) getDialogPane().lookupButton(getSubmitButtonType());
        var cancel = (Button) getDialogPane().lookupButton(getCancelButtonType());
        submit.getStyleClass().addAll("success");
        cancel.getStyleClass().addAll("error");

        submit.setOnAction(ignore -> {
            for (UserPrefType prefType : settingsPref) {
                PreferenceUtils.getInstance().setPref(prefType, "" + prefMap.get(prefType).stateProperty().get());
            }
        });
    }

    private void createLimits() {
        prefMap.get(SEE_SEED).stateProperty().addListener((ob, o, n) -> {
            if (n) {
                prefMap.get(SEE_SEED_HOVER).stateProperty().set(true);
            }
        });

        prefMap.get(SEE_SEED_HOVER).stateProperty().addListener((ob, o, n) -> {
            var seed = prefMap.get(SEE_SEED).stateProperty();
            if (!n && seed.get()) {
                prefMap.get(SEE_SEED_HOVER).stateProperty().set(true);
            }
        });
    }

    private void addConfigBoolean(UserPrefType prefType) {
        var switchBtn = new SwitchButton(PreferenceUtils.getInstance().getSettingsPrefs().get(prefType).get());
        switchBtn.setUserData(prefType);
        grid.add(getLabel("settings." + prefType.name().toLowerCase()), 0, line);
        grid.add(switchBtn, 1, line++);
        prefMap.put(prefType, switchBtn);
    }

    private Label getLabel(String key) {
        var lab = new Label();
        lab.textProperty().bind(I18NUtils.getInstance().bindStr(key));
        return lab;
    }
}
