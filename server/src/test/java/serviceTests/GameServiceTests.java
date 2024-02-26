package serviceTests;

import dataAccess.MemoryAuthDAO;
import org.junit.jupiter.api.Test;
import responses.CreateGameResponse;
import service.ClearAppService;
import service.GameService;

public class GameServiceTests {
    @Test
    public void createGameTest(){
        new ClearAppService();
        String authToken = new MemoryAuthDAO().createAuth("username");

        CreateGameResponse response = new GameService().createGame("gameName",authToken);

    }

    @Test
    public void joinGameTest(){

    }

    @Test
    public void getGameListTest(){

    }
}
