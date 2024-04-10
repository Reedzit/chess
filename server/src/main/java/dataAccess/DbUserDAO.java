package dataAccess;

import model.UserData;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.sql.SQLException;

public class DbUserDAO  implements UserDAO{
    String initStatement = """
            CREATE TABLE IF NOT EXISTS  UserData (
              `id` int NOT NULL AUTO_INCREMENT,
              `username` varchar(256) NOT NULL,
              `password` varchar(256) NOT NULL,
              `email` varchar(256) NOT NULL,
              PRIMARY KEY (`id`),
              INDEX(email),
              INDEX(username)
            )
            """;
    public DbUserDAO() throws DataAccessException{
        configTable();
    }
    @Override
    public void createUser(UserData entry) throws DataAccessException {
        var insertStatement = "INSERT INTO UserData (username, password, email) VALUES (?, ?, ?)";
        var encodedPassword = encodePassword(entry.password());
        try (var conn = DatabaseManager.getConnection()){
            try (var ps = conn.prepareStatement(insertStatement)) {
                ps.setString(1, entry.username());
                ps.setString(2, encodedPassword);
                ps.setString(3, entry.email());
                ps.executeUpdate();
                //automatically closes connection
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    String encodePassword(String password){ return new BCryptPasswordEncoder().encode(password);}

    @Override
    public UserData getUser(String username) throws DataAccessException {
        var selectStatement = String.format("SELECT * FROM UserData WHERE username = '%s'", username);
//        System.out.println("DOEs it get here?" + selectStatement);
        try (var conn = DatabaseManager.getConnection()){
            try (var statement = conn.createStatement()){
                var result = statement.executeQuery(selectStatement);
                if (result.next()){
                    UserData newData = new UserData(result.getString(2), result.getString(3), result.getString(4));
                    result.close();
                    return newData;
                }else {
                    return null;
                }
            }
        }catch (SQLException e){
            throw new DataAccessException(e.getMessage());
        }
    }

    private void configTable() throws DataAccessException {
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
        String deleteStatement = "TRUNCATE TABLE UserData;";
        try (var conn = DatabaseManager.getConnection()){
            try (var statement = conn.createStatement()){
                statement.executeUpdate(deleteStatement);
            }
        }catch (SQLException e){
            throw new DataAccessException(e.getMessage());
        }
    }
}
