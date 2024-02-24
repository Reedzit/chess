package handler;

import chess.ChessGame;
import com.google.gson.Gson;
import dataAccess.MemoryAuthDAO;
import dataAccess.MemoryUserDAO;
import responses.GameListResponse;
import service.GameService;
import spark.Request;
import spark.Response;
import spark.Route;

public class ListGameHandler implements Route {
    @Override
    public Object handle(Request request, Response response) throws Exception {
        String authToken = request.headers("authorization");
        GameListResponse gameListResponse = new GameService().getGameList(authToken);
        switch (gameListResponse.message()){
            case null -> response.status(200);
            case "Error: unauthorized" -> response.status(401);
            default -> response.status(500);
        }
        return new Gson().toJson(gameListResponse);
    }
}
