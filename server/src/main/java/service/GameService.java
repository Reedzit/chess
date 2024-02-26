package service;

import chess.ChessGame;
import dataAccess.DataAccessException;
import dataAccess.MemoryAuthDAO;
import dataAccess.MemoryGameDAO;
import model.GameData;
import responses.CreateGameResponse;
import responses.EmptyResponse;
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
    public EmptyResponse joinGame(ChessGame.TeamColor clientColor, Integer gameID, String authToken) throws DataAccessException {
        EmptyResponse response = new EmptyResponse(null);
        MemoryGameDAO gameDAO = new MemoryGameDAO();
        if (new MemoryAuthDAO().getAuth(authToken) == null){
            response = new EmptyResponse("Error: unauthorized");
        }else if (new MemoryGameDAO().getGameName(gameID) == null){
            response = new EmptyResponse("Error: bad request");
        }else if (new MemoryGameDAO().getGame(gameDAO.getGameName(gameID)).blackUsername() != null && clientColor == ChessGame.TeamColor.BLACK){
            response = new EmptyResponse("Error: already taken");
        }else if (new MemoryGameDAO().getGame(gameDAO.getGameName(gameID)).whiteUsername() != null && clientColor == ChessGame.TeamColor.WHITE){
            response = new EmptyResponse("Error: already taken");
        }else{
            GameData oldGame = gameDAO.getGame(gameDAO.getGameName(gameID));
            if (clientColor == ChessGame.TeamColor.WHITE){
                GameData updatedGame = new GameData(oldGame.gameID(), new MemoryAuthDAO().getUsername(authToken),oldGame.blackUsername(),oldGame.gameName(),oldGame.game());
            }else{
                GameData updatedGame = new GameData(oldGame.gameID(), oldGame.whiteUsername(), new MemoryAuthDAO().getUsername(authToken),oldGame.gameName(),oldGame.game());
            }
        }
        return response;
    }
    public GameListResponse getGameList(String authToken){
        GameListResponse response = new GameListResponse(null, "Error: unauthorized"); // only one error so initialize the error
        if (new MemoryAuthDAO().getAuth(authToken) != null){
            response = new GameListResponse(new MemoryGameDAO().listGames(), null);
        }
        return response;
    }
}
