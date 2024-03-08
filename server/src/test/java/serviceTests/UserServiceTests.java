package serviceTests;

import dataAccess.*;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import requests.LoginRequest;
import responses.EmptyResponse;
import responses.LoginResponse;
import responses.RegisterResponse;
import service.ClearAppService;
import service.UserService;

public class UserServiceTests {
    AuthDAO authDAO = new DbAuthDAO();
    UserDAO userDAO = new DbUserDAO();

    public UserServiceTests() throws DataAccessException {
    }

    @Test
    public void registerNegativeUserTest() throws DataAccessException {
        new ClearAppService().clearAll();
        UserData userData = new UserData(null, "password", "email");
        RegisterResponse response = new UserService().register(userData);
        Assertions.assertEquals(response.message(), "Error: bad request");
    }
    @Test
    public void registerPositiveUserTest() throws DataAccessException {
        new ClearAppService().clearAll();
        UserData userData = new UserData("username", "password", "email");
        RegisterResponse response = new UserService().register(userData);
        Assertions.assertNull(response.message());
        Assertions.assertEquals(response.username(), "username");

    }

    @Test
    public void loginPositiveUserTest() throws DataAccessException {
        new ClearAppService().clearAll();
        UserData userData = new UserData("name", "password", "email");
        userDAO.createUser(userData);
        LoginRequest request = new LoginRequest("name", "password");
        LoginResponse response = new UserService().login(request);
        Assertions.assertNull(response.message());
        Assertions.assertEquals(response.authToken(), authDAO.getAuth(response.authToken()));

    }
    @Test
    public void loginNegativeUserTest() throws DataAccessException {
        new ClearAppService().clearAll();
        UserData userData = new UserData("name", "password", "email");
        userDAO.createUser(userData);
        LoginRequest request = new LoginRequest("wrong", "password");
        LoginResponse response = new UserService().login(request);
        Assertions.assertEquals(response.message(), "Error: unauthorized");
    }
    @Test
    public void logoutPositiveUserTest() throws DataAccessException {
        new ClearAppService().clearAll();
        UserData userData = new UserData("name", "password", "email");
        userDAO.createUser(userData);
        String authToken = authDAO.createAuth("name");
        new UserService().logout(authToken);
        Assertions.assertNull(authDAO.getAuth(authToken));
    }
    @Test
    public void logoutNegativeUserTest() throws DataAccessException {
        new ClearAppService().clearAll();
        UserData userData = new UserData("name", "password", "email");
        userDAO.createUser(userData);
        String authToken = "fakeAuthToken";
        EmptyResponse response = new UserService().logout(authToken);
        Assertions.assertEquals(response.message(), "Error: unauthorized");
    }

}
