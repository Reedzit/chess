package service;

import dataAccess.*;
import model.*;
import requests.LoginRequest;
import responses.*;

import java.util.Objects;

public class UserService {
    UserDAO userDAO = new DbUserDAO();
    AuthDAO authDAO =  new MemoryAuthDAO();
    public UserService() throws DataAccessException {

    }
    public RegisterResponse register(UserData user){
        RegisterResponse response;
//        System.out.println(user);
        if (userDAO.getUser(user.username()) != null) {
            response = new RegisterResponse(null,null,"Error: already taken");
        }else if (user.username() == null || user.password() == null || user.email() == null){
            response = new RegisterResponse(null, null, "Error: bad request");
        }else{
            userDAO.createUser(user);
            String authToken = new MemoryAuthDAO().createAuth(user.username());
            response = new RegisterResponse(user.username(), authToken, null);
        }
        return response;
    }
    public LoginResponse login(LoginRequest loginRequest) throws DataAccessException {
        LoginResponse response = new LoginResponse(null,null,null);
        UserData userData = userDAO.getUser(loginRequest.username());
        if (userDAO.getUser(loginRequest.username()) == null || !Objects.equals(userData.password(), loginRequest.password())){
            response = new LoginResponse(null, null, "Error: unauthorized");
        }else {
            String authToken = authDAO.createAuth(userData.username());
            response = new LoginResponse(userData.username(),authToken, null);
        }
        return response;
    }
    public EmptyResponse logout(String authToken){
        EmptyResponse response = new EmptyResponse(null);
        String tempToken = new MemoryAuthDAO().getAuth(authToken);
        if (tempToken != null){
            new MemoryAuthDAO().deleteAuth(authToken);
        }else{
            response = new EmptyResponse("Error: unauthorized");
        }
        return response;
    }
}
