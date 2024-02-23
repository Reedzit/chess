package dataAccess;

import model.UserData;

import java.util.HashMap;

public class MemoryUserDAO implements UserDAO{
     static final private HashMap<String, UserData> userTable = new HashMap<>();
    @Override
    public void createUser(UserData entry) {
        userTable.put(entry.username(), entry);
    }

    @Override
    public UserData getUser(String username) {
        return userTable.get(username);
    }

    @Override
    public void clear() {
        userTable.clear();
    }
}
