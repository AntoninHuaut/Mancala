package fr.antoninhuaut.projet.model.views.socket;

import javafx.beans.property.SimpleBooleanProperty;

public class SocketConnectionModel {

    private final SimpleBooleanProperty loadingSpinnerVisibility = new SimpleBooleanProperty(false);
    private final SimpleBooleanProperty errorMsgVisibility = new SimpleBooleanProperty(false);

    public SimpleBooleanProperty loadingSpinnerVisibilityProperty() {
        return loadingSpinnerVisibility;
    }

    public void setLoadingSpinnerVisibility(boolean loadingSpinnerVisibility) {
        this.loadingSpinnerVisibility.set(loadingSpinnerVisibility);
    }

    public SimpleBooleanProperty errorMsgVisibilityProperty() {
        return errorMsgVisibility;
    }

    public void setErrorMsgVisibility(boolean errorMsgVisibility) {
        this.errorMsgVisibility.set(errorMsgVisibility);
    }
}
