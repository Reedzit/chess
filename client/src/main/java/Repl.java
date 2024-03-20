import com.sun.nio.sctp.HandlerResult;
import com.sun.nio.sctp.Notification;
import com.sun.nio.sctp.NotificationHandler;

import java.util.Scanner;

import static ui.EscapeSequences.*;

public class Repl implements NotificationHandler {
    private final ChessClient client;

    public Repl(String serverUrl){
        client = new ChessClient(serverUrl, this);
    }

    public void run(){
        System.out.println("\uD83D\uDC36 Welcome to Chess. Sign in to play.");
        System.out.println(client.help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while(!result.equals("logout")){
//            printPrompt();
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

//    private void printPrompt() {
//        System.out.print("\n" +  + ">>> " + )
//    }
}
