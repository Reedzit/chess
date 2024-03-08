package dataAccessTests;

import dataAccess.*;
import org.junit.jupiter.api.Test;

public class UserDAOTests {
    UserDAO userDAO = new DbUserDAO();
    AuthDAO authDAO = new DbAuthDAO();

    public UserDAOTests() throws DataAccessException {
    }

    @Test
    public void createUserTestPos() throws DataAccessException {
        userDAO.clear();
        authDAO.clear();

    }
    @Test
    public void createUserTestNeg(){

    }
    @Test
    public void getUserTestPos(){

    }
    @Test
    public void getUserTestNeg(){

    }
}
