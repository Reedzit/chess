package ui;

import exception.ResponseException;
import webSocket.NotificationHandler;
import webSocketMessages.serverMessages.ServerMessage;

import java.util.Scanner;

public class Repl {
    private final PreLoginUI preLoginUI;
    private final PostLoginUI postLoginUI;
    public static State state = State.SIGNEDOUT;
    public GameplayUI gameplayUI;

    public Repl(String serverUrl) throws ResponseException {
        preLoginUI = new PreLoginUI(serverUrl);
        postLoginUI = new PostLoginUI(PreLoginUI.server, serverUrl);
        gameplayUI = new GameplayUI();
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
                result = gameplayUI.eval();
                gameplayUI.joinGame()
            }
            //implement state.gameplay
        }
    }

    public enum State{
        SIGNEDIN, SIGNEDOUT, GAMEPLAY
    }

    private void printPrompt() {
        System.out.print("\n" + ">>> ");
    }
}
