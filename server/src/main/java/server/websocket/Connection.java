package server.websocket;
import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import webSocketMessages.serverMessages.*;
import java.io.IOException;

public class Connection {
    String authToken;
    Session session;
    public Connection(String authToken, Session session) {
        this.authToken = authToken;
        this.session = session;
    }

    public String getAuthToken() {
        return authToken;
    }

    public Session getSession() {
        return session;
    }
    public void send(ServerMessage msg) {
        try {
            session.getRemote().sendString(new Gson().toJson(msg));
        }catch (IOException ex){
            System.out.printf("Error: unable to make connection. this is the error message: %s", ex.getMessage());
        }
    }
}
