package ui;

import java.util.Scanner;

public class Repl {
    private final PreLoginUI preLoginUI;
    private final PostLoginUI postLoginUI;
    public static State state = State.SIGNEDOUT;

    public Repl(String serverUrl){
        preLoginUI = new PreLoginUI(serverUrl);
        postLoginUI = new PostLoginUI(PreLoginUI.server);
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
