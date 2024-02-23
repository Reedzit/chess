package service;

import chess.ChessGame;
import dataAccess.MemoryAuthDAO;
import dataAccess.MemoryGameDAO;
import model.GameData;

import java.util.List;

public class GameService {
    public int createGame(String gameName, String token){
//        new MemoryGameDAO().listGames().contains();
        return 1;
    }
    public void joinGame(ChessGame.TeamColor clientColor, int gameID, String authToken){

    }
    public List<GameData> getGameList(String authToken){
        if (new MemoryAuthDAO().getAuth(authToken) != null){
            return new MemoryGameDAO().listGames();
        }
        return null; // throw error unauthorized
    }
}
