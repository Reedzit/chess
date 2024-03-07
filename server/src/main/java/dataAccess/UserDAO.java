package dataAccess;

import model.UserData;

import javax.xml.crypto.Data;

public interface UserDAO {
    void createUser(UserData entry) throws DataAccessException;
    UserData getUser(String username) throws DataAccessException;
    void clear() throws DataAccessException;


}
