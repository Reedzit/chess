package webSocketMessages.serverMessages;

import chess.ChessBoard;
import chess.ChessGame;

public class LoadGameMessage extends ServerMessage{
    ChessBoard game;
    public LoadGameMessage(ServerMessageType type) {
        super(type);
    }
}
