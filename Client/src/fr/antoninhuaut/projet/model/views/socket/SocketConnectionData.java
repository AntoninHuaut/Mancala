package fr.antoninhuaut.projet.model.views.socket;

import fr.antoninhuaut.projet.utils.PreferenceUtils;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class SocketConnectionData {

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
}
