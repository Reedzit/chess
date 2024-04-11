package webSocket;

import chess.ChessGame;
import chess.ChessMove;
import com.google.gson.Gson;
import exception.ResponseException;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import webSocketMessages.serverMessages.*;
import webSocketMessages.userCommands.*;

public class WebSocketFacade extends Endpoint{
    Session session;
    NotificationHandler notificationHandler;
    public WebSocketFacade (String url, NotificationHandler notificationHandler) throws ResponseException {
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/connect");
            this.notificationHandler = notificationHandler;
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);
            this.session.addMessageHandler(new MessageHandler.Whole<String>(){
                @Override
                public void onMessage(String msg) {
                    ServerMessage serverMessage = new Gson().fromJson(msg, ServerMessage.class);
                    notificationHandler.notify(serverMessage);
                }
            });
        } catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }
    public void send(String msg) {
        try {
            this.session.getBasicRemote().sendText(msg); // call this after every method
        }catch (Exception e){
            System.out.printf("This exception is thrown trying to send a message from client to the server: %s",e.getMessage());
        }
    }

    public void makeMove(String authToken, Integer gameID, ChessMove move) {
        try{
            var moveCommand = new MakeMoveCommand(authToken, gameID, move);
            this.send(new Gson().toJson(moveCommand));


        }catch (Exception ex){
            System.out.println("Error: Unable to make connection to server");
        }
    }
    public void joinPlayer(Integer gameID, String authToken, ChessGame.TeamColor playerColor)  {
        try {
            JoinPlayerCommand command = new JoinPlayerCommand(authToken, gameID, playerColor);
            this.send(new Gson().toJson(command));
        }catch (Exception ex){
            System.out.println("Error: Unable to make connection to server");
        }
    }
    public void joinObserver(Integer gameID, String authToken) {
        try {
            JoinObserverCommand command = new JoinObserverCommand(authToken, gameID);
            this.send(new Gson().toJson(command));
        }catch (Exception ex){
            System.out.println("Error: Unable to make connection to server");
        }
    }
    public void leave(String authToken, Integer gameID, ChessGame.TeamColor playerColor) {
        try {
            LeaveCommand command = new LeaveCommand(authToken, gameID, playerColor);
            this.send(new Gson().toJson(command));
        }catch (Exception ex){
            System.out.println("Error: Unable to make connection to server");
        }
    }
    public void resign(String authToken, Integer gameID) {
        try {
            ResignCommand resignCommand = new ResignCommand(authToken, gameID);
            this.send(new Gson().toJson(resignCommand));
        }catch (Exception ex){
            System.out.println("Error: Unable to make connection to server");
        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }
}
