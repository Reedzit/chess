package dataAccessTests;

import dataAccess.*;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import service.ClearAppService;

import java.util.HashSet;

public class ClearAppDbTest {
        @Test
    public void clearAppTest() throws DataAccessException {
        DbAuthDAO auth1 = new DbAuthDAO();
        DbGameDAO game1 = new DbGameDAO();
        DbUserDAO user1 = new DbUserDAO();
        auth1.clear();
        game1.clear();
        user1.clear();
        // add things to database
        String authToken1 = auth1.createAuth("username1");
        game1.createGame("game1");
        user1.createUser(new UserData("username1", "password1", "email1"));
        // clear database
        auth1.clear();
        game1.clear();
        user1.clear();
        // and then check if the database is empty.
        Assertions.assertNull(auth1.getAuth(authToken1));
        Assertions.assertEquals(game1.listGames(), new HashSet<>());
        Assertions.assertNull(user1.getUser("username1"));
    }


}
