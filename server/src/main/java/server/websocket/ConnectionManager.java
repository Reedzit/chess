package server.websocket;

import exception.InvalidGameIDException;
import org.eclipse.jetty.websocket.api.Session;
import server.Server;
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
    public void broadcast(Integer gameID, String excludeAuthToken, ServerMessage msg) throws InvalidGameIDException {
        try {
            var removeList = new ArrayList<Connection>();
            if (!connections.containsKey(gameID)) {
                throw new InvalidGameIDException("Error: Invalid gameID. Please choose a valid gameID");
            }
            var list = connections.get(gameID);
            if (msg.getServerMessageType() == ServerMessage.ServerMessageType.NOTIFICATION) {
                for (var connection : list) {
                    if (!connection.getSession().isOpen()) {
                        removeList.add(connection);
                    } else if (connection.getAuthToken().equals(excludeAuthToken)) {
                        continue;
                    } else {
                        connection.send(msg);
                    }
                }
                for (var remove : removeList) {
                    list.removeIf(c -> c.getAuthToken().equals(remove.getAuthToken()));
                }
            } else if (msg.getServerMessageType() == ServerMessage.ServerMessageType.ERROR) {
                for (var connection : list) {
                    if (connection.getAuthToken().equals(excludeAuthToken)) {
                        connection.send(msg);
                        return;
                    }
                }
            } else if (msg.getServerMessageType() == ServerMessage.ServerMessageType.LOAD_GAME) {
                for (var connection : list) {
                    if (connection.getAuthToken().equals(excludeAuthToken)) {
                        continue;
                    }
                    connection.send(msg);
                }
            }
        } catch (IOException ex){
            System.out.printf("This is the message for a ws send error: %s%n", ex.getMessage());
        }
    }
    public void broadcastAll(Integer gameID, ServerMessage msg) {
        for (var connection : connections.get(gameID)) {
            connection.send(msg);
        }
    }
    public void broadcastToOne(Integer gameID, String authToken, ServerMessage msg) throws IOException {
        for (var connection : connections.get(gameID)) {
            if (connection.getAuthToken().equals(authToken)) {
                connection.send(msg);
                return;
            }

        }
    }
}
