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
import java.util.zip.CheckedOutputStream;

public class GameService {
    public CreateGameResponse createGame(String gameName, String authToken){
//        System.out.println("This is the gameName for createGame" + gameName);
//        System.out.println("This is the game from the DAO: " + new MemoryGameDAO().getGame(gameName));
        CreateGameResponse response = new CreateGameResponse(null, null);
        if (new MemoryAuthDAO().getAuth(authToken) == null){
//            System.out.println("unauthorized");
            response = new CreateGameResponse(null, "Error: unauthorized");
        }else if (gameName == null || new MemoryGameDAO().getGame(gameName) != null){
            response = new CreateGameResponse(null, "Error: bad request");
        }else {
//            System.out.println("this shows that the game is being made");
            response = new CreateGameResponse(new MemoryGameDAO().createGame(gameName), null);
//            System.out.println("This is the response" + response);
        }
//        System.out.println("This is the response for createGame: " + response);
        return response;
    }
    public EmptyResponse joinGame(ChessGame.TeamColor clientColor, Integer gameID, String authToken) {
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
        }else if (clientColor != null){
            GameData oldGame = gameDAO.getGame(gameDAO.getGameName(gameID));
            GameData updatedGame;
            if (clientColor == ChessGame.TeamColor.WHITE){
                updatedGame = new GameData(oldGame.gameID(), new MemoryAuthDAO().getUsername(authToken), oldGame.blackUsername(), oldGame.gameName(), oldGame.game());
            }else{
                updatedGame = new GameData(oldGame.gameID(), oldGame.whiteUsername(), new MemoryAuthDAO().getUsername(authToken), oldGame.gameName(), oldGame.game());
            }
            gameDAO.updateGame(updatedGame);

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
