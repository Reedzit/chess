package serviceTests;

import chess.ChessGame;
import dataAccess.*;
import model.GameData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.context.support.GenericApplicationContextExtensionsKt;
import responses.CreateGameResponse;
import responses.EmptyResponse;
import responses.GameListResponse;
import service.ClearAppService;
import service.GameService;

import java.util.HashSet;
import java.util.List;

public class GameServiceTests {
    @Test
    public void createGameTestPos() throws DataAccessException {
        new ClearAppService();
        AuthDAO authDAO = new MemoryAuthDAO();
        String authToken = authDAO.createAuth("username");
        CreateGameResponse response = new GameService().createGame("gameName",authToken);
        Assertions.assertEquals(response.gameID(), new MemoryGameDAO().getGame("gameName").gameID());
    }
    @Test
    public void createGameTestNeg() throws DataAccessException {
        new ClearAppService();
        AuthDAO authDAO = new MemoryAuthDAO();
        String authToken = authDAO.createAuth("username");
        CreateGameResponse response = new GameService().createGame(null,authToken);
        Assertions.assertEquals(response.message(), "Error: bad request");
    }

    @Test
    public void joinGameTestPos() throws DataAccessException {
        new ClearAppService();
        AuthDAO authDAO = new MemoryAuthDAO();
        GameDAO gameDAO = new MemoryGameDAO();
        String blackToken = authDAO.createAuth("blackUsername");
        String whiteToken = authDAO.createAuth("whiteUsername");
        Integer gameID = gameDAO.createGame("gameName");
        new GameService().joinGame(ChessGame.TeamColor.BLACK, gameID, blackToken);
        new GameService().joinGame(ChessGame.TeamColor.WHITE, gameID, whiteToken);
        Assertions.assertEquals("blackUsername", gameDAO.getGame("gameName").blackUsername());
        Assertions.assertEquals("whiteUsername", gameDAO.getGame("gameName").whiteUsername());
    }
    @Test
    public void joinGameTestNeg() throws DataAccessException {
        new ClearAppService();
        AuthDAO authDAO = new MemoryAuthDAO();
        GameDAO gameDAO = new MemoryGameDAO();
        String blackToken = authDAO.createAuth("blackUsername");
        String anotherBlackToken = authDAO.createAuth("anotherBlackUsername");
        Integer gameID = gameDAO.createGame("gameName");
        new GameService().joinGame(ChessGame.TeamColor.BLACK, gameID, blackToken);
        EmptyResponse response = new GameService().joinGame(ChessGame.TeamColor.BLACK, gameID, anotherBlackToken);
        Assertions.assertEquals("Error: already taken", response.message());

    }

    @Test
    public void getGameListTestPos() throws DataAccessException {
        new ClearAppService().clearAll();
        AuthDAO authDAO = new MemoryAuthDAO();
        GameDAO gameDAO = new MemoryGameDAO();
        String authToken = authDAO.createAuth("username");
        gameDAO.createGame("gameName1");
        gameDAO.createGame("gameName2");
        gameDAO.createGame("gameName3");
        HashSet<GameData> gameList = new HashSet<>();
                gameList.add( gameDAO.getGame("gameName1"));
                gameList.add(gameDAO.getGame("gameName2"));
                gameList.add(gameDAO.getGame("gameName3"));
        GameListResponse response = new GameService().getGameList(authToken);
        Assertions.assertEquals(gameList, response.games());
    }
    @Test
    public void getGameListTestNeg() throws DataAccessException {
        new ClearAppService().clearAll();
        String authToken = new MemoryAuthDAO().createAuth("username");
        GameListResponse response = new GameService().getGameList(null);
        Assertions.assertEquals("Error: unauthorized", response.message());
    }
}
