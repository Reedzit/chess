package dataAccess;

import model.AuthData;
import model.UserData;

public interface AuthDAO{
    String createAuth(String username);
    String getAuth(String token);
    void deleteAuth(String token);
    void clear();
}
