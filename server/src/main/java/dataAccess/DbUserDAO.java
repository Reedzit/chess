package dataAccess;

import model.UserData;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DbUserDAO implements UserDAO {

    private static final String CREATE_TABLE_SQL = """
            CREATE TABLE IF NOT EXISTS UserData (
              `id` int NOT NULL AUTO_INCREMENT,
              `username` varchar(256) NOT NULL,
              `password` varchar(256) NOT NULL,
              `email` varchar(256) NOT NULL,
              PRIMARY KEY (`id`),
              INDEX(email),
              INDEX(username)
            )
            """;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public DbUserDAO() throws DataAccessException {
        configTable();
    }

    @Override
    public void createUser(UserData entry) throws DataAccessException {
        String insertStatement = "INSERT INTO UserData (username, password, email) VALUES (?, ?, ?)";
        executeUpdate(insertStatement, entry.username(), encodePassword(entry.password()), entry.email());
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        String selectStatement = "SELECT * FROM UserData WHERE username = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement statement = conn.prepareStatement(selectStatement)) {
            statement.setString(1, username);
            ResultSet result = statement.executeQuery();
            if (result.next()) {
                return new UserData(result.getString("username"), result.getString("password"), result.getString("email"));
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
        return null;
    }

    @Override
    public void clear() throws DataAccessException {
        String truncateStatement = "TRUNCATE TABLE UserData";
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

    private String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }
}
