package handler;

import com.google.gson.Gson;
import dataAccess.MemoryAuthDAO;
import service.UserService;
import spark.Request;
import spark.Response;
import spark.Route;

public class LogoutHandler implements Route {

    @Override
    public Object handle(Request request, Response response) throws Exception {
        String authToken = new Gson().fromJson(request.body(), String.class);
        new UserService().logout(authToken);
        return new Gson().toJson();
    }
}
