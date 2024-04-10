package server.websocket;

import chess.ChessGame;
import com.google.gson.Gson;
import dataAccess.DataAccessException;
import dataAccess.DbAuthDAO;
import dataAccess.DbUserDAO;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import service.UserService;
import webSocketMessages.userCommands.UserGameCommand;
import webSocketMessages.serverMessages.ServerMessage;

import java.io.IOException;
import java.util.jar.JarEntry;

@WebSocket
public class WebSocketHandler {
    private final ConnectionManager connections = new ConnectionManager();
    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException, DataAccessException {
        UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);
        if(new DbAuthDAO().getAuth(command.getAuthString())!= null) {
            switch (UserGameCommand.getCommandType()) {
                case JOIN_PLAYER -> joinPlayer(command, session);
                case JOIN_OBSERVER -> joinObserver(command, session);
                case MAKE_MOVE -> makeMove();
                case LEAVE -> leave();
                case RESIGN -> resign();
            }
        }
    }
    private void joinPlayer(Integer gameID, ChessGame.TeamColor playerColor, Session session) throws IOException, DataAccessException {
        try {
            connections.add();
            var username = new DbAuthDAO().getUsername(authToken);
            var teamColor = ChessGame.TeamColor.WHITE;
                    var message = String.format("%s has joined the game", username);
            var serverMessage = new ServerMessage(ServerMessage.Type.Notification, message);
            connections.broadcast(authToken, serverMessage);
        } catch (DataAccessException | IOException ex){

        }
    }
    private void joinObserver(Integer gameID, Session session) throws IOException, DataAccessException {
        connections.add(authToken,session);
        var username = new DbAuthDAO().getUsername(authToken);
        var message = String.format("%s is observing your game", username);
        var serverMessage = new ServerMessage(ServerMessage.Type.Notification, message);
        connections.broadcast(authToken, serverMessage);
    }
    private void makeMove(String authToken, Session session) {

    }

}
