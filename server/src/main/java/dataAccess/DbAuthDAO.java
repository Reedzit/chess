package dataAccess;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class DbAuthDAO implements AuthDAO {

    private static final String CREATE_TABLE_SQL = """
            CREATE TABLE IF NOT EXISTS AuthData (
              `id` int NOT NULL AUTO_INCREMENT,
              `username` varchar(256) NOT NULL,
              `token` varchar(256) NOT NULL,
              PRIMARY KEY (`id`),
              INDEX(username),
              INDEX(token)
            )
            """;

    public DbAuthDAO() throws DataAccessException {
        configTable();
    }

    @Override
    public String createAuth(String username) throws DataAccessException {
        String authToken = UUID.randomUUID().toString();
        String insertStatement = "INSERT INTO AuthData (username, token) VALUES (?, ?)";
        executeUpdate(insertStatement, username, authToken);
        return authToken;
    }

    @Override
    public String getAuth(String token) throws DataAccessException {
        String selectStatement = "SELECT * FROM AuthData WHERE token = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement statement = conn.prepareStatement(selectStatement)) {
            statement.setString(1, token);
            ResultSet result = statement.executeQuery();
            if (result.next()) {
                return token;
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
        return null;
    }

    @Override
    public String getUsername(String token) throws DataAccessException {
        String selectStatement = "SELECT username FROM AuthData WHERE token = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement statement = conn.prepareStatement(selectStatement)) {
            statement.setString(1, token);
            ResultSet result = statement.executeQuery();
            if (result.next()) {
                return result.getString("username");
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
        return null;
    }

    @Override
    public void deleteAuth(String token) throws DataAccessException {
        String deleteStatement = "DELETE FROM AuthData WHERE token = ?";
        executeUpdate(deleteStatement, token);
    }

    @Override
    public void clear() throws DataAccessException {
        String truncateStatement = "TRUNCATE TABLE AuthData";
        executeUpdate(truncateStatement);
    }

    private void configTable() throws DataAccessException {
        DatabaseManager.createDatabase();
        executeUpdate(CREATE_TABLE_SQL);
    }

    private void executeUpdate(String sql, Object... params) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement statement = conn.prepareStatement(sql)) {
            int index = 1;
            for (Object param : params) {
                statement.setObject(index++, param);
            }
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }
}
