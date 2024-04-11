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

import javax.xml.crypto.Data;
import java.io.IOException;

@WebSocket
public class WebSocketHandler {
    private final ConnectionManager connections = new ConnectionManager();
    DbAuthDAO dbAuthDAO;
    DbGameDAO dbGameDAO;
    DbUserDAO dbUserDAO;

    public WebSocketHandler()  {
        try {
            this.dbAuthDAO = new DbAuthDAO();
            dbGameDAO = new DbGameDAO();
            dbUserDAO = new DbUserDAO();
        }catch (DataAccessException ex){
            System.out.println(ex.getMessage());
        }
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) {
        UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);
        try {
            if (dbAuthDAO.getAuth(command.getAuthString()) != null) {
                switch (command.getCommandType()) {
                    case JOIN_PLAYER -> {
                        JoinPlayerCommand joinPlayerCommand = new Gson().fromJson(message, JoinPlayerCommand.class);
                        joinPlayer(joinPlayerCommand, session);
                    }
                    case JOIN_OBSERVER -> {
                        JoinObserverCommand joinObserverCommand = new Gson().fromJson(message, JoinObserverCommand.class);
                        joinObserver(joinObserverCommand, session);
                    }
                    case MAKE_MOVE -> {
                        MakeMoveCommand makeMoveCommand = new Gson().fromJson(message, MakeMoveCommand.class);
                        makeMove(makeMoveCommand, session);
                    }
                    case LEAVE -> {
                        LeaveCommand leaveCommand = new Gson().fromJson(message, LeaveCommand.class);
                        leave(leaveCommand, session);
                    }
                    case RESIGN -> {
                        ResignCommand resignCommand = new Gson().fromJson(message, ResignCommand.class);
                        resign(resignCommand, session);
                    }
                }
            }
        } catch (DataAccessException ex) {
            System.out.println(ex.getMessage());
        }
    }
    private void joinPlayer(JoinPlayerCommand command, Session session) {
        try {
            ServerMessage serverMessage;
            var username = dbAuthDAO.getUsername(command.getAuthString());
            GameData gameData = dbGameDAO.getGame(dbGameDAO.getGameName(command.getGameID()));
            if (command.getPlayerColor() == ChessGame.TeamColor.BLACK) {
                if (gameData.blackUsername() == null) {
                    serverMessage = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, String.format("%s has joined the game on team BLACK", username));
                } else {
                    serverMessage = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "Error: Black team is already taken");
                }
            } else {
                if (gameData.whiteUsername() == null) {
                    serverMessage = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, String.format("%s has joined the game on team WHITE", username));
                } else {
                    serverMessage = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "Error: White team is already taken");
                }
            }
            connections.add(command.getGameID(), command.getAuthString(), session);
            connections.broadcast(command.getGameID(), command.getAuthString(), serverMessage);
            if (serverMessage.getServerMessageType()!= ServerMessage.ServerMessageType.ERROR) {
                LoadGameMessage loadGameMessage = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, dbGameDAO.getGame(dbGameDAO.getGameName(command.getGameID())).game());
                connections.broadcastToOne(command.getGameID(), command.getAuthString(), loadGameMessage);
            }
        }catch (Exception ex) {
            if (ex.getClass() == DataAccessException.class){
                ErrorMessage serverMessage = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "Error: unable to get game. Please try another game.");
            }else{
                System.out.printf("Error: %s", ex.getMessage());
            }
        }
    }
    private void joinObserver(JoinObserverCommand command, Session session) {
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
                ErrorMessage serverMessage = new ErrorMessage( ServerMessage.ServerMessageType.ERROR,String.format("Error: %s", ex.getMessage()));
                Connection connection = new Connection(command.getAuthString(),session);
                connection.send(serverMessage);
            }else{
                System.out.printf("Error: %s", ex.getMessage());
            }
        }
    }
    private void makeMove(MakeMoveCommand command, Session session) {
        try {
            var username = dbAuthDAO.getUsername(command.getAuthString());
            ChessGame game = dbGameDAO.getGame(dbGameDAO.getGameName(command.getGameID())).game();
            game.makeMove(command.getMove());
            var message = String.format("%s made a move", username);
            LoadGameMessage loadGameMessage = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, game);
            connections.broadcastAll(command.getGameID(), loadGameMessage);
            var serverMessage = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
            connections.broadcast(command.getGameID(), command.getAuthString(), serverMessage);
        } catch (InvalidGameIDException | DataAccessException ex){
            ErrorMessage serverMessage = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, String.format("Error: You are unauthorized or an invalid game ID. here is message: %s",ex.getMessage()));
            Connection connection = new Connection(command.getAuthString(),session);
            connection.send(serverMessage);

        } catch (InvalidMoveException e) {
            ErrorMessage serverMessage = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "Error: This is an invalid move. Please enter a valid move.");
            Connection connection = new Connection(command.getAuthString(),session);
            connection.send(serverMessage);
        }
    }
    private void leave(LeaveCommand command, Session session) {
        try {
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
        }catch (DataAccessException | InvalidGameIDException ex){
            ErrorMessage serverMessage = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "Error: Unable to get game from server.");
            Connection connection = new Connection(command.getAuthString(),session);
            connection.send(serverMessage);
        }
    }
    private void resign(ResignCommand command, Session session) {
        try {
            var username = dbAuthDAO.getUsername(command.getAuthString());
            var message = String.format("%s resigned", username);
            var serverMessage = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
            connections.broadcastAll(command.getGameID(), serverMessage);
            connections.removeGame(command.getGameID());
        } catch (DataAccessException ex){
            ErrorMessage serverMessage = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "Error: Unable to get game from server.");
            Connection connection = new Connection(command.getAuthString(),session);
            connection.send(serverMessage);
        }
    }
}
