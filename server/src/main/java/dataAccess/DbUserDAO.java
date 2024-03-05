package dataAccess;

import model.UserData;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.sql.SQLException;
import java.sql.*;

public class DbUserDAO  implements UserDAO{
    String statement = """
            CREATE TABLE IF NOT EXISTS  user (
              `id` int NOT NULL AUTO_INCREMENT,
              `username` varchar(256) NOT NULL,
              `password` varchar(256) NOT NULL,
              `email` TEXT DEFAULT NULL,
              PRIMARY KEY (`id`),
              INDEX(email),
              INDEX(username)
            )
            """;
    public DbUserDAO() throws DataAccessException{
        configTable();
    }
    @Override
    public void createUser(UserData entry) {
        var insertStatement = "INSERT INTO user (username, password, email) VALUES (?, ?, ?)";
        var encodedPassword = encodePassword(entry.password());
        var id = updateTable(insertStatement, entry.username(), encodedPassword, entry.email());
    }

    public Integer updateTable(String updateStatement, String username, String password, String email) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()){
            try (var ps = conn.prepareStatement(statement, Statement.RETURN_GENERATED_KEYS)){
                ps.setString(1, username);
                ps.setString(2, password);
                ps.setString(3, email);
                ps.executeUpdate();
                var result = ps.getGeneratedKeys();
                if(result.next()) {
                    return result.getInt(1);
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
        return null;
    }

    public String encodePassword(String password){ return new BCryptPasswordEncoder().encode(password);}

    @Override
    public UserData getUser(String username) {
        return null;
    }

    public void configTable() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection()){
            try (var preparedStatement = conn.prepareStatement(statement)){
                preparedStatement.executeUpdate();
            }
        }catch (SQLException ex){
            throw new DataAccessException(String.format("Unable to configure database: %s", ex.getMessage()));
        }
    }

    @Override
    public void clear() {

    }
}
