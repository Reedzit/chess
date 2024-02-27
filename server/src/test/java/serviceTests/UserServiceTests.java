package serviceTests;

import model.UserData;
import org.junit.jupiter.api.Test;
import service.ClearAppService;
import service.UserService;

public class UserServiceTests {

    @Test
    public void registerUserTest(){
        new ClearAppService().clearAll();
        UserData userData = new UserData("username", "password", "email");

        new UserService().register(userData);
    }
}
