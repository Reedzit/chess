package dataAccess;

import model.GameData;

import java.sql.SQLException;
import java.util.HashSet;

public class DbGameDAO implements GameDAO {
    String initStatement = """
            CREATE TABLE IF NOT EXISTS  game (
              `gameID` int NOT NULL AUTO_INCREMENT,
              `whiteUsername` varchar(256) NOT NULL,
              `blackUsername` varchar(256) NOT NULL,
              `gameName` varchar(256) NOT NULL,
              `game` varchar(256) NOT NULL,
              PRIMARY KEY (`gameID`),
              INDEX(gameID),
              INDEX(gameName)
            )
            """;
    public DbGameDAO() throws DataAccessException {
        configTable();
    }

    @Override
    public Integer createGame(String gameName) throws DataAccessException {
        return null;
    }

    @Override
    public GameData getGame(String gameName) throws DataAccessException {
        return null;
    }

    @Override
    public String getGameName(Integer gameID) throws DataAccessException {
        return null;
    }

    @Override
    public HashSet<GameData> listGames() throws DataAccessException {
        return null;
    }

    @Override
    public void updateGame(GameData game) throws DataAccessException {

    }

    public void configTable() throws DataAccessException {
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
        String deleteStatement = "TRUNCATE TABLE chess.game;";
        try (var conn = DatabaseManager.getConnection()){
            try (var statement = conn.createStatement()){
                statement.executeUpdate(deleteStatement);
            }
        }catch (SQLException e){
            throw new DataAccessException(e.getMessage());
        }
    }
}
