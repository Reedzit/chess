package handler;

import com.google.gson.Gson;
import requests.JoinGameRequest;
import responses.EmptyResponse;
import service.GameService;
import spark.Request;
import spark.Response;
import spark.Route;

public class JoinGameHandler implements Route {
    @Override
    public Object handle(Request request, Response response) throws Exception {
        String authToken = request.headers("authorization");
        JoinGameRequest joinGameRequest = new Gson().fromJson(request.body(), JoinGameRequest.class);
        EmptyResponse emptyResponse = new GameService().joinGame(joinGameRequest.playerColor(), joinGameRequest.gameID(), authToken);
        switch (emptyResponse.message()){
            case null -> response.status(200);
            case "Error: bad request" -> response.status(400);
            case "Error: unauthorized" -> response.status(401);
            case "Error: already taken" -> response.status(403);
            default -> response.status(500);
        }
        return new Gson().toJson(emptyResponse);
    }
}
