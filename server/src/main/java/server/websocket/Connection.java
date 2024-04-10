package server.websocket;
import org.eclipse.jetty.websocket.api.Session;
public class Connection {
    Integer gameID;
    Session session;
    public Connection(Integer gameID, Session session) {
        this.gameID = gameID;
        this.session = session;
    }
}
