package webSocket;

import chess.ChessGame;
import chess.ChessMove;
import com.google.gson.Gson;
import exception.ResponseException;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import webSocketMessages.serverMessages.ErrorMessage;
import webSocketMessages.serverMessages.LoadGameMessage;
import webSocketMessages.serverMessages.NotificationMessage;
import webSocketMessages.serverMessages.ServerMessage;
import webSocketMessages.userCommands.*;

public class WebSocketFacade {
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
                    ServerMessage updatedMessage;
                    ServerMessage serverMessage = new Gson().fromJson(msg, ServerMessage.class);
                    switch(serverMessage.getServerMessageType()){
                        case NOTIFICATION -> notificationHandler.notify((NotificationMessage)serverMessage);
                        case ERROR -> notificationHandler.notify((ErrorMessage)serverMessage);
                        default -> notificationHandler.notify((LoadGameMessage)serverMessage);
                    }
                }
            });
        } catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }
    public void send(String msg) throws Exception {
        this.session.getBasicRemote().sendText(msg); // call this after every method
    }


//    @Override
//    public void onOpen(Session session, EndpointConfig endpointConfig) {
//
//    }
    public void makeMove(String authToken, Integer gameID, ChessMove move) throws ResponseException {
        try{
            var moveCommand = new MakeMoveCommand(authToken, gameID, move);
            this.send(new Gson().toJson(moveCommand));


        } catch (Exception ex){
            throw new ResponseException(500, ex.getMessage());
        }
    }
    public void joinPlayer(Integer gameID, String authToken, ChessGame.TeamColor playerColor) throws Exception {
        JoinPlayerCommand command = new JoinPlayerCommand(authToken,gameID, playerColor);
        this.send(new Gson().toJson(command));

    }
    public void joinObserver(Integer gameID, String authToken) throws Exception {
        JoinObserverCommand command = new JoinObserverCommand(authToken,gameID);
        this.send(new Gson().toJson(command));
    }
    public void leave(String authToken, Integer gameID, ChessGame.TeamColor playerColor) throws Exception {
        LeaveCommand command = new LeaveCommand(authToken, gameID, playerColor);
        this.send(new Gson().toJson(command));
    }
    public void resign(String authToken, Integer gameID) throws Exception {
        ResignCommand resignCommand = new ResignCommand(authToken, gameID);
        this.send(new Gson().toJson(resignCommand));
    }
}
