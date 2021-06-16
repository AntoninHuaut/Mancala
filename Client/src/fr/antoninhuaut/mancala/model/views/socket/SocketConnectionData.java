package fr.antoninhuaut.mancala.model.views.socket;

import fr.antoninhuaut.mancala.utils.PreferenceUtils;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class SocketConnectionData {

    private final SimpleStringProperty username = new SimpleStringProperty(PreferenceUtils.getInstance().getUsername());
    private final SimpleStringProperty host = new SimpleStringProperty(PreferenceUtils.getInstance().getSocketHost());
    private final SimpleIntegerProperty port = new SimpleIntegerProperty(PreferenceUtils.getInstance().getSocketPort());
    private final SimpleStringProperty sessionId = new SimpleStringProperty();

    public String getHost() {
        return host.get();
    }

    public SimpleStringProperty hostProperty() {
        return host;
    }

    public String getSessionId() {
        return sessionId.get();
    }

    public SimpleStringProperty sessionIdProperty() {
        return sessionId;
    }

    public int getPort() {
        return port.get();
    }

    public SimpleIntegerProperty portProperty() {
        return port;
    }

    public String getUsername() {
        return username.get();
    }

    public SimpleStringProperty usernameProperty() {
        return username;
    }

    public void setUsername(String username) {
        this.username.set(username);
    }
}
