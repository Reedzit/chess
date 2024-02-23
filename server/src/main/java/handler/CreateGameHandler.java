package handler;

import com.google.gson.Gson;
import service.GameService;
import spark.Request;
import spark.Response;
import spark.Route;

public class CreateGameHandler implements Route {
    @Override
    public Object handle(Request request, Response response) throws Exception {
        String gameName = new Gson().fromJson(request.body(), String.class);
        String authToken = request.headers("authorization");
        return new Gson().toJson(new GameService().createGame(gameName,authToken));
    }
}
