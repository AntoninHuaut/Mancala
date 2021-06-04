package fr.antoninhuaut.mancala.model.enums;

public enum ClientToServerEnum {

    CLIENT_INIT,
    MOVE,

    UNDO,
    STOP_MATCH,

    UNKNOWN;

    public static ClientToServerEnum extractFromCommand(String data) {
        try {
            return ClientToServerEnum.valueOf(data);
        } catch (IllegalArgumentException ignored) {
        }
        return UNKNOWN;
    }

}
