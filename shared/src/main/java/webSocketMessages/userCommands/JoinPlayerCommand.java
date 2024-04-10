package webSocketMessages.userCommands;

import chess.ChessGame;

public class JoinPlayerCommand extends UserGameCommand {
    Integer gameID;
    ChessGame.TeamColor playerColor;
    public JoinPlayerCommand(String authToken, Integer gameID, ChessGame.TeamColor playerColor) {
        super(authToken);
        this.gameID = gameID;
        this.playerColor = playerColor;
        this.commandType = CommandType.JOIN_PLAYER;

    }
    public Integer getGameID(){ return this.gameID;}

    public ChessGame.TeamColor getPlayerColor() {
        return playerColor;
    }
}
