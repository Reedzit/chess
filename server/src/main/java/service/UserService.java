package service;

import model.AuthData;
import model.UserData;

public class UserService {
    public AuthData register(UserData user){
        return new AuthData("authToken",user.username());
    }
//    public AuthData login(UserData user){}
//    public void logout(UserData user){}
}
