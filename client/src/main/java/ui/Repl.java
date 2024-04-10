package ui;

import exception.ResponseException;

import java.util.Scanner;

public class Repl {
    private final PostLoginUI postLoginUI;
    public static State state = State.SIGNEDOUT;
    public GameplayUI gameplayUI;

    public Repl(String serverUrl) throws ResponseException {
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
                result = PreLoginUI.eval(line);
                System.out.print(result);
            }else {
                if (gameplayUI.gameID == null) {
                    gameplayUI.joinGame();
                }
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
