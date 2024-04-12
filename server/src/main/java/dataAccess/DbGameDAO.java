package dataAccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;

import java.sql.*;
import java.util.HashSet;

public class DbGameDAO implements GameDAO {

    private static final String CREATE_TABLE_SQL = """
            CREATE TABLE IF NOT EXISTS GameData (
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

    private final Gson gson = new Gson();

    public DbGameDAO() throws DataAccessException {
        configTable();
    }

    @Override
    public Integer createGame(String gameName) throws DataAccessException {
        String insertStatement = "INSERT INTO GameData (whiteUsername, blackUsername, gameName, chessGame) VALUES (?, ?, ?, ?)";
        ChessGame chessGame = new ChessGame();
        String serializedGame = gson.toJson(chessGame);
        return executeInsertAndGetGeneratedKey(insertStatement, null, null, gameName, serializedGame);
    }

    @Override
    public GameData getGame(String gameName) throws DataAccessException {
        String selectStatement = "SELECT * FROM GameData WHERE gameName = ?";
        return executeQueryAndRetrieveGameData(selectStatement, gameName);
    }

    @Override
    public String getGameName(Integer gameID) throws DataAccessException {
        String selectStatement = "SELECT gameName FROM GameData WHERE gameID = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement statement = conn.prepareStatement(selectStatement)) {
            statement.setInt(1, gameID);
            ResultSet result = statement.executeQuery();
            if (result.next()) {
                return result.getString("gameName");
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
        return null;
    }

    @Override
    public HashSet<GameData> listGames() throws DataAccessException {
        HashSet<GameData> gameList = new HashSet<>();
        String selectStatement = "SELECT * FROM GameData";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement statement = conn.prepareStatement(selectStatement)) {
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                gameList.add(parseGameDataFromResultSet(result));
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
        return gameList;
    }

    @Override
    public void updateGame(GameData game) throws DataAccessException {
        String updateStatement = "UPDATE GameData SET whiteUsername = ?, blackUsername = ?, chessGame = ? WHERE gameName = ?";
        String serializedGame = gson.toJson(game.game());
        executeUpdate(updateStatement, game.whiteUsername(), game.blackUsername(), serializedGame, game.gameName());
    }

    private void configTable() throws DataAccessException {
        DatabaseManager.createDatabase();
        executeUpdate(CREATE_TABLE_SQL);
    }

    private Integer executeInsertAndGetGeneratedKey(String sql, Object... params) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement statement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            setParameters(statement, params);
            statement.executeUpdate();
            ResultSet generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                return generatedKeys.getInt(1);
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
        return null;
    }

    private GameData executeQueryAndRetrieveGameData(String sql, Object... params) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement statement = conn.prepareStatement(sql)) {
            setParameters(statement, params);
            ResultSet result = statement.executeQuery();
            if (result.next()) {
                return parseGameDataFromResultSet(result);
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
        return null;
    }

    private void executeUpdate(String sql, Object... params) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement statement = conn.prepareStatement(sql)) {
            setParameters(statement, params);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    private void setParameters(PreparedStatement statement, Object... params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            if (params[i] == null) {
                statement.setNull(i + 1, Types.VARCHAR);
            } else {
                statement.setObject(i + 1, params[i]);
            }
        }
    }

    private GameData parseGameDataFromResultSet(ResultSet result) throws SQLException {
        return new GameData(
                result.getInt("gameID"),
                result.getString("whiteUsername"),
                result.getString("blackUsername"),
                result.getString("gameName"),
                gson.fromJson(result.getString("chessGame"), ChessGame.class)
        );
    }

    @Override
    public void clear() throws DataAccessException {
        String deleteStatement = "TRUNCATE TABLE GameData";
        executeUpdate(deleteStatement);
    }
}
