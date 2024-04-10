package webSocketMessages.userCommands;

import chess.ChessGame;

public class LeaveCommand extends UserGameCommand{
    Integer gameID;
    ChessGame.TeamColor playerColor;
    public LeaveCommand(String authToken, Integer gameID, ChessGame.TeamColor playerColor) {
        super(authToken);
        this.gameID = gameID;
        this.commandType = CommandType.LEAVE;
        this.playerColor = playerColor;
    }

    public Integer getGameID() {
        return gameID;
    }
    public ChessGame.TeamColor getPlayerColor() {
        return playerColor;
    }
}
