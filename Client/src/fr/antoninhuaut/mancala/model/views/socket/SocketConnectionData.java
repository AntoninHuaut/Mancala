package fr.antoninhuaut.mancala.model.views.socket;

import fr.antoninhuaut.mancala.utils.PreferenceUtils;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class SocketConnectionData {

    private final SimpleStringProperty username = new SimpleStringProperty(PreferenceUtils.getInstance().getUsername());
    private final SimpleStringProperty host = new SimpleStringProperty(PreferenceUtils.getInstance().getSocketHost());
    private final SimpleIntegerProperty port = new SimpleIntegerProperty(PreferenceUtils.getInstance().getSocketPort());

    public String getHost() {
        return host.get();
    }

    public SimpleStringProperty hostProperty() {
        return host;
    }

    public void setHost(String host) {
        this.host.set(host);
    }

    public int getPort() {
        return port.get();
    }

    public SimpleIntegerProperty portProperty() {
        return port;
    }

    public void setPort(int port) {
        this.port.set(port);
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
