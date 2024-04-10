package webSocketMessages.userCommands;

import chess.ChessMove;

public class MakeMoveCommand extends UserGameCommand{
    ChessMove move;
    Integer gameID;
    public MakeMoveCommand(String authToken, Integer gameID, ChessMove move) {
        super(authToken);
        this.gameID = gameID;
        this.move = move;
    }
}
