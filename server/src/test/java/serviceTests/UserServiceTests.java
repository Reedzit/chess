package serviceTests;

import model.UserData;
import org.junit.jupiter.api.Test;

public class UserServiceTests {

    @Test
    public void registerUserTest(){
        UserData userData = new UserData("username", "password", "email");

    }
}
