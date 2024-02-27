package serviceTests;

import dataAccess.MemoryAuthDAO;
import dataAccess.MemoryUserDAO;
import model.UserData;
import org.eclipse.jetty.server.Authentication;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import requests.LoginRequest;
import responses.EmptyResponse;
import responses.LoginResponse;
import responses.RegisterResponse;
import service.ClearAppService;
import service.UserService;

public class UserServiceTests {

    @Test
    public void registerNegativeUserTest(){
        new ClearAppService().clearAll();
        UserData userData = new UserData(null, "password", "email");
        RegisterResponse response = new UserService().register(userData);
        Assertions.assertEquals(response.message(), "Error: bad request");
    }
    @Test
    public void registerPositiveUserTest(){
        new ClearAppService().clearAll();
        UserData userData = new UserData("username", "password", "email");
        RegisterResponse response = new UserService().register(userData);
        Assertions.assertNull(response.message());
        Assertions.assertEquals(response.username(), "username");

    }

    @Test
    public void loginPositiveUserTest(){
        new ClearAppService().clearAll();
        UserData userData = new UserData("name", "password", "email");
        new MemoryUserDAO().createUser(userData);
        LoginRequest request = new LoginRequest("name", "password");
        LoginResponse response = new UserService().login(request);
        Assertions.assertNull(response.message());
        Assertions.assertEquals(response.authToken(), new MemoryAuthDAO().getAuth(response.authToken()));

    }
    @Test
    public void loginNegativeUserTest(){
        new ClearAppService().clearAll();
        UserData userData = new UserData("name", "password", "email");
        new MemoryUserDAO().createUser(userData);
        LoginRequest request = new LoginRequest("wrong", "password");
        LoginResponse response = new UserService().login(request);
        Assertions.assertEquals(response.message(), "Error: unauthorized");
    }
    @Test
    public void logoutPositiveUserTest(){
        new ClearAppService().clearAll();
        UserData userData = new UserData("name", "password", "email");
        new MemoryUserDAO().createUser(userData);
        String authToken = new MemoryAuthDAO().createAuth("name");
        EmptyResponse response = new UserService().logout(authToken);
        Assertions.assertNull(new MemoryAuthDAO().getAuth(authToken));
    }
    @Test
    public void logoutNegativeUserTest(){
        new ClearAppService().clearAll();
        UserData userData = new UserData("name", "password", "email");
        new MemoryUserDAO().createUser(userData);
        String authToken = "fakeAuthToken";
        EmptyResponse response = new UserService().logout(authToken);
        Assertions.assertEquals(response.message(), "Error: unauthorized");
    }

}
