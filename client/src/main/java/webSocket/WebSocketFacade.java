package webSocket;

import com.google.gson.Gson;
import exception.ResponseException;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import webSocketMessages.serverMessages.ServerMessage;
import webSocketMessages.userCommands.UserGameCommand;

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
                    ServerMessage serverMessage = new Gson().fromJson(msg, ServerMessage.class);
                    notificationHandler.notify(serverMessage);
                }
            });
        } catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }
    public void send(String msg) throws Exception {
        this.session.getBasicRemote().sendText(msg); // call this after every method
    }


    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {

    }
    public void makeMove() throws ResponseException {
        try{
            var moveCommand = new MakeMoveCommand();

        } catch (Exception ex){
            throw new ResponseException(500, ex.getMessage());
        }
    }
    public void joinPlayer(Integer gameID) {

    }
    public void joinObserver(Integer gameID) {

    }
}
