
import java.util.Scanner;

public class Repl {
    private final ChessClient client;

    public Repl(String serverUrl){
        client = new ChessClient(serverUrl); // will add this as a param after ws is implemented
    }

    public void run(){
        System.out.println("\uD83D\uDC36 Welcome to Chess. Type help to begin.");
        System.out.println(client.help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while(!result.equals("logout")){
            printPrompt();
            String line = scanner.nextLine();
            try {
                result = client.eval(line);
                System.out.print(result);
            } catch (Throwable e){
                var msg = e.toString();
                System.out.println(msg);
            }
        }
    }

    private void printPrompt() {
        System.out.print("\n" + ">>> ");
    }
}
