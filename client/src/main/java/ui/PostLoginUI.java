package ui;

import com.google.gson.Gson;
import exception.ResponseException;

import java.util.Arrays;

public class PostLoginUI {
    private static ServerFacade server;

    public PostLoginUI(String serverURL) {
        server = new ServerFacade(serverURL);
    }
    public static String eval(String input) throws ResponseException {
        var tokens = input.toLowerCase().split(" ");
        var cmd = (tokens.length > 0) ? tokens[0] : "help";
        var params = Arrays.copyOfRange(tokens, 1, tokens.length);

        return switch (cmd) {
            case "create" -> createGame(params);
            case "list" -> listGames();
            case "join" -> joinGame(params);
            case "observe" -> observeGame(params);
            case "logout" -> logout();
            case "quit" -> "quit";
            default -> help();
        };
        }

    public static String help() {
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
    public static String createGame(String... params) throws ResponseException {
        server.createGame(params[0]);
        return String.format("You have created the game '%s'.", params[0]);
    }
    public static String listGames() throws ResponseException {
        var games = server.listGames();
        var result = new StringBuilder();
        var gson = new Gson();
        for (var game : games) {
            result.append(gson.toJson(game)).append('\n');
        }
        return result.toString();
    }
    public static String joinGame(String... params) throws ResponseException {
        server.joinGame(params);
        return String.format("You have joined the game '%s'.", params[0]);
    }
    public static String observeGame(String... params) throws ResponseException {
        server.observeGame(params);
        return String.format("You are observing the game '%s'.", Integer.parseInt(params[0]));
    }
    public static String logout() throws ResponseException {
        Repl.state = Repl.State.SIGNEDOUT;
        server.logout();
        return "You have logged out.";
    }
}
