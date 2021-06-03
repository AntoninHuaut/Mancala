package fr.antoninhuaut.mancala.model.enums;

public enum SocketExchangeEnum {

    WELCOME,
    INIT_PLAYER,
    MESSAGE,
    GAME_UPDATE,
    OPPONENT_NAME,
    UNKNOWN;

    public static SocketExchangeEnum extractFromCommand(String data) {
        try {
            return SocketExchangeEnum.valueOf(data);
        } catch (IllegalArgumentException ignored) {
        }
        return UNKNOWN;
    }

}
