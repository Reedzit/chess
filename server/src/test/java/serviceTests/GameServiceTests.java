package serviceTests;

import chess.ChessGame;
import dataAccess.MemoryAuthDAO;
import dataAccess.MemoryGameDAO;
import model.GameData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import responses.CreateGameResponse;
import responses.EmptyResponse;
import responses.GameListResponse;
import service.ClearAppService;
import service.GameService;

import java.util.List;

public class GameServiceTests {
    @Test
    public void createGameTestPos(){
        new ClearAppService();
        String authToken = new MemoryAuthDAO().createAuth("username");
        CreateGameResponse response = new GameService().createGame("gameName",authToken);
        Assertions.assertEquals(response.gameID(), new MemoryGameDAO().getGame("gameName").gameID());
    }
    @Test
    public void createGameTestNeg(){
        new ClearAppService();
        String authToken = new MemoryAuthDAO().createAuth("username");
        CreateGameResponse response = new GameService().createGame(null,authToken);
        Assertions.assertEquals(response.message(), "Error: bad request");
    }

    @Test
    public void joinGameTestPos(){
        new ClearAppService();
        String blackToken = new MemoryAuthDAO().createAuth("blackUsername");
        String whiteToken = new MemoryAuthDAO().createAuth("whiteUsername");
        Integer gameID = new MemoryGameDAO().createGame("gameName");
        new GameService().joinGame(ChessGame.TeamColor.BLACK, gameID, blackToken);
        new GameService().joinGame(ChessGame.TeamColor.WHITE, gameID, whiteToken);
        Assertions.assertEquals("blackUsername", new MemoryGameDAO().getGame("gameName").blackUsername());
        Assertions.assertEquals("whiteUsername", new MemoryGameDAO().getGame("gameName").whiteUsername());
    }
    @Test
    public void joinGameTestNeg(){
        new ClearAppService();
        String blackToken = new MemoryAuthDAO().createAuth("blackUsername");
        String anotherBlackToken = new MemoryAuthDAO().createAuth("anotherBlackUsername");
        Integer gameID = new MemoryGameDAO().createGame("gameName");
        new GameService().joinGame(ChessGame.TeamColor.BLACK, gameID, blackToken);
        EmptyResponse response = new GameService().joinGame(ChessGame.TeamColor.BLACK, gameID, anotherBlackToken);
        Assertions.assertEquals("Error: already taken", response.message());

    }

    @Test
    public void getGameListTestPos(){
        new ClearAppService().clearAll();
        String authToken = new MemoryAuthDAO().createAuth("username");
        new MemoryGameDAO().createGame("gameName1");
        new MemoryGameDAO().createGame("gameName2");
        new MemoryGameDAO().createGame("gameName3");
        List<GameData> gameList = List.of(new MemoryGameDAO().getGame("gameName1"),
                new MemoryGameDAO().getGame("gameName2"),
                new MemoryGameDAO().getGame("gameName3"));
        GameListResponse response = new GameService().getGameList(authToken);
        Assertions.assertEquals(gameList, response.games());
    }
    @Test
    public void getGameListTestNeg(){
        new ClearAppService().clearAll();
        String authToken = new MemoryAuthDAO().createAuth("username");
        GameListResponse response = new GameService().getGameList(null);
        Assertions.assertEquals("Error: unauthorized", response.message());
    }
}
