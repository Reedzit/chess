package service;

import dataAccess.MemoryAuthDAO;
import dataAccess.MemoryGameDAO;
import dataAccess.MemoryUserDAO;
import model.*;

public class UserService {
    public String register(UserData user){
        if (new MemoryUserDAO().getUser(user.username()) == null) {
            new MemoryUserDAO().createUser(user);
            return new MemoryAuthDAO().createAuth(user.username());

        }
        return null;
    }
    public String login(LoginData loginObj){
        if (new MemoryUserDAO().getUser(loginObj.username()) != null){
            UserData userData = new MemoryUserDAO().getUser(loginObj.username());
            if (userData.password() != loginObj.password()){
                return null; //throw error 401 unauthorized
            }
            return new MemoryAuthDAO().createAuth(loginObj.username());
        }
        return null;
    }
    public void logout(String authToken){
        if (new MemoryAuthDAO().getAuth(authToken) != null){
            new MemoryAuthDAO().deleteAuth(authToken);
        }
    }
}
