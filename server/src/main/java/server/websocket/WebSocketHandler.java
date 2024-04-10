package server.websocket;

import chess.ChessGame;
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
//            if (command.getCommandType() == UserGameCommand.CommandType.JOIN_PLAYER){
//                command = new Gson().fromJson(message, JoinPlayerCommand.class);
//                joinPlayer(command, session);
//            }
            switch (command.getCommandType()) {
                case JOIN_PLAYER -> joinPlayer((JoinPlayerCommand) command, session);
                case JOIN_OBSERVER -> joinObserver((JoinObserverCommand)command, session);
                case MAKE_MOVE -> makeMove((MakeMoveCommand) command, session);
                case LEAVE -> leave((LeaveCommand) command, session);
                case RESIGN -> resign((ResignCommand) command, session);
            }
        }
    }
    private void joinPlayer(JoinPlayerCommand command, Session session) throws IOException, DataAccessException {
        ServerMessage serverMessage;
        var username = dbAuthDAO.getUsername(command.getAuthString());
        if (command.getPlayerColor() == ChessGame.TeamColor.BLACK) {
            serverMessage = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, String.format("%s has joined the game on team BLACK", username));
        } else {
            serverMessage = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, String.format("%s has joined the game on team WHITE", username));

        }
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
        try {
            var username = dbAuthDAO.getUsername(command.getAuthString());
            ChessGame game = dbGameDAO.getGame(dbGameDAO.getGameName(command.getGameID())).game();
            game.makeMove(command.getMove());
            var message = String.format("%s made a move", username);
            var serverMessage = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
            connections.broadcast(command.getGameID(), command.getAuthString(), serverMessage);
        } catch (Exception ex){

        }
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
