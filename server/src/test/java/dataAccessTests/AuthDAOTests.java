package dataAccessTests;

import dataAccess.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.sql.ResultSet;
import java.sql.SQLException;

public class AuthDAOTests {
    AuthDAO authDAO = new DbAuthDAO();

    public AuthDAOTests() throws DataAccessException {
    }

    @Test
    public void createAuthTestPos() throws DataAccessException, SQLException {
        authDAO.clear();
        authDAO.createAuth("username1");
        authDAO.createAuth("username2");
        authDAO.createAuth("username3");
        var conn = DatabaseManager.getConnection();
        var statement = conn.prepareStatement("SELECT * FROM AuthData");
        var result = statement.executeQuery();
        int counter = 0;
        while(result.next()){
            counter += 1;
        }
        result.close();
        statement.close();
        Assertions.assertEquals(counter, 3);
    }
    @Test
    public void createAuthTestNeg() throws DataAccessException, SQLException {
        authDAO.clear();
        Assertions.assertThrows(DataAccessException.class, () -> authDAO.createAuth(null));
    }

    @Test
    public void getAuthTestPos() throws DataAccessException, SQLException {
        authDAO.clear();
        String username;
        String token = authDAO.createAuth("username");
        try(var conn = DatabaseManager.getConnection()){
            try(var statement = conn.prepareStatement("SELECT * FROM AuthData WHERE token = ?")) {
                statement.setString(1, token);
                var result = statement.executeQuery();
                result.next();
                username = result.getString(2);
                result.close();
            }
        }
        Assertions.assertEquals("username", username);
    }
    @Test
    public void getAuthTestNeg() throws DataAccessException, SQLException {
        authDAO.clear();
        Assertions.assertNull(authDAO.getAuth("token"));
    }
    @Test
    public void deleteAuthPos() throws DataAccessException, SQLException {
        authDAO.clear();
        String token1 = authDAO.createAuth("username");
        String token2 = authDAO.createAuth("username");
        String check;
        try(var conn = DatabaseManager.getConnection()){
            try(var statement = conn.prepareStatement("SELECT * FROM AuthData WHERE username = username")) {
                var result = statement.executeQuery();
                result.next();
                check = result.getString(3);
                result.close();
            }
        }
        Assertions.assertNotEquals(check, token2);
    }
    @Test
    public void deleteAuthNeg() throws DataAccessException, SQLException {
        authDAO.clear();
        String token1 = authDAO.createAuth("username");
        String token2 = authDAO.createAuth("username");
        authDAO.deleteAuth(token1);
        String check;
        try(var conn = DatabaseManager.getConnection()){
            try(var statement = conn.prepareStatement("SELECT * FROM AuthData WHERE username = username")) {
                var result = statement.executeQuery();
                result.next();
                check = result.getString(3);
                result.close();
            }
        }
        Assertions.assertNotEquals(check, token1);
    }
    @Test
    public void getUsernamePos() throws DataAccessException, SQLException {
        authDAO.clear();
        String username;
        String token = authDAO.createAuth("username");
        try(var conn = DatabaseManager.getConnection()){
            try(var statement = conn.prepareStatement("SELECT * FROM AuthData WHERE token = ?")) {
                statement.setString(1, token);
                var result = statement.executeQuery();
                result.next();
                username = result.getString(2);
                result.close();
            }
        }
        Assertions.assertEquals("username", username);
    }
    @Test
    public void getUsernameNeg() throws DataAccessException, SQLException {
        authDAO.clear();
        Assertions.assertNull(authDAO.getUsername("username"));
    }
}
