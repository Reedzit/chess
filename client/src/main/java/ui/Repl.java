package ui;

import java.util.Scanner;

import exception.ResponseException;
import ui.EscapeSequences;
import ui.PostLoginUI;
import ui.PreLoginUI;

public class Repl {
    private final PreLoginUI preLoginUI;
    private final PostLoginUI postLoginUI;
    public static State state = State.SIGNEDOUT;

    public Repl(String serverUrl){
        preLoginUI = new PreLoginUI(serverUrl); // will add this as a param after ws is implemented
        postLoginUI = new PostLoginUI(serverUrl);
    }

    public void run() throws ResponseException {
        System.out.println("\uD83D\uDC36 Welcome to Chess. Type help to begin.");
        System.out.println(PreLoginUI.help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while(!result.equals("quit")){
            printPrompt();
            String line = scanner.nextLine();
            if (state == State.SIGNEDIN){
                result = PostLoginUI.eval(line);
                System.out.print(result);
            }else {
            result = PreLoginUI.eval(line);
            System.out.print(result);
            }
        }
    }

    public enum State{
        SIGNEDIN, SIGNEDOUT
    }

    private void printPrompt() {
        System.out.print("\n" + ">>> ");
    }
}
