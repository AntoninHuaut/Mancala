package fr.antoninhuaut.mancala.model.enums;

public enum ClientToServerEnum {

    CLIENT_INIT,
    MOVE,

    SAVE_MATCH,
    LOAD_MATCH,
    NEW_MATCH,
    STOP_MATCH,

    ASK_FOR_SURRENDER_VOTE,
    ACCEPT_SURRENDER,
    REFUSE_SURRENDER,

    UNDO,

    UNKNOWN;
    public static ClientToServerEnum extractFromCommand(String data) {
        try {
            return ClientToServerEnum.valueOf(data);
        } catch (IllegalArgumentException ignored) {
        }
        return UNKNOWN;
    }

}
