package ui;

import exception.ResponseException;
import webSocket.NotificationHandler;
import webSocket.WebSocketFacade;
import webSocketMessages.serverMessages.ServerMessage;

import java.util.Arrays;

public class GameplayUI implements NotificationHandler {



    WebSocketFacade ws;

    public GameplayUI() throws ResponseException {
        ws = new WebSocketFacade("", this);
    }
    @Override
    public void notify(ServerMessage message) {
        switch (message.getServerMessageType()) {
            // if LOADGAME do something
            // if notification do something
        }
    }

    public static void main(String[] args) throws ResponseException {
        GameplayUI ui = new GameplayUI();

        /**
         * if userInput == "help"
         * ui.printHelp();
         */
    }
    public String eval(){
        var tokens = input.toLowerCase().split(" ");
        var cmd = (tokens.length > 0) ? tokens[0] : "help";
        var params = Arrays.copyOfRange(tokens, 1, tokens.length);
        try {
            return switch (cmd) {
                case "create" -> createGame(params);
                case "list" -> listGames();
                case "join" -> joinGame(params);
                case "observe" -> observeGame(params);
                case "logout" -> logout();
                case "quit" -> "quit";
                default -> help();
            };
        }catch (Exception e) {
            return e.getMessage();
        }
    }

    void printHelp() {

    }




    //print out boards

    // notifications about the game
}
