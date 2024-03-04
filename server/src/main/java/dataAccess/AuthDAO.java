package dataAccess;

import model.AuthData;
import model.UserData;

import java.sql.SQLException;

public interface AuthDAO{
    String createAuth(String username) throws DataAccessException;
    String getAuth(String token);
    String getUsername(String token);
    void deleteAuth(String token);
    void clear();
}
