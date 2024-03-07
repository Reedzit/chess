package dataAccess;

import model.UserData;

public interface UserDAO {
    void createUser(UserData entry) throws DataAccessException;
    UserData getUser(String username) throws DataAccessException;
    void clear() throws DataAccessException;


}
