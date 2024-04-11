package ui;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import com.google.gson.Gson;
import exception.ResponseException;
import webSocket.NotificationHandler;
import webSocket.WebSocketFacade;
import webSocketMessages.serverMessages.ErrorMessage;
import webSocketMessages.serverMessages.LoadGameMessage;
import webSocketMessages.serverMessages.NotificationMessage;
import webSocketMessages.serverMessages.ServerMessage;

import java.util.Arrays;
import java.util.Scanner;

public class GameplayUI implements NotificationHandler {
    Integer gameID = null;
    ChessGame currentGame;
    ChessGame.TeamColor playerColor;
    String authToken;
    WebSocketFacade ws;
    BoardPrinter boardPrinter;

    public GameplayUI(String serverUrl) throws ResponseException {
        ws = new WebSocketFacade(serverUrl, this);
        boardPrinter = new BoardPrinter();
    }
    @Override
    public void notify(String message) {
        ServerMessage serverMessage = new Gson().fromJson(message, ServerMessage.class);
        switch (serverMessage.getServerMessageType()) {
            case LOAD_GAME -> { LoadGameMessage loadGameMessage = new Gson().fromJson(message, LoadGameMessage.class);
                loadGame(loadGameMessage);
            }
            case ERROR -> { ErrorMessage errorMessage = new Gson().fromJson(message, ErrorMessage.class);
                sendError(errorMessage);
            }
            case NOTIFICATION -> { NotificationMessage notification = new Gson().fromJson(message, NotificationMessage.class);
                broadcast(notification);
            }

        }
    }

    public static void main(String[] args) throws ResponseException {
        GameplayUI ui = new GameplayUI(Repl.serverUrl);
        Scanner scanner = new Scanner(System.in);
        String result = "";
        while (!result.equals("quit")){
            String line = scanner.nextLine();
            result = ui.eval(line);
        }

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
        if (playerColor != null) {
            return """
                    redraw - redraws chessboard
                    leave - leave game
                    move <startRow> <startColumn> <endRow> <endColumn> - a game
                    resign - resign from game
                    showMoves <row> <column> - highlights legal moves
                    help - list possible commands
                    """;
        } else {
            return """
                    redraw - redraws chessboard
                    leave - leave game
                    showMoves <row> <column> - highlights legal moves
                    help - list possible commands
                    """;
        }
    }

    public void joinPlayer() {
        ws.joinPlayer(gameID, authToken, playerColor);

    }
    public void joinObserver() {
        ws.joinObserver(gameID, authToken);
    }


    public String redrawBoard(){
        if (playerColor == null) {
            return boardPrinter.printChessboard(currentGame);
        }else if (playerColor == ChessGame.TeamColor.BLACK){
            return boardPrinter.printBlackSide(currentGame).toString();
        }else {
            return boardPrinter.printWhiteSide(currentGame).toString();
        }
    }
    public String leaveGame() throws Exception {
        ws.leave(authToken, gameID, playerColor);
        Repl.state = Repl.State.SIGNEDIN;
        return "You have left the game";
    }
    public String makeMove(String[] params) {
        if (params.length != 4){
            return "Please enter you move as <startRow> <startColumn> <endRow> <endColumn>";
        }else if (playerColor == null){
            return "Cannot make move as an observer.";
        }
        ChessPosition startPosition = new ChessPosition(Integer.parseInt(params[0]), Integer.parseInt(params[1]));
        ChessPosition endPosition = new ChessPosition(Integer.parseInt(params[2]), Integer.parseInt(params[3]));
        ChessMove move = new ChessMove(startPosition, endPosition, null); // how do I know what the promotion piece will be? do I check where they are?
        ws.makeMove(authToken, gameID, move);
        return "";
    }
    public String resign() throws Exception {
        System.out.println("\nAre you sure you want to resign? (yes/no)\n");
        Scanner scanner = new Scanner(System.in);
        String line = "";
        while (!line.equals("yes")) {
            line = scanner.nextLine();
            if (line.equals("no")) {
                return redrawBoard();
            }
        }//delete game
        ws.resign(authToken, gameID);
        Repl.state = Repl.State.SIGNEDIN;
        return "You have resigned from the game.";
    }

    public String showMoves(){
        return "";
    }
    public void loadGame(LoadGameMessage msg) {
        this.currentGame = msg.getGame();
        System.out.println("Game has been loaded");
    }
    public void broadcast(NotificationMessage msg){
        System.out.println(msg.getMessage());
    }

    public void sendError(ErrorMessage msg){
        System.out.println(msg.getErrorMessage());
    }
}
