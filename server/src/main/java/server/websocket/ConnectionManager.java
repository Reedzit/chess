package server.websocket;

import org.eclipse.jetty.websocket.api.Session;
import webSocketMessages.serverMessages.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public final ConcurrentHashMap<Integer, Connection> connections = new ConcurrentHashMap<>();
// make the key a game ID or soemthing like that
    public void add(Integer gameID, Session session) {
        var connection = new Connection(gameID,session);
        connections.put(gameID, connection);
    }
    public void remove(Integer gameID) {
        connections.remove(gameID);
    }
    public void broadcast(String excludeAuthToken, ServerMessage msg) throws IOException{
        var removeList = new ArrayList<Connection>();
    }
}
