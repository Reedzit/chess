package service;

import dataAccess.MemoryAuthDAO;
import dataAccess.MemoryGameDAO;
import dataAccess.MemoryUserDAO;

public class ClearAppService {

    public void clearAll(){
        new MemoryAuthDAO().clear();
        new MemoryUserDAO().clear();
        new MemoryGameDAO().clear();
    }
}
