package ui;
import java.util.Arrays;
import java.util.Objects;

public class PreLoginUI {
    public static ServerFacade server;

    public PreLoginUI(String serverURL){
        server = new ServerFacade(serverURL);
    }
    public static String eval(String input) {
        var tokens = input.toLowerCase().split(" ");
        var cmd = (tokens.length > 0) ? tokens[0] : "help";
        var params = Arrays.copyOfRange(tokens, 1, tokens.length);
        try {
            return switch (cmd) {
                case "login" -> login(params);
                case "register" -> register(params);
                case "quit" -> "quit";
                default -> help();
            };
        }catch (Exception e){
            return e.getMessage();
        }
    }
    public static String login(String... params) {
        try {
            server.login(params);
            Repl.state = Repl.State.SIGNEDIN;
            return String.format("You are logged in as %s", params[0]);
        } catch (Exception e){
            return "Invalid username or password";
        }
    }
    public static String register(String... params) {
        try {
            server.register(params);
            Repl.state = Repl.State.SIGNEDIN;
            return String.format("You have registered as %s", params[0]);
        }catch (Exception e){
//            System.out.println(e.getMessage());
            if (Objects.equals(e.getMessage(), "failure: 403")){
                return "This user is already registered. Please login.";
            }
            return "Expected: <USERNAME> <PASSWORD> <EMAIL>";
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
