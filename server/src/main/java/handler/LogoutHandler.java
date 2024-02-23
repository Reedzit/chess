package handler;

import com.google.gson.Gson;
import responses.LogoutResponse;
import service.UserService;
import spark.Request;
import spark.Response;
import spark.Route;

public class LogoutHandler implements Route {

    @Override
    public Object handle(Request request, Response response) throws Exception {
        String authToken = request.headers("authorization");
        LogoutResponse logoutResponse = new UserService().logout(authToken);
        switch (logoutResponse.message()){
            case "" -> response.status(200);
            case "Error: unauthorized" -> response.status(401);
            default -> response.status(500);
        }
        return new Gson().toJson(logoutResponse);
    }
}
