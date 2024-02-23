package dataAccess;

import model.GameData;

import java.util.ArrayList;
import java.util.List;

public class MemoryGameDAO implements GameDAO{
    static final private List<GameData> games = new ArrayList<>();
    @Override
    public void createGame(GameData game) {
        games.add(game);
    }

    @Override
    public GameData getGame(int id) {
        for (var curr : games){
            if (id == curr.gameID()){
                return curr;
            }
        }
        return null;
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
    public void clear() {
        games.clear();
    }
}
