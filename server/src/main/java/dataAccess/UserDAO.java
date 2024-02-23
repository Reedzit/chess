package dataAccess;

import model.UserData;

public interface UserDAO {
    void createUser(UserData entry);
    UserData getUser(String username);
    void clear();


}
