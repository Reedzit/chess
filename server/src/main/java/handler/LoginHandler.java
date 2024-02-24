package handler;

import com.google.gson.Gson;
import org.eclipse.jetty.server.Authentication;
import requests.LoginRequest;
import requests.LoginRequest;
import responses.LoginResponse;
import spark.Request;
import spark.Response;
import spark.Route;
import service.UserService;

public class LoginHandler implements Route {
    @Override
    public Object handle(Request request, Response response) throws Exception {
        LoginRequest loginRequest = new Gson().fromJson(request.body(), LoginRequest.class);
        LoginResponse loginResponse = new UserService().login(loginRequest);
        switch (loginResponse.message()){
            case null -> response.status(200);
            case "Error: unauthorized" -> response.status(401);
            default -> response.status(500);
        }
        return new Gson().toJson(loginResponse);
    }
}
