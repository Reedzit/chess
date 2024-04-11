package ui;

import exception.ResponseException;

import java.util.Scanner;

public class Repl {
    private final PostLoginUI postLoginUI;
    public PreLoginUI preLoginUI;
    public static State state = State.SIGNEDOUT;
    public static GameplayUI gameplayUI;
    public static String serverUrl;

    public Repl(String serverUrl) throws ResponseException {
        Repl.serverUrl = serverUrl;
        preLoginUI = new PreLoginUI(serverUrl);
        postLoginUI = new PostLoginUI(PreLoginUI.server, serverUrl);
        gameplayUI = new GameplayUI(serverUrl);
    }

    public void run() {
        System.out.println(" ♛  Welcome to Chess. Type help to begin.");
        System.out.println(PreLoginUI.help());
        Scanner scanner = new Scanner(System.in);
        var result = "";
        while(!result.equals("quit")){
            printPrompt();
            String line = scanner.nextLine();
            if (state == State.SIGNEDIN){
                result = postLoginUI.eval(line);
                System.out.print(result);
            }else if(state == State.SIGNEDOUT) {
                result = preLoginUI.eval(line);
                System.out.print(result);
            }else {
                result = gameplayUI.eval(line);
                System.out.println(result);
            }

        }
    }

    public enum State{
        SIGNEDIN, SIGNEDOUT, GAMEPLAY
    }

    private void printPrompt() {
        System.out.print("\n" + ">>> ");
    }
}
