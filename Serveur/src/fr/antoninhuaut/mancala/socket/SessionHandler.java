package fr.antoninhuaut.mancala.socket;

import fr.antoninhuaut.mancala.socket.cenum.ServerToClientEnum;
import fr.antoninhuaut.mancala.socket.player.ServerPlayer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SessionHandler implements Runnable {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final List<Session> sessionList = new ArrayList<>();

    private final Socket socket;

    public SessionHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            var p = new ServerPlayer(socket);
            var sessionId = p.waitSessionId();
            var seletectedSession = getSessionOrCreateOne(sessionId);
            if (seletectedSession == null) {
                p.sendData(ServerToClientEnum.SESSION_FULL);
                socket.close();
                return;
            }

            seletectedSession.addPlayer(p);
            p.listenClient();
        } catch (IOException | ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    private static synchronized Session getSessionOrCreateOne(String sessionId) {
        Session selectedSession;
        var stream = sessionList.stream();
        if (sessionId != null) {
            stream = stream.filter(ses -> ses.getSessionId().equals(sessionId));
        } else {
            stream = stream.filter(ses -> ses.getNbPlayer() == 1);
        }

        Optional<Session> optSession = stream.findFirst();

        if (optSession.isPresent()) {
            if (optSession.get().getNbPlayer() >= 2) {
                return null;
            }

            selectedSession = optSession.get();
            LOGGER.debug("Using existing session n°{}", selectedSession.getSessionId());
        } else {
            var session = new Session(sessionId);
            LOGGER.debug("Creating new session n°{}", session.getSessionId());
            selectedSession = session;

            SessionHandler.sessionList.add(session);
        }

        LOGGER.debug("Active session: {}", sessionList.size());
        return selectedSession;
    }

    public static synchronized void destroySession(Session toDestroy) {
        LOGGER.debug("Destroying session: {}", toDestroy.getSessionId());
        sessionList.remove(toDestroy);
    }
}