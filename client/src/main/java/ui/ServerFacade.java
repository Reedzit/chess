package ui;

import chess.ChessGame;
import com.google.gson.Gson;
import exception.ResponseException;
import model.GameData;
import model.UserData;
import requests.CreateGameRequest;
import requests.JoinGameRequest;
import requests.LoginRequest;
import responses.CreateGameResponse;
import responses.GameListResponse;
import responses.LoginResponse;
import responses.RegisterResponse;

import java.io.*;
import java.net.*;
import java.util.HashSet;
import java.util.Objects;

public class ServerFacade {

    private final String serverUrl;
    public String authToken = null;

    public ServerFacade(String url) {
        this.serverUrl = url;
    }

    public RegisterResponse register(String[] params) throws ResponseException {
        var path = "/user";
        var response =  this.makeRequest("POST", path, new UserData(params[0],params[1],params[2]) ,RegisterResponse.class);
        authToken = response.authToken();
        return response;
    }
    public LoginResponse login(String[] params) throws ResponseException {
        var path = "/session";
        var response = this.makeRequest("POST", path, new LoginRequest(params[0], params[1]), LoginResponse.class);
        authToken = response.authToken();
        return response;
    }

    public void logout() throws ResponseException {
        var path = "/session";
        this.makeRequest("DELETE", path, null, null);
        authToken = null;
    }

    public HashSet<GameData> listGames() throws ResponseException {
        var path = "/game";
        var response = this.makeRequest("GET", path, null, GameListResponse.class);
        return response.games();
    }

    public CreateGameResponse createGame(String gameName) throws ResponseException {
        var path = "/game";
        return this.makeRequest("GET", path, new CreateGameRequest(gameName), CreateGameResponse.class);
    }
    public void joinGame(String[] params) throws ResponseException {
        var path =  "/game";
        if (Objects.equals(params[1], "WHITE")){
            this.makeRequest("PUT", path, new JoinGameRequest(ChessGame.TeamColor.WHITE, Integer.parseInt(params[0])), null);
        }else {
            this.makeRequest("PUT", path, new JoinGameRequest(ChessGame.TeamColor.BLACK, Integer.parseInt(params[0])), null);
        }
    }

    public void observeGame(String ... params) throws ResponseException {
        var path =  "/game";
        this.makeRequest("PUT",path, new JoinGameRequest(null, Integer.parseInt(params[0])), null);
    }

    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass) throws ResponseException {
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);
            http.addRequestProperty("authorization", authToken);
            writeBody(request, http);
            http.connect();
            throwIfNotSuccessful(http);
            return readBody(http, responseClass);
        } catch (Exception ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }


    private static void writeBody(Object request, HttpURLConnection http) throws IOException {
        if (request != null) {
            http.addRequestProperty("Content-Type", "application/json");
            String reqData = new Gson().toJson(request);
            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(reqData.getBytes());
            }
        }
    }

    private void throwIfNotSuccessful(HttpURLConnection http) throws IOException, ResponseException {
        var status = http.getResponseCode();
        if (!isSuccessful(status)) {
            throw new ResponseException(status, "failure: " + status);
        }
    }

    private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        T response = null;
        if (http.getContentLength() < 0) {
            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(respBody);
                if (responseClass != null) {
                    response = new Gson().fromJson(reader, responseClass);
                }
            }
        }
        return response;
    }

    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }
}