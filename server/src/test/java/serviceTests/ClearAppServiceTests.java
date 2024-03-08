package serviceTests;

import dataAccess.*;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import service.ClearAppService;
import java.util.HashSet;

public class ClearAppServiceTests {
    @Test
    public void clearAppTest() throws DataAccessException {
        // add things to database
        AuthDAO auth1 = new DbAuthDAO();
        String authToken1 = auth1.createAuth("username1");
        GameDAO game1 = new DbGameDAO();
        game1.createGame("game1");
        UserDAO user1 = new DbUserDAO();
        user1.createUser(new UserData("username1", "password1", "email1"));
        // clear database
        new ClearAppService().clearAll();
        // and then check if the database is empty.
        Assertions.assertNull(auth1.getAuth(authToken1));
        Assertions.assertEquals(game1.listGames(), new HashSet<>());
        Assertions.assertNull(user1.getUser("username1"));
    }
}
