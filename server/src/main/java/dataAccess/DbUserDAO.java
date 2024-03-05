package dataAccess;

import model.UserData;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.sql.SQLException;

public class DbUserDAO  implements UserDAO{
    String statement = """
            CREATE TABLE IF NOT EXISTS  users (
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

    }

    public String encodePassword(String password){ return new BCryptPasswordEncoder().encode(password);}

    @Override
    public UserData getUser(String username) {
        return null;
    }

    public void configTable() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection()){
            try(var preparedStatement = conn.prepareStatement(statement)){
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
