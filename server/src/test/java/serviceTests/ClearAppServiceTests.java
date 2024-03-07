package serviceTests;

import dataAccess.DataAccessException;
import dataAccess.MemoryAuthDAO;
import dataAccess.MemoryGameDAO;
import dataAccess.MemoryUserDAO;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import service.ClearAppService;

import java.util.ArrayList;

public class ClearAppServiceTests {
    @Test
    public void clearAppTest() throws DataAccessException {
        // add things to database
        MemoryAuthDAO auth1 = new MemoryAuthDAO();
        String authToken1 = auth1.createAuth("username1");
        MemoryGameDAO game1 = new MemoryGameDAO();
        game1.createGame("game1");
        MemoryUserDAO user1 = new MemoryUserDAO();
        user1.createUser(new UserData("username1", "password1", "email1"));
        // clear database
        new ClearAppService().clearAll();
        // and then check if the database is empty.
        Assertions.assertNull(auth1.getAuth(authToken1));
        Assertions.assertEquals(game1.listGames(), new ArrayList<>());
        Assertions.assertNull(user1.getUser("username1"));
    }
}
