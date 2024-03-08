package dataAccessTests;

import dataAccess.*;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAOTests {
    UserDAO userDAO = new DbUserDAO();
    AuthDAO authDAO = new DbAuthDAO();

    public UserDAOTests() throws DataAccessException {
    }

    @Test
    public void createUserTestPos() throws DataAccessException, SQLException {
        userDAO.clear();
        authDAO.clear();
        ResultSet result;
        UserData userData = new UserData("username","password","email");
        userDAO.createUser(userData);
        try(var conn = DatabaseManager.getConnection()) {
            try (var statement = conn.prepareStatement("SELECT * FROM UserData WHERE username = ?")) {
                statement.setString(1, "username");
                result = statement.executeQuery();
                Assertions.assertTrue(result.next());
                result.close();
            }
        }
    }
    @Test
    public void createUserTestNeg() throws DataAccessException {
        userDAO.clear();
        authDAO.clear();
        UserData userData = new UserData(null,null,null);
        Assertions.assertThrows(IllegalArgumentException.class, () -> userDAO.createUser(userData));
    }
    @Test
    public void getUserTestPos() throws DataAccessException, SQLException{
        userDAO.clear();
        authDAO.clear();
        ResultSet result;
        UserData userData = new UserData("username","password","email");
        userDAO.createUser(userData);
        try (var conn = DatabaseManager.getConnection()) {
            try (var statement = conn.prepareStatement("SELECT * FROM UserData WHERE username = ?")) {
                statement.setString(1, "username");
                result = statement.executeQuery();
                result.next();
                Assertions.assertEquals("username", result.getString(2));
                result.close();
            }
        }
    }
    @Test
    public void getUserTestNeg() throws DataAccessException, SQLException {
        userDAO.clear();
        authDAO.clear();
        Assertions.assertNull(userDAO.getUser("username"));
    }
}
