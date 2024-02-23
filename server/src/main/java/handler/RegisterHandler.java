package handler;

import com.google.gson.Gson;
import model.AuthData;
import model.UserData;
import service.UserService;
import spark.Request;
import spark.Response;
import spark.Route;

public class RegisterHandler implements Route {

    @Override
    public Object handle(Request request, Response response) throws Exception {
        UserData user = new Gson().fromJson(request.body(), UserData.class);
        String authToken = new UserService().register(user);
        return new Gson().toJson(authToken);
    }
}
