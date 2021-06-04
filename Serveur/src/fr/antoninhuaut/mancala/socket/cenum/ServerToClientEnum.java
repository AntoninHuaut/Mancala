package fr.antoninhuaut.mancala.socket.cenum;

public enum ServerToClientEnum {

    WELCOME,
    INIT_PLAYER,
    WAIT_OPPONENT,
    GAME_UPDATE,
    OPPONENT_NAME,
    BAD_STATE,
    END_ROUND,
    END_GAME,
    UNKNOWN;

    public static ServerToClientEnum extractFromCommand(String data) {
        try {
            return ServerToClientEnum.valueOf(data);
        } catch (IllegalArgumentException ignored) {
        }
        return UNKNOWN;
    }

    public enum BadStateEnum {
        NOT_YOUR_TURN,
        CANT_UNDO_NOW,
        NOT_YOUR_CELL;
    }
}
