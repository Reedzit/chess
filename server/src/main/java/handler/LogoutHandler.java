package handler;

import com.google.gson.Gson;
import responses.EmptyResponse;
import service.UserService;
import spark.Request;
import spark.Response;
import spark.Route;

public class LogoutHandler implements Route {

    @Override
    public Object handle(Request request, Response response) throws Exception {
        String authToken = request.headers("authorization");
        EmptyResponse logoutResponse = new UserService().logout(authToken);
        switch (logoutResponse.message()){
            case null -> response.status(200);
            case "Error: unauthorized" -> response.status(401);
            default -> response.status(500);
        }
        return new Gson().toJson(logoutResponse);
    }
}
