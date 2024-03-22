import com.google.gson.Gson;

import java.util.Arrays;


public class ChessClient {
    private final ServerFacade server;
    private final String serverUrl;
    private State state = State.SIGNEDOUT;

    public ChessClient(String serverUrl, Repl ) {
        server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
    }
    public String signIn(String[] stuff){
        state = State.SIGNEDIN;
    }
    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            if (state != State.SIGNEDIN){
                return switch (cmd) {
                    case "signin" -> signIn(params);
                    case "register" -> register(params);
                    case "quit" -> "quit";
                    default -> help();
                };
            }else {
                 return switch (cmd) {
                     case "logout" -> "logout";
                     case "create" -> createGame(params);
                     case "list" -> listGames(params);
                     case "join" -> joinGame(params);
                     case "observe" -> observeGame(params);
                     case
                     default -> help();
                 }
            }

        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }
    private enum State{
        SIGNEDIN, SIGNEDOUT
    }

    public String signIn(String ... params){

    }
    public String help(){
        if (state == State.SIGNEDIN){
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
        return """
                     register <USERNAME> <PASSWORD> <EMAIL> - to create an account
                     login <USERNAME> <PASSWORD> - to play chess
                     quit - playing chess
                     help - list possible commands
                     """;
    }
}
