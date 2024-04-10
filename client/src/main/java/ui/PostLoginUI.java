package ui;

import chess.ChessBoard;
import com.google.gson.Gson;
import exception.ResponseException;
import webSocket.WebSocketFacade;

import java.util.Arrays;

public class PostLoginUI {
    public ServerFacade server;
    private final  String serverURL;


    public PostLoginUI(ServerFacade server, String serverURL) throws ResponseException {
        this.server = server;
        this.serverURL = serverURL;
    }
    public String eval(String input) {
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

    public String help() {
        return """
                create <NAME> - a game
                list - games
                join <ID> [WHITE|BLACK|<empty>] - a game
                observe <ID> - a game
                logout - when you are done
                quit - playing chess
                help - list possible commands
                """;
    }
    public String createGame(String... params) throws ResponseException {
        if (params.length != 1){
            return "Expected: <NAME>\n";
        }
        server.createGame(params[0]);
        return String.format("You have created the game '%s'. \n", params[0]);
    }
    public String listGames() throws ResponseException {
        var games = server.listGames();
        var result = new StringBuilder();
        var gson = new Gson();
        for (var game : games) {
            result.append(gson.toJson(game)).append('\n');
        }
        return result.toString();
    }
    public String joinGame(String... params) throws ResponseException {
        if (params.length != 2){
            return "Expected: <ID> [WHITE|BLACK|<empty>] \n";
        }
        server.joinGame(params);
        Repl.state = Repl.State.GAMEPLAY;

        //call websocket facade join game
        Repl.gameplayUI.gameID = Integer.parseInt(params[0]);
        Repl.gameplayUI.authToken = server.authToken;
        return String.format("You have joined the game %s. \n", params[0]);
    }
    public String observeGame(String... params) throws ResponseException {
        if (params.length != 1){
            return "Expected: <ID>\n";
        }
        server.observeGame(params);
        Repl.state = Repl.State.GAMEPLAY;
        BoardPrinter.main(new String[]{});
        return String.format("You are observing the game %s. \n", Integer.parseInt(params[0]));
    }
    public String logout() throws ResponseException {
        Repl.state = Repl.State.SIGNEDOUT;
        server.logout();
        return "You have logged out.\n";
    }
}
