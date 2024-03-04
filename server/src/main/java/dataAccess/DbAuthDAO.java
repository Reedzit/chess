package dataAccess;

import java.sql.SQLException;

public class DbAuthDAO implements AuthDAO{

    @Override
    public String createAuth(String username) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement("SELECT 1+1")) {
                var result = preparedStatement.executeQuery();
                result.next();
            }
        }
        return null;
    }

    @Override
    public String getAuth(String token) {
        return null;
    }

    @Override
    public String getUsername(String token) {
        return null;
    }

    @Override
    public void deleteAuth(String token) {

    }

    @Override
    public void clear() {

    }
}
