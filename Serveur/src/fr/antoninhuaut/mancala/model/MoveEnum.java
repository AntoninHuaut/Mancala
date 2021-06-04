package fr.antoninhuaut.mancala.model;

public enum MoveEnum {

    SUCCESS,
    SUCCESS_FORBIDDEN_CAPTURE,
    FAILED_MOVE_ALREADY_DONE,
    FAILED_FEED_OPPONENT_POSSIBLE,
    SUCCESS_AND_WIN_OPPONENT_CANT_PLAY;

    public boolean isSuccess() {
        return this == SUCCESS || this == SUCCESS_FORBIDDEN_CAPTURE || this == SUCCESS_AND_WIN_OPPONENT_CANT_PLAY;
    }
}
