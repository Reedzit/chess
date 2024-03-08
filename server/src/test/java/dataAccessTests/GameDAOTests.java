package dataAccessTests;

import chess.ChessGame;
import dataAccess.*;
import model.GameData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import service.GameService;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;

public class GameDAOTests {
    AuthDAO authDAO = new DbAuthDAO();
    GameDAO gameDAO = new DbGameDAO();

    public GameDAOTests() throws DataAccessException {
    }

    @Test
    public void createGameTestPos() throws DataAccessException, SQLException {
        authDAO.clear();
        gameDAO.clear();
        ResultSet result;
        Integer gameID = gameDAO.createGame("gameName");
        try(var conn = DatabaseManager.getConnection()) {
            try (var statement = conn.prepareStatement("SELECT * FROM GameData WHERE gameID = ?")) {
                statement.setInt(1, gameID);
                result = statement.executeQuery();
                Assertions.assertTrue(result.next());
                result.close();
            }
        }
    }
    @Test
    public void createGameTestNeg() throws DataAccessException {
        authDAO.clear();
        gameDAO.clear();
        Assertions.assertThrows(DataAccessException.class, () -> gameDAO.createGame(null));
    }

    @Test
    public void updateGameTestPos() throws DataAccessException, SQLException {
        authDAO.clear();
        gameDAO.clear();
        ResultSet result;
        gameDAO.createGame("gameName");
        gameDAO.updateGame(new GameData(1,"username", null, "gameName", new ChessGame()));
        try(var conn = DatabaseManager.getConnection()){
            try(var statement = conn.prepareStatement("SELECT * FROM GameData WHERE whiteUsername = ?")) {
                statement.setString(1, "username");
                result = statement.executeQuery();
                result.next();
                String username = result.getString(2);
                Assertions.assertEquals("username", username);
                result.close();
            }
        }
    }
    @Test
    public void updateGameTestNeg() throws DataAccessException, SQLException {
        authDAO.clear();
        gameDAO.clear();
        ResultSet result;
        gameDAO.createGame("gameName");
        gameDAO.updateGame(new GameData(1,null,null, "wrongGame", new ChessGame()));
        try(var conn = DatabaseManager.getConnection()){
            try(var statement = conn.prepareStatement("SELECT * FROM GameData WHERE gameName = ?")) {
                statement.setString(1, "wrongGame");
                result = statement.executeQuery();
                Assertions.assertFalse(result.next());
                result.close();
            }
        }
    }

    @Test
    public void getGameListTestPos() throws DataAccessException {
        authDAO.clear();
        gameDAO.clear();
        gameDAO.createGame("gameName1");
        gameDAO.createGame("gameName2");
        gameDAO.createGame("gameName3");
        HashSet<GameData> gameList = new HashSet<>();
        gameList.add(gameDAO.getGame("gameName1"));
        gameList.add(gameDAO.getGame("gameName2"));
        gameList.add(gameDAO.getGame("gameName3"));
        Assertions.assertEquals(gameList, gameDAO.listGames());

    }
    @Test
    public void getGameListTestNeg() throws DataAccessException {
        authDAO.clear();
        gameDAO.clear();
        Assertions.assertEquals(new HashSet<>(), gameDAO.listGames());
    }
}
