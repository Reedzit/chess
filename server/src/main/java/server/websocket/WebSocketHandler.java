package server.websocket;

import com.google.gson.Gson;
import dataAccess.DataAccessException;
import dataAccess.DbAuthDAO;
import dataAccess.DbGameDAO;
import dataAccess.DbUserDAO;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import webSocketMessages.serverMessages.NotificationMessage;
import webSocketMessages.userCommands.*;
import webSocketMessages.serverMessages.ServerMessage;

import java.io.IOException;

@WebSocket
public class WebSocketHandler {
    private final ConnectionManager connections = new ConnectionManager();
    DbAuthDAO dbAuthDAO = new DbAuthDAO();
    DbGameDAO dbGameDAO = new DbGameDAO();
    DbUserDAO dbUserDAO = new DbUserDAO();

    public WebSocketHandler() throws DataAccessException {
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException, DataAccessException {
        UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);
        if(new DbAuthDAO().getAuth(command.getAuthString())!= null) {
            switch (UserGameCommand.getCommandType()) {
                case JOIN_PLAYER -> joinPlayer(command, session);
                case JOIN_OBSERVER -> joinObserver(command, session);
                case MAKE_MOVE -> makeMove(command, session);
                case LEAVE -> leave(command, session);
                case RESIGN -> resign(command, session);
            }
        }
    }
    private void joinPlayer(JoinPlayerCommand command, Session session) throws IOException, DataAccessException {

        var username = dbAuthDAO.getUsername(command.getAuthString());
        var serverMessage = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, String.format("%s has joined the game", username));
        connections.add(command.getGameID(), command.getAuthString(), session);
        connections.broadcast(command.getGameID(), command.getAuthString(), serverMessage);

    }
    private void joinObserver(JoinObserverCommand command, Session session) throws IOException, DataAccessException {
        connections.add(command.getGameID(), command.getAuthString(), session);
        var username = dbAuthDAO.getUsername(command.getAuthString());
        var message = String.format("%s is observing your game", username);
        var serverMessage = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
        connections.broadcast(command.getGameID(), command.getAuthString(), serverMessage);
    }
    private void makeMove(MakeMoveCommand command, Session session) throws DataAccessException, IOException {
        var username = dbAuthDAO.getUsername(command.getAuthString());
        var message = String.format("%s made a move", username);
        var serverMessage = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
        connections.broadcast(command.getGameID(), command.getAuthString(), serverMessage);
    }
    private void leave(LeaveCommand command, Session session) throws DataAccessException, IOException {
        connections.removePlayer(command.getGameID(), command.getAuthString(), session);
        var username = dbAuthDAO.getUsername(command.getAuthString());
        var message = String.format("%s left the game", username);
        var serverMessage = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
        connections.broadcast(command.getGameID(), command.getAuthString(), serverMessage);
    }
    private void resign(ResignCommand command, Session session) throws DataAccessException, IOException {
        connections.removePlayer(command.getGameID(), command.getAuthString(), session);
        var username = dbAuthDAO.getUsername(command.getAuthString());
        var message = String.format("%s resigned. You win!", username);
        var serverMessage = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
        connections.broadcast(command.getGameID(), command.getAuthString(), serverMessage);
    }
}
