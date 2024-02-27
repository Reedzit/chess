package dataAccess;

import chess.ChessGame;
import model.GameData;

import java.util.ArrayList;
import java.util.List;

public class MemoryGameDAO implements GameDAO{
    static final private List<GameData> games = new ArrayList<>();
    static private Integer nextGameID = 0;
    @Override
    public Integer createGame(String gameName) {
        GameData game = new GameData(games.size()+1,null, null, gameName, new ChessGame());
        games.add(game);
        return game.gameID();
    }

    @Override
    public GameData getGame(String gameName) {
        for (var curr : games){
            if (gameName.equals(curr.gameName())){
                return curr;
            }
        }
        return null;
    }

    @Override
    public String getGameName(Integer gameID)  {
        try {
            return games.get(gameID-1).gameName();
        }catch (ArrayIndexOutOfBoundsException e) {
                return null;
        }
    }

    @Override
    public List<GameData> listGames() {
        return games;
    }

    @Override
    public void updateGame(GameData game) {
        for (var curr : games){
            if (game.gameID() == curr.gameID()){
                games.remove(curr);
                games.add(game);
                break;
            }
        }
    }

    @Override
    public Integer createGameID() {
        nextGameID++;
        return nextGameID;
    }

    @Override
    public void clear() {
        games.clear();
    }
}
