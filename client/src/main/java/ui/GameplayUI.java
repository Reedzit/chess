package ui;

import exception.ResponseException;
import webSocket.NotificationHandler;
import webSocket.WebSocketFacade;
import webSocketMessages.serverMessages.LoadGameMessage;
import webSocketMessages.serverMessages.ServerMessage;

import java.util.Arrays;
import java.util.Scanner;

public class GameplayUI implements NotificationHandler {
    Integer gameID = null;
    WebSocketFacade ws;

    public GameplayUI(String serverUrl) throws ResponseException {
        ws = new WebSocketFacade(serverUrl, this);
    }
    @Override
    public void notify(ServerMessage message) {
        switch (message.getServerMessageType()) {
            case LOAD_GAME -> loadGame(message)
            // if LOADGAME do something
            // if notification do something
        }
    }

    public static void main(String[] args) throws ResponseException {
        GameplayUI ui = new GameplayUI("");
        Scanner scanner = new Scanner(System.in);
        String result = "";
        while (!result.equals("quit")){
            String line = scanner.nextLine();
            result = ui.eval(line);
        }


        /**
         * if userInput == "help"
         * ui.printHelp();
         */
    }
    public String eval(String input){
        var tokens = input.toLowerCase().split(" ");
        var cmd = (tokens.length > 0) ? tokens[0] : "help";
        var params = Arrays.copyOfRange(tokens, 1, tokens.length);
        try {
            return switch (cmd) {
                case "redraw" -> redrawBoard();
                case "leave" -> leaveGame();
                case "move" -> makeMove(params);
                case "resign" -> resign();
                case "showMoves" -> showMoves();
                default -> printHelp();
            };
        }catch (Exception e) {
            return e.getMessage();
        }
    }

    public String printHelp() {
        return """
                redraw - redraws chessboard
                leave - leave game
                move <StartPosition> <EndPosition> - a game
                resign - resign from game
                showMoves - highlights legal moves
                help - list possible commands
                """;
    }

    public String joinGame(){}

    public String redrawBoard(){

    }
    public String leaveGame() {

    }
    public String makeMove(String[] params) {

    }
    public String resign() {
        System.out.println("\nAre you sure you want to resign? (yes/no)\n");
        Scanner scanner = new Scanner(System.in);
        String line = "";
        while (!line.equals("yes")) {
            line = scanner.nextLine();
            if (line.equals("no")) {
                return redrawBoard();
            }
        }
        //delete game
        Repl.state = Repl.State.SIGNEDIN;
        return ""
    }

    public String showMoves(){
        return "";
    }
    public void loadGame(LoadGameMessage msg) {

    }

    //print out boards

    // notifications about the game
}
