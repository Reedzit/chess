package dataAccess;

public interface AuthDAO{
    String createAuth(String username) throws DataAccessException;
    String getAuth(String token) throws DataAccessException;
    String getUsername(String token) throws DataAccessException;
    void deleteAuth(String token) throws DataAccessException;
    void clear() throws DataAccessException;
}
