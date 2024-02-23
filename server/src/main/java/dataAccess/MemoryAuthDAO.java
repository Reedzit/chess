package dataAccess;

import model.AuthData;
import model.UserData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class MemoryAuthDAO implements AuthDAO{
    HashMap<String, String> authTable = new HashMap<>();
    List<AuthData> authList = new ArrayList<>();
    @Override
    public String createAuth(String username) {
        String authToken = UUID.randomUUID().toString();
        authList.add(new AuthData(authToken, username));
        authTable.put(authToken, username);
        return authToken;
    }

    @Override
    public String getAuth(String token) {
        for (var curr : authList){
            if (curr.authToken().equals(token)){
                return curr.authToken();
            }
        }
        return null;
    }

    @Override
    public void deleteAuth(String token) {

    }

    @Override
    public void clear() {

    }
}
