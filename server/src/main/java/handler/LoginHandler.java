package handler;

import com.google.gson.Gson;
import model.LoginData;
import spark.Request;
import spark.Response;
import spark.Route;
import service.UserService;

public class LoginHandler implements Route {
    @Override
    public Object handle(Request request, Response response) throws Exception {
        LoginData loginInfo = new Gson().fromJson(request.body(), LoginData.class);
        return new Gson().toJson(new UserService().login(loginInfo));
    }
}
