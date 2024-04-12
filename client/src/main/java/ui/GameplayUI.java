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
import java.util.Collection;
import java.util.Scanner;

public class GameplayUI implements NotificationHandler {
    Integer gameID = null;
    ChessGame currentGame;
    ChessGame.TeamColor playerColor;
    String authToken;
    WebSocketFacade ws = new WebSocketFacade(Repl.serverUrl, this);
    BoardPrinter boardPrinter;

    public GameplayUI(String serverUrl) throws ResponseException {

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
                case "showmoves" -> showMoves(params);
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
            return boardPrinter.printSide(currentGame, true, null).toString();
        }else {
            return boardPrinter.printSide(currentGame, false, null).toString();
        }
    }
    public String leaveGame() throws Exception {
        ws.leave(authToken, gameID, playerColor);
        Repl.state = Repl.State.SIGNEDIN;
        return "You have left the game";
    }
    public String makeMove(String[] params) {
        if (params.length != 4) {
            return "Please enter your move as <startRow> <startColumn> <endRow> <endColumn>";
        } else if (playerColor == null) {
            return "Cannot make move as an observer.";
        }

        // Helper method to convert column letter to 1-based integer string
        String startColumnStr = letterToColumn(params[1]);
        ChessMove move = getChessMove(params, startColumnStr);

        // Assuming ws.makeMove(authToken, gameID, playerColor, move) is valid
        ws.makeMove(authToken, gameID, move);

        return "";
    }

    private ChessMove getChessMove(String[] params, String startColumnStr) {
        String endColumnStr = letterToColumn(params[3]);

        // Parse the integer values for rows
        int startRow = Integer.parseInt(params[0]);
        int endRow = Integer.parseInt(params[2]);

        // Create ChessMove with converted positions
        ChessPosition startPosition = new ChessPosition(startRow, Integer.parseInt(startColumnStr));
        ChessPosition endPosition = new ChessPosition(endRow, Integer.parseInt(endColumnStr));
        return new ChessMove(startPosition, endPosition, null);
    }

    // Helper method to convert column letter ('a' to 'h') to 1-based integer string ("1" to "8")
    private String letterToColumn(String letter) {
        return switch (letter) {
            case "a" -> "1";
            case "b" -> "2";
            case "c" -> "3";
            case "d" -> "4";
            case "e" -> "5";
            case "f" -> "6";
            case "g" -> "7";
            case "h" -> "8";
            default -> "Invalid column letter. Use letters 'a' to 'h'.";
        };
    }

    public String resign() {
        if(currentGame.gameOver){
            return "This game is already over.";
        }
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
        return "You have resigned from the game.";
    }

    public String showMoves(String[] params){
        if (params.length != 2){
            return "Please enter in: showMoves <row> <column>";
        }
        ChessPosition position = new ChessPosition(Integer.parseInt(params[0]), Integer.parseInt(letterToColumn(params[1])));
        Collection<ChessMove> validMoves = currentGame.validMoves(position);
        if (currentGame.getBoard().getPiece(position).getTeamColor() == ChessGame.TeamColor.WHITE) {
            return boardPrinter.printSide(currentGame, false, validMoves).toString();
        }else {
            return boardPrinter.printSide(currentGame, true, validMoves).toString();
        }
//        return null;
    }
    public void loadGame(LoadGameMessage msg) {
        this.currentGame = msg.getGame();
        if (playerColor == ChessGame.TeamColor.BLACK){
            System.out.println(boardPrinter.printSide(currentGame, true, null));
        }else if (playerColor == ChessGame.TeamColor.WHITE) {
            System.out.println(boardPrinter.printSide(currentGame, false, null));
        }else {
            boardPrinter.printChessboard(currentGame);
        }
    }
    public void broadcast(NotificationMessage msg){
        redrawBoard();
        System.out.println(msg.getMessage());
    }

    public void sendError(ErrorMessage msg){
        System.out.println(msg.getErrorMessage());
    }
}
