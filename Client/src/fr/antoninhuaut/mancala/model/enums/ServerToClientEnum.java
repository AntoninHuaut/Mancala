package fr.antoninhuaut.mancala.model.enums;

public enum ServerToClientEnum {

    WELCOME,
    INIT_PLAYER,
    WAIT_OPPONENT,
    GAME_UPDATE,
    OPPONENT_NAME,
    MESSAGE,

    END_ROUND,
    END_GAME,

    SAVE_SUCCESS,
    SAVE_FAILED,
    LOAD_SAVE_SUCCESS,
    LOAD_SAVE_FAILED,

    ASK_TO_SURRENDER,
    RESPONSE_HIGHSCORE,

    UNKNOWN;

    public static ServerToClientEnum extractFromCommand(String data) {
        try {
            return ServerToClientEnum.valueOf(data);
        } catch (IllegalArgumentException ignored) {
        }
        return UNKNOWN;
    }

    public enum MessageEnum {
        NOT_YOUR_TURN,
        CANT_UNDO_NOW,
        NOT_YOUR_CELL,
        NEW_MATCH,
        MATCH_LOAD_FROM_SAVE,
        SUCCESS_SURRENDER,
        FAIL_SURRENDER,
        SUCCESS_SOLO_SURRENDER,
        STOP_MATCH;
    }
}