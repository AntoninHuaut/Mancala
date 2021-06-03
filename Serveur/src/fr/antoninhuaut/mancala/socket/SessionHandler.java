package fr.antoninhuaut.mancala.socket;

import fr.antoninhuaut.mancala.model.Player;
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
            var p = new Player(socket);
            getSessionOrCreateOne().addPlayer(p);
            p.listenClient();
        } catch (IOException | ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    private static synchronized Session getSessionOrCreateOne() {
        Session selectedSession;
        Optional<Session> optSession = sessionList.stream().filter(ses -> ses.getNbPlayer() == 1).findFirst();

        if (optSession.isPresent()) {
            selectedSession = optSession.get();
            LOGGER.debug("Using existing session n°{}", selectedSession.getSessionId());
        } else {
            var session = new Session();
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