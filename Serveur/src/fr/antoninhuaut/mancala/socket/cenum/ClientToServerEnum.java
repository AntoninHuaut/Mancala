package fr.antoninhuaut.mancala.socket.cenum;

public enum ClientToServerEnum {

    CLIENT_SESSION,
    CLIENT_NAME,
    PLAY_WITH_BOT,
    MOVE,

    SAVE_MATCH,
    LOAD_MATCH,
    NEW_MATCH,
    STOP_MATCH,
    UNDO,

    ASK_FOR_SURRENDER_VOTE,
    ACCEPT_SURRENDER,
    REFUSE_SURRENDER,
    SOLO_SURRENDER,

    ASK_HIGHSCORE,

    UNKNOWN;
    public static ClientToServerEnum extractFromCommand(String data) {
        try {
            return ClientToServerEnum.valueOf(data);
        } catch (IllegalArgumentException ignored) {
        }
        return UNKNOWN;
    }

}
