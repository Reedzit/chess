package server.websocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataAccess.DataAccessException;
import dataAccess.DbAuthDAO;
import dataAccess.DbGameDAO;
import dataAccess.DbUserDAO;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import webSocketMessages.serverMessages.ErrorMessage;
import webSocketMessages.serverMessages.LoadGameMessage;
import webSocketMessages.serverMessages.NotificationMessage;
import webSocketMessages.userCommands.*;
import webSocketMessages.serverMessages.ServerMessage;

import java.util.Objects;

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
            if (dbAuthDAO.getAuth(command.getAuthString()) == null){
                serverMessage = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "Error: unauthorized");
                Connection connection = new Connection(command.getAuthString(),session);
                connection.send(serverMessage);
                return;
            }
            GameData gameData = dbGameDAO.getGame(dbGameDAO.getGameName(command.getGameID()));
            if (gameData == null){
                serverMessage = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "Error: unable to get game. Please try another game.");
                Connection connection = new Connection(command.getAuthString(),session);
                connection.send(serverMessage);
                return;
            }
            if (command.getPlayerColor() == ChessGame.TeamColor.BLACK) {
                if (gameData.blackUsername() == null || gameData.blackUsername().equals(username)) {
                    serverMessage = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, String.format("%s has joined the game on team BLACK", username));
                } else {
                    serverMessage = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "Error: Black team is already taken");
                }
            } else {
                if (gameData.whiteUsername() == null || gameData.whiteUsername().equals(username)) {
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
        }catch (DataAccessException ex) {
//            if (ex.getClass() == DataAccessException.class){
                ErrorMessage serverMessage = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "Error: unable to get game. Please try another game.");
                Connection connection = new Connection(command.getAuthString(),session);
                connection.send(serverMessage);
//            }else{
//                System.out.printf("Error: %s", ex.getMessage());
//            }
        }
    }
    private void joinObserver(JoinObserverCommand command, Session session) {
        try {
            if(dbAuthDAO.getAuth(command.getAuthString()) == null){
                ErrorMessage serverMessage = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "Error: unauthorized");
                Connection connection = new Connection(command.getAuthString(),session);
                connection.send(serverMessage);
                return;
            }
            if (dbGameDAO.getGame(dbGameDAO.getGameName(command.getGameID())) == null){
                ErrorMessage serverMessage = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "Error: unable to get game. Please try another game.");
                Connection connection = new Connection(command.getAuthString(),session);
                connection.send(serverMessage);
                return;
            }
            connections.add(command.getGameID(), command.getAuthString(), session);
            var username = dbAuthDAO.getUsername(command.getAuthString());
            var message = String.format("%s is observing your game", username);
            LoadGameMessage loadGameMessage = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, dbGameDAO.getGame(dbGameDAO.getGameName(command.getGameID())).game());
            connections.broadcastToOne(command.getGameID(), command.getAuthString(), loadGameMessage);
            var serverMessage = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
            connections.broadcast(command.getGameID(), command.getAuthString(), serverMessage);
        }
        catch (DataAccessException ex){
//            if (ex.getClass() == InvalidGameIDException.class){
            ErrorMessage serverMessage = new ErrorMessage( ServerMessage.ServerMessageType.ERROR,String.format("Error: %s", ex.getMessage()));
            Connection connection = new Connection(command.getAuthString(),session);
            connection.send(serverMessage);
//            }else{
//                System.out.printf("Error: %s", ex.getMessage());
//            }
        }
    }
    private void makeMove(MakeMoveCommand command, Session session) {
        try {
            var username = dbAuthDAO.getUsername(command.getAuthString());
            GameData game = dbGameDAO.getGame(dbGameDAO.getGameName(command.getGameID()));
            if(!game.game().validMoves(command.getMove().getStartPosition()).contains(new ChessMove( command.getMove().getStartPosition(), command.getMove().getEndPosition(), null))){
                ErrorMessage serverMessage = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "Error: This is an invalid move. Please enter a valid move.");
                connections.broadcastToOne(command.getGameID(), command.getAuthString(), serverMessage);
                return;
            }
            if (game.game().gameOver){
                ErrorMessage serverMessage = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "Error: This game is over.");
                connections.broadcastToOne(command.getGameID(), command.getAuthString(), serverMessage);
            }
            game.game().makeMove(command.getMove());
            var message = String.format("%s made a move", username);
            LoadGameMessage loadGameMessage = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, game.game());
            connections.broadcastAll(command.getGameID(), loadGameMessage);
            var serverMessage = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
            connections.broadcast(command.getGameID(), command.getAuthString(), serverMessage);
            if (game.game().isInStalemate(ChessGame.TeamColor.BLACK)){
               NotificationMessage notification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, "Black is in stalemate. White team wins!");
               connections.broadcastAll(command.getGameID(), notification);
               game.game().gameOver();
            }else if (game.game().isInStalemate(ChessGame.TeamColor.WHITE)){
                NotificationMessage notification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, "White is in stalemate. Black team wins!");
                connections.broadcastAll(command.getGameID(), notification);
            }else if (game.game().isInCheckmate(ChessGame.TeamColor.WHITE)){
                NotificationMessage notification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, "White is in checkmate. Black team wins!");
                connections.broadcastAll(command.getGameID(), notification);
            }else if (game.game().isInCheckmate(ChessGame.TeamColor.BLACK)){
                NotificationMessage notification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, "Black is in checkmate. White team wins!");
                connections.broadcastAll(command.getGameID(), notification);
            }else if (game.game().isInCheck(ChessGame.TeamColor.WHITE)){
               if(!Objects.equals(dbAuthDAO.getUsername(command.getAuthString()), game.whiteUsername())){
                   NotificationMessage notification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, String.format("%s is now in check.",game.whiteUsername()));
                   connections.broadcastAll(command.getGameID(), notification);
               }
            }else if (game.game().isInCheck(ChessGame.TeamColor.BLACK)){
                if(!Objects.equals(dbAuthDAO.getUsername(command.getAuthString()), game.blackUsername())){
                    NotificationMessage notification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, String.format("%s is now in check.",game.whiteUsername()));
                    connections.broadcastAll(command.getGameID(), notification);
                }
            }
        } catch (DataAccessException ex){
            ErrorMessage serverMessage = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, String.format("Error: You are unauthorized or an invalid game ID. here is message: %s",ex.getMessage()));
            Connection connection = new Connection(command.getAuthString(),session);
            connection.send(serverMessage);

        } catch (InvalidMoveException e) {
            ErrorMessage serverMessage = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "Error: This is an invalid move. Please enter a valid move.");
            connections.broadcastToOne(command.getGameID(), command.getAuthString(), serverMessage);
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
        }catch (DataAccessException ex){
            ErrorMessage serverMessage = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "Error: Unable to get game from server.");
            Connection connection = new Connection(command.getAuthString(),session);
            connection.send(serverMessage);
        }
    }
    private void resign(ResignCommand command, Session session) {
        try {
            var username = dbAuthDAO.getUsername(command.getAuthString());
            GameData game = dbGameDAO.getGame(dbGameDAO.getGameName(command.getGameID()));
            if (!Objects.equals(dbAuthDAO.getUsername(command.getAuthString()), game.whiteUsername()) ||!Objects.equals(dbAuthDAO.getUsername(command.getAuthString()), game.blackUsername())){
                ErrorMessage errorMessage = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "Error: You cannot resign as an observer");
                connections.broadcastToOne(command.getGameID(), command.getAuthString(), errorMessage);
                return;
            }
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
