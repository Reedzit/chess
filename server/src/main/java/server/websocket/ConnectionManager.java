package server.websocket;

import org.eclipse.jetty.websocket.api.Session;
import shared.src.main.java.webSocketMessages.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public final ConcurrentHashMap<String, Session> connections = new ConcurrentHashMap<>();
// make the key a game ID or soemthing like that
    public void add(String authToken, Session session) {
        var connection = new Connection(authToken,session);
        connections.put(authToken, connection);
    }
    public void remove(String authToken) {
        connections.remove(authToken);
    }
    public void broadcast(String excludeAuthToken, ServerMessage msg) throws IOException{
        var removeList = new ArrayList<Connection>();
    }
}
