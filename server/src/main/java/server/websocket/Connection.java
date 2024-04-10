package server.websocket;
import org.eclipse.jetty.websocket.api.Session;

import java.io.IOException;

public class Connection {
    String authToken;
    Session session;
    public Connection(String authToken, Session session) {
        this.authToken = authToken;
        this.session = session;
    }

    public String getAuthToken() {
        return authToken;
    }

    public Session getSession() {
        return session;
    }
    public void send(String msg) throws IOException {
        session.getRemote().sendString(msg);
    }
}
