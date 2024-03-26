package clientTests;

import dataAccess.DataAccessException;
import dataAccess.DbGameDAO;
import exception.ResponseException;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.*;
import requests.LoginRequest;
import responses.CreateGameResponse;
import responses.LoginResponse;
import server.Server;
import service.ClearAppService;
import service.GameService;
import service.UserService;
import ui.ServerFacade;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertTrue;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;
    public ServerFacadeTests() {
    }

    @BeforeAll
    public static void init() throws DataAccessException {
        server = new Server();
        var port = server.run(0);
        new ClearAppService().clearAll();
        facade = new ServerFacade(String.format("http://localhost:%s", port));
        System.out.println("Started test HTTP server on " + port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    public void sampleTest() {
        assertTrue(true);
    }

    @Test
    void registerPos() throws Exception {
        var authData = facade.register(new String[] {"player1", "password", "p1@email.com"});
        assertTrue(authData.authToken().length() > 10);
    }
    @Test
    void registerNeg() {
        Assertions.assertThrows(ResponseException.class, () -> facade.register(new String[] {"player1", "password", null}));
    }
    @Test
    void loginPos() throws Exception {
        new UserService().register(new UserData("username", "password", "emial"));
        var response = facade.login(new String[] {"username", "password"});
        Assertions.assertNull(response.message());
    }
    @Test
    void loginNeg() {
        Assertions.assertThrows(ResponseException.class, () -> facade.login(new String[]{"notvalidusername", "password"}));
    }
    @Test
    void logoutPos() throws Exception {
        new UserService().register(new UserData("username", "password", "emial"));
        LoginResponse response = new UserService().login(new LoginRequest("username", "password"));
        facade.authToken = response.authToken();
//        facade.login(new String[] {"username", "password"});
        facade.logout();
        Assertions.assertNull(facade.authToken);
    }
    @Test
    void logoutNeg() {
        Assertions.assertThrows(ResponseException.class, () -> facade.logout());
    }
    @Test
    void listGamesPos() throws Exception {
        new UserService().register(new UserData("username", "password", "emial"));
        LoginResponse response = new UserService().login(new LoginRequest("username", "password"));
        GameService gameService = new GameService();
        gameService.createGame("game1", response.authToken());
        gameService.createGame("game2", response.authToken());
        gameService.createGame("game3", response.authToken());
        HashSet<GameData> gamesComparer = gameService.getGameList(response.authToken()).games();
        facade.authToken = response.authToken();
        HashSet<GameData> games = facade.listGames();
        Assertions.assertEquals(gamesComparer, games);
    }
    @Test
    void listGamesNeg() {
        Assertions.assertThrows(ResponseException.class, () -> facade.listGames());
    }
    @Test
    void createGamePos() throws Exception {
        new UserService().register(new UserData("username", "password", "emial"));
        LoginResponse response = new UserService().login(new LoginRequest("username", "password"));
        facade.authToken = response.authToken();
        Assertions.assertEquals(new CreateGameResponse(1, null), facade.createGame("gameName"));
    }
    @Test
    void createGameNeg() {
        facade.authToken = null;
        Assertions.assertThrows(ResponseException.class, () -> facade.createGame("gameName"));
    }
    @Test
    void joinGamePos() throws Exception {
        new UserService().register(new UserData("username", "password", "emial"));
        LoginResponse response = new UserService().login(new LoginRequest("username", "password"));
        GameService gameService = new GameService();
        gameService.createGame("game", response.authToken());
        facade.authToken = response.authToken();
        facade.joinGame(new String[] {"WHITE", "1"});
        Assertions.assertEquals("username", new DbGameDAO().getGame("game").whiteUsername());
    }
    @Test
    void joinGameNeg() {
        Assertions.assertThrows(ResponseException.class, () -> facade.joinGame(new String[] {"username",  "12"}));
    }
    @Test
    void observeGamePos() throws  Exception {
        new UserService().register(new UserData("username", "password", "emial"));
        LoginResponse response = new UserService().login(new LoginRequest("username", "password"));
        GameService gameService = new GameService();
        gameService.createGame("game", response.authToken());
        facade.authToken = response.authToken();
        facade.observeGame("1");
        Assertions.assertNull(new DbGameDAO().getGame("game").blackUsername());
        Assertions.assertNull(new DbGameDAO().getGame("game").whiteUsername());
    }
    @Test
    void observeGameNeg() {
        Assertions.assertThrows(ResponseException.class, () -> facade.observeGame("12"));
    }
}
