package dataAccess;

import chess.ChessGame;
import model.GameData;
import com.google.gson.Gson;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.HashSet;

public class DbGameDAO implements GameDAO {
    String initStatement = """
            CREATE TABLE IF NOT EXISTS  game (
              `gameID` int NOT NULL AUTO_INCREMENT,
              `whiteUsername` varchar(256),
              `blackUsername` varchar(256),
              `gameName` varchar(256) NOT NULL,
              `chessGame` TEXT NOT NULL,
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
        var insertStatement = "INSERT INTO game (whiteUsername, blackUsername, gameName, chessGame) VALUES (?, ?, ?, ?)";
        ChessGame chessGame = new ChessGame();
        var serializedGame = new Gson().toJson(chessGame);
        try (var conn = DatabaseManager.getConnection()){
            try (var ps = conn.prepareStatement(insertStatement, Statement.RETURN_GENERATED_KEYS)) {
                ps.setNull(1, Types.VARCHAR);
                ps.setNull(2,Types.VARCHAR);
                ps.setString(3, gameName);
                ps.setString(4, serializedGame);
                ps.executeUpdate();
                var result = ps.getGeneratedKeys();
                result.next();
                int id = result.getInt(1);
                ps.close();
                System.out.println("This is the gameID for a created game: "+ id);
                return id;
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public GameData getGame(String gameName) throws DataAccessException {
        var selectStatement = "SELECT * FROM game WHERE gameName = ?";
        try (var conn = DatabaseManager.getConnection()){
            try (var statement = conn.prepareStatement(selectStatement)){
                statement.setString(1, gameName);
                var result = statement.executeQuery();
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
        var selectStatement = "SELECT * FROM game";
        try (var conn = DatabaseManager.getConnection()){
            try (var statement = conn.prepareStatement(selectStatement)){
                var result = statement.executeQuery();
                while (result.next()){
                    GameData gameData = new GameData(result.getInt(1), result.getString(2), result.getString(3), result.getString(4), new Gson().fromJson(result.getString(5),ChessGame.class));
                    gameList.add(gameData);

                }
                result.close();
                statement.close();
                return gameList;
            }
        }catch (SQLException e){
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public void updateGame(GameData game) throws DataAccessException {
        String gameName = game.gameName();
        var updateStatement = "UPDATE game SET whiteUsername = ?, blackUsername = ?, chessGame = ? WHERE gameName = ?";
        try (var conn = DatabaseManager.getConnection()){
            try (var statement = conn.prepareStatement(updateStatement)){
                if (game.whiteUsername() != null) {
                    statement.setString(1, game.whiteUsername());
                }else {
                    statement.setNull(1,Types.VARCHAR);
                }
                if (game.blackUsername() != null) {
                    statement.setString(2, game.blackUsername());
                }else {
                    statement.setNull(2,Types.VARCHAR);
                }
                statement.setString(3, new Gson().toJson(game.game()));
                statement.setString(4, gameName);
                System.out.println("This is the update statement for a game: " + statement);
                statement.executeUpdate();
            }
        }catch (SQLException e){
            throw new DataAccessException(e.getMessage());
        }
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
