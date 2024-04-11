package webSocketMessages.userCommands;

import chess.ChessGame;
import chess.ChessMove;

public class MakeMoveCommand extends UserGameCommand{
    ChessMove move;
    Integer gameID;
    ChessGame.TeamColor teamColor;
    public MakeMoveCommand(String authToken, Integer gameID, ChessGame.TeamColor teamColor, ChessMove move) {
        super(authToken);
        this.gameID = gameID;
        this.move = move;
        this.commandType = CommandType.MAKE_MOVE;
        this.teamColor = teamColor;
    }

    public ChessMove getMove() {
        return move;
    }

    public Integer getGameID() {
        return gameID;
    }
    public ChessGame.TeamColor getTeamColor(){
        return teamColor;
    }
}
