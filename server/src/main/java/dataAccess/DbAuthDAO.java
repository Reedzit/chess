package dataAccess;

import java.sql.SQLException;
import java.util.UUID;

public class DbAuthDAO implements AuthDAO{
    String initStatement = """
            CREATE TABLE IF NOT EXISTS  auth (
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
        String updateStatement = "INSERT INTO chess.auth (username, token) VALUES(?, ?);";
        try (var conn = DatabaseManager.getConnection()){
            try (var statement = conn.prepareStatement(updateStatement)) {
                statement.setString(1, username);
                statement.setString(2, authToken);
                statement.executeUpdate();
                statement.close();
                return authToken;
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public String getAuth(String token) throws DataAccessException {
        var selectStatement = String.format("SELECT * FROM chess.auth WHERE token = '%s'", token);
        try (var conn = DatabaseManager.getConnection()){
            try (var statement = conn.createStatement()){
                var result = statement.executeQuery(selectStatement);
                if (result.next()){
                    result.close();
                    statement.close();
                    return token;
                }
                return null;
            }
        }catch (SQLException e){
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public String getUsername(String token) throws DataAccessException {
        var selectStatement = String.format("SELECT * FROM chess.auth WHERE token = '%s'", token);
        try (var conn = DatabaseManager.getConnection()){
            try (var statement = conn.createStatement()){
                var result = statement.executeQuery(selectStatement);
                if (result.next()){
                    return result.getString(2);
                }
                return null;
            }
        }catch (SQLException e){
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public void deleteAuth(String token) throws DataAccessException {
        String deleteStatement = String.format("DELETE FROM chess.auth WHERE token = '%s'", token);
        try (var conn = DatabaseManager.getConnection()){
            try ( var statement = conn.createStatement()){
                statement.executeUpdate(deleteStatement);
            }
        }catch (SQLException e){
            throw new DataAccessException(e.getMessage());
        }
    }

    void configTable() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection()){
            try (var preparedStatement = conn.prepareStatement(initStatement)){
                preparedStatement.executeUpdate();
            }
        }catch (SQLException ex){
            throw new DataAccessException(String.format("Unable to configure database: %s", ex.getMessage()));
        }
    }
    @Override
    public void clear() throws DataAccessException{
        String deleteStatement = "TRUNCATE TABLE chess.auth;";
        try (var conn = DatabaseManager.getConnection()){
            try (var statement = conn.createStatement()){
                statement.executeUpdate(deleteStatement);
            }
        }catch (SQLException e){
            throw new DataAccessException(e.getMessage());
        }
    }
}
