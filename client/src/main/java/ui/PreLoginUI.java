package ui;

import exception.ResponseException;
import java.util.Arrays;

public class PreLoginUI {
    private static ServerFacade server;
    private final String serverUrl;

    public PreLoginUI(String serverURL){
        server = new ServerFacade(serverURL);
        this.serverUrl = serverURL;
    }
    public static String eval(String input) throws ResponseException {
        var tokens = input.toLowerCase().split(" ");
        var cmd = (tokens.length > 0) ? tokens[0] : "help";
        var params = Arrays.copyOfRange(tokens, 1, tokens.length);
        return switch (cmd) {
            case "login" -> login(params);
            case "register" -> register(params);
            case "quit" -> "quit";
            default -> help();
        };
    }
    public static String login(String... params) throws ResponseException {
        Repl.state = Repl.State.SIGNEDIN;
        try {
            server.login(params);
            return String.format("You are logged in as %s", params[0]);
        } catch (Exception e){
            throw new ResponseException(400, "Expected: <USERNAME> <PASSWORD>");
        }
    }
    public static String register(String... params) throws ResponseException {
        Repl.state = Repl.State.SIGNEDIN;
        try {
            server.register(params);
            return String.format("You have registered as %s", params[0]);
        }catch (Exception e){
            throw new ResponseException(400, "Expected: <USERNAME> <PASSWORD> <EMAIL>");
        }
    }

    public static String help(){
        return """
                     register <USERNAME> <PASSWORD> <EMAIL> - to create an account
                     login <USERNAME> <PASSWORD> - to play chess
                     quit - playing chess
                     help - list possible commands
                     """;
    }
}
