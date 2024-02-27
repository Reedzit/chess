package handler;

import com.google.gson.Gson;
import requests.CreateGameRequest;
import responses.CreateGameResponse;
import service.GameService;
import spark.Request;
import spark.Response;
import spark.Route;

public class CreateGameHandler implements Route {
    @Override
    public Object handle(Request request, Response response) throws Exception {
        System.out.println("DEBUG: " + request.body());
        CreateGameRequest createGameRequest = new Gson().fromJson(request.body(), CreateGameRequest.class);
        String authToken = request.headers("authorization");
        System.out.println("This is the authToken for create game: "+ authToken);
        CreateGameResponse createGameResponse = new GameService().createGame(createGameRequest.gameName(), authToken);
        switch (createGameResponse.message()){
            case null -> response.status(200);
            case "Error: bad request" -> response.status(400);
            case "Error: unauthorized" -> response.status(401);
            default -> response.status(500);
        }
        return new Gson().toJson(createGameResponse);
    }
}
