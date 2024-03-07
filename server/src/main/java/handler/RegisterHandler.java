package handler;

import com.google.gson.Gson;
import model.UserData;
import responses.RegisterResponse;
import service.UserService;
import spark.Request;
import spark.Response;
import spark.Route;

public class RegisterHandler implements Route {

    @Override
    public Object handle(Request request, Response response) throws Exception {
        UserData user = new Gson().fromJson(request.body(), UserData.class);
        RegisterResponse registerResponse = new UserService().register(user);
        switch (registerResponse.message()) {
            case null -> response.status(200);
            case "Error: already taken" -> response.status(403);
            case "Error: bad request" -> response.status(400);
            default -> response.status(500);
        }
        return new Gson().toJson(registerResponse);
    }
}
