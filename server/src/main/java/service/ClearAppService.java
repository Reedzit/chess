package service;

import dataAccess.*;

public class ClearAppService {

    public void clearAll() throws DataAccessException {
        new DbAuthDAO().clear();
        new DbUserDAO().clear();
        new DbGameDAO().clear();
    }
}
