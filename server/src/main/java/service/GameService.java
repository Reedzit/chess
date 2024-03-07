package service;

import chess.ChessGame;
import dataAccess.*;
import model.GameData;
import responses.CreateGameResponse;
import responses.EmptyResponse;
import responses.GameListResponse;

public class GameService {
    AuthDAO authDAO = new DbAuthDAO();
    GameDAO gameDAO = new DbGameDAO();

    public GameService() throws DataAccessException {
    }

    public CreateGameResponse createGame(String gameName, String authToken) throws DataAccessException {
        CreateGameResponse response;
        if (authDAO.getAuth(authToken) == null){
            response = new CreateGameResponse(null, "Error: unauthorized");
        }else if (gameName == null || gameDAO.getGame(gameName) != null){
            response = new CreateGameResponse(null, "Error: bad request");
        }else {
            response = new CreateGameResponse(gameDAO.createGame(gameName), null);
        }
        return response;
    }
    public EmptyResponse joinGame(ChessGame.TeamColor clientColor, Integer gameID, String authToken) throws DataAccessException {
        EmptyResponse response = new EmptyResponse(null);
        if (authDAO.getAuth(authToken) == null){
            response = new EmptyResponse("Error: unauthorized");
        }else if (gameDAO.getGameName(gameID) == null){
            response = new EmptyResponse("Error: bad request");
        }else if (gameDAO.getGame(gameDAO.getGameName(gameID)).blackUsername() != null && clientColor == ChessGame.TeamColor.BLACK){
            response = new EmptyResponse("Error: already taken");
        }else if (gameDAO.getGame(gameDAO.getGameName(gameID)).whiteUsername() != null && clientColor == ChessGame.TeamColor.WHITE){
            response = new EmptyResponse("Error: already taken");
        }else if (clientColor != null){
            GameData oldGame = gameDAO.getGame(gameDAO.getGameName(gameID));
            GameData updatedGame;
            if (clientColor == ChessGame.TeamColor.WHITE){
                updatedGame = new GameData(oldGame.gameID(), authDAO.getUsername(authToken), oldGame.blackUsername(), oldGame.gameName(), oldGame.game());
            }else{
                updatedGame = new GameData(oldGame.gameID(), oldGame.whiteUsername(), authDAO.getUsername(authToken), oldGame.gameName(), oldGame.game());
            }
            gameDAO.updateGame(updatedGame);

        }
        return response;
    }
    public GameListResponse getGameList(String authToken) throws DataAccessException {
        GameListResponse response = new GameListResponse(null, "Error: unauthorized"); // only one error so initialize the error
        if (authDAO.getAuth(authToken) != null){
            response = new GameListResponse(gameDAO.listGames(), null);
        }
        return response;
    }
}
