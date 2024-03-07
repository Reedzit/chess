package dataAccess;

import chess.ChessGame;
import model.GameData;
import com.google.gson.Gson;
import java.sql.SQLException;
import java.util.HashSet;

public class DbGameDAO implements GameDAO {
    String initStatement = """
            CREATE TABLE IF NOT EXISTS  game (
              `gameID` int NOT NULL AUTO_INCREMENT,
              `whiteUsername` varchar(256) NULL,
              `blackUsername` varchar(256) NULL,
              `gameName` varchar(256) NOT NULL,
              `chessGame` varchar(256) NOT NULL,
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
        var insertStatement = "INSERT INTO game (whiteUsername, blackUsername, gameName, game) VALUES (?, ?, ?, ?)";
        ChessGame chessGame = new ChessGame();
        var serializedGame = new Gson().toJson(chessGame);
        try (var conn = DatabaseManager.getConnection()){
            try (var ps = conn.prepareStatement(insertStatement)) {
                ps.setString(1, null);
                ps.setString(2, null);
                ps.setString(3, gameName);
                ps.setString(4, serializedGame);
                ps.executeUpdate();
                var result = ps.getGeneratedKeys();
                int id = result.getInt(1);
                ps.close();
                return id;
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public GameData getGame(String gameName) throws DataAccessException {
        var selectStatement = String.format("SELECT * FROM chess.game WHERE gameName = '%s'", gameName);
        try (var conn = DatabaseManager.getConnection()){
            try (var statement = conn.createStatement()){
                var result = statement.executeQuery(selectStatement);
                if (result.next()){
                    GameData gameData = new GameData(result.getInt(1), result.getString(2), result.getString(3), result.getString(4), new Gson().fromJson(result.getString(5),ChessGame.class));
                    result.close();
                    statement.close();
                    return gameData;
                }
                return null;
            }
        }catch (SQLException e){
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public String getGameName(Integer gameID) throws DataAccessException {
        var selectStatement = String.format("SELECT * FROM chess.game WHERE gameID = '%s'", gameID);
        try (var conn = DatabaseManager.getConnection()){
            try (var statement = conn.createStatement()){
                var result = statement.executeQuery(selectStatement);
                if (result.next()){
                    String gameName = result.getString(4);
                    result.close();
                    statement.close();
                    return gameName;
                }
                return null;
            }
        }catch (SQLException e){
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public HashSet<GameData> listGames() throws DataAccessException {
        HashSet<GameData> gameList = new HashSet<>();
        var selectStatement = "SELECT * FROM chess.game;";
        try (var conn = DatabaseManager.getConnection()){
            try (var statement = conn.createStatement()){
                var result = statement.executeQuery(selectStatement);
                while (result.next()){
                    GameData gameData = new GameData(result.getInt(1), result.getString(2), result.getString(3), result.getString(4), new Gson().fromJson(result.getString(5),ChessGame.class));
                    gameList.add(gameData);
                    result.close();
                    statement.close();
                }
                return gameList;
            }
        }catch (SQLException e){
            throw new DataAccessException(e.getMessage());
        }
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
