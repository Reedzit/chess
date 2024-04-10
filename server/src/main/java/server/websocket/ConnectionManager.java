package server.websocket;

import org.eclipse.jetty.websocket.api.Session;
import webSocketMessages.serverMessages.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public final ConcurrentHashMap<Integer, List<Connection>> connections = new ConcurrentHashMap<>();
// make the key a game ID or soemthing like that
    public void add(Integer gameID, String authToken, Session session) {
        var connection = new Connection(authToken,session);
        var list = connections.get(gameID);
        list.add(connection);
    }
    public void removeGame(Integer gameID) {
        connections.remove(gameID);
    }
    public void removePlayer(Integer gameID, String authToken, Session session) {
        var list = connections.get(gameID);
        list.removeIf(connection -> connection.getAuthToken().equals(authToken));

    }
    public void broadcast(Integer gameID, String excludeAuthToken, ServerMessage msg) throws IOException{
        var removeList = new ArrayList<Connection>();
        var list = connections.get(gameID);
        for (var connection : list) {
            if(connection.getAuthToken().equals(excludeAuthToken) || !connection.getSession().isOpen()){
                removeList.add(connection);
            }else {
                connection.send(msg.toString());
            }
        }
        for (var remove : removeList){
            list.removeIf(c -> c.getAuthToken().equals(remove.getAuthToken()));
        }
    }
}
