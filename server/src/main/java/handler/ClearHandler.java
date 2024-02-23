package handler;

import dataAccess.MemoryAuthDAO;
import dataAccess.MemoryGameDAO;
import dataAccess.MemoryUserDAO;
import spark.Request;
import spark.Response;
import spark.Route;

public class ClearHandler implements Route {
    @Override
    public Object handle(Request request, Response response) throws Exception {
        new MemoryAuthDAO().clear();
        new MemoryUserDAO().clear();
        new MemoryGameDAO().clear();
        return null;
    }
}
