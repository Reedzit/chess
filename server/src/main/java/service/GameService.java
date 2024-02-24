package service;

import chess.ChessGame;
import dataAccess.MemoryAuthDAO;
import dataAccess.MemoryGameDAO;
import model.GameData;
import responses.CreateGameResponse;
import responses.GameListResponse;

import java.util.List;

public class GameService {
    public CreateGameResponse createGame(String gameName, String authToken){
        CreateGameResponse response = new CreateGameResponse(null, null);
        if (new MemoryAuthDAO().getAuth(authToken) == null){
            response = new CreateGameResponse(null, "Error: unauthorized");
        }else if (new MemoryGameDAO().getGame(gameName) != null){
            response = new CreateGameResponse(null, "Error: bad request");
        }else {
            response = new CreateGameResponse(new MemoryGameDAO().createGame(gameName), null);
        }
        return response;
    }
    public void joinGame(ChessGame.TeamColor clientColor, int gameID, String authToken){

    }
    public GameListResponse getGameList(String authToken){
        GameListResponse response = new GameListResponse(null, "Error: unauthorized"); // only one error so initialize the error
        if (new MemoryAuthDAO().getAuth(authToken) != null){
            response = new GameListResponse(new MemoryGameDAO().listGames(), null);
        }
        return response;
    }
}
