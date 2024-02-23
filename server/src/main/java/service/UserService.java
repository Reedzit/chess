package service;

import dataAccess.MemoryAuthDAO;
import dataAccess.MemoryUserDAO;
import model.*;
import requests.LoginRequest;
import responses.*;

import java.util.Objects;

public class UserService {
    public RegisterResponse register(UserData user){
        RegisterResponse response = new RegisterResponse(null,null,null);
        System.out.println(user);
        if (new MemoryUserDAO().getUser(user.username()) == null) {
            new MemoryUserDAO().createUser(user);
            String authToken = new MemoryAuthDAO().createAuth(user.username());
            response = new RegisterResponse(user.username(), authToken, null);
        }else{
            response = new RegisterResponse(null,null,"Error: already taken");
        }
        return response;
    }
    public LoginResponse login(LoginRequest loginRequest){
        LoginResponse response = new LoginResponse(null,null,null);
        UserData userData = new MemoryUserDAO().getUser(loginRequest.username());
        if (new MemoryUserDAO().getUser(loginRequest.username()) == null || !Objects.equals(userData.password(), loginRequest.password())){
            response = new LoginResponse(null, null, "Error: unauthorized");
        }else {
            String authToken = new MemoryAuthDAO().createAuth(userData.username());
            response = new LoginResponse(userData.username(),authToken, null);
        }
        return response;
    }
    public LogoutResponse logout(String authToken){
        LogoutResponse response = new LogoutResponse(null);
        String tempToken = new MemoryAuthDAO().getAuth(authToken);
        if (tempToken != null){
            new MemoryAuthDAO().deleteAuth(authToken);
        }else{
            response = new LogoutResponse("Error: unauthorized");
        }
        return response;
    }
}
