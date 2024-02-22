package dataAccess;

import model.UserData;

public interface UserDAO {
    void createUser(UserData entry);
    UserData getUser(UserData entry);
    void clear();


}
