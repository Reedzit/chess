package server.websocket;

import chess.ChessGame;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataAccess.DataAccessException;
import dataAccess.DbAuthDAO;
import dataAccess.DbGameDAO;
import dataAccess.DbUserDAO;
import exception.InvalidGameIDException;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import webSocketMessages.serverMessages.ErrorMessage;
import webSocketMessages.serverMessages.LoadGameMessage;
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
        NotificationMessage serverMessage;
        var username = dbAuthDAO.getUsername(command.getAuthString());

        if (command.getPlayerColor() == ChessGame.TeamColor.BLACK) {
            serverMessage = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, String.format("%s has joined the game on team BLACK", username));
        } else {
            serverMessage = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, String.format("%s has joined the game on team WHITE", username));
        }

        connections.add(command.getGameID(), command.getAuthString(), session);
        connections.broadcast(command.getGameID(), command.getAuthString(), serverMessage);
        LoadGameMessage loadGameMessage = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, dbGameDAO.getGame(dbGameDAO.getGameName(command.getGameID())).game());
        connections.broadcastToOne(command.getGameID(), command.getAuthString(), loadGameMessage);

    }
    private void joinObserver(JoinObserverCommand command, Session session) throws IOException, DataAccessException {
        try {
            connections.add(command.getGameID(), command.getAuthString(), session);
            var username = dbAuthDAO.getUsername(command.getAuthString());
            var message = String.format("%s is observing your game", username);
            LoadGameMessage loadGameMessage = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, dbGameDAO.getGame(dbGameDAO.getGameName(command.getGameID())).game());
            connections.broadcastToOne(command.getGameID(), command.getAuthString(), loadGameMessage);
            var serverMessage = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
            connections.broadcast(command.getGameID(), command.getAuthString(), serverMessage);
        }catch (Exception ex){
            if (ex.getClass() == InvalidGameIDException.class){
                connections.broadcastToOne(command.getGameID(), command.getAuthString(),new ErrorMessage( ServerMessage.ServerMessageType.ERROR,ex.getMessage()));
            }
        }
    }
    private void makeMove(MakeMoveCommand command, Session session) throws IOException {
        try {
            var username = dbAuthDAO.getUsername(command.getAuthString());
            ChessGame game = dbGameDAO.getGame(dbGameDAO.getGameName(command.getGameID())).game();
            game.makeMove(command.getMove());
            var message = String.format("%s made a move", username);
            LoadGameMessage loadGameMessage = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, game);
            connections.broadcastAll(command.getGameID(), loadGameMessage);
            var serverMessage = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
            connections.broadcast(command.getGameID(), command.getAuthString(), serverMessage);
        } catch (Exception ex){
            if (ex.getClass() == InvalidMoveException.class){
                ErrorMessage serverMessage = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "This is an invalid move. Please enter a valid move.");
                connections.broadcastToOne(command.getGameID(), command.getAuthString(), serverMessage);
            }

        }
    }
    private void leave(LeaveCommand command, Session session) throws DataAccessException, IOException {
        var username = dbAuthDAO.getUsername(command.getAuthString());
        GameData gameData = dbGameDAO.getGame(dbGameDAO.getGameName(command.getGameID()));
        if (command.getPlayerColor() == ChessGame.TeamColor.WHITE) {
            dbGameDAO.updateGame(new GameData(gameData.gameID(), null, gameData.blackUsername(), gameData.gameName(), gameData.game()));
        } else {
            dbGameDAO.updateGame(new GameData(gameData.gameID(), gameData.whiteUsername(), null, gameData.gameName(), gameData.game()));
        }
        var message = String.format("%s left the game", username);
        var serverMessage = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
        connections.broadcast(command.getGameID(), command.getAuthString(), serverMessage);
        connections.removePlayer(command.getGameID(), command.getAuthString(), session);
    }
    private void resign(ResignCommand command, Session session) throws DataAccessException, IOException {
        var username = dbAuthDAO.getUsername(command.getAuthString());
        var message = String.format("%s resigned", username);
        var serverMessage = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
        connections.broadcastAll(command.getGameID(), serverMessage);
        connections.removeGame(command.getGameID());
    }
}
