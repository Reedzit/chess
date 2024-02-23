package dataAccess;

import model.GameData;

import java.util.ArrayList;
import java.util.List;

public class MemoryGameDAO implements GameDAO{
    List<GameData> gameList = new ArrayList<>();
    @Override
    public void createGame(GameData game) {
        gameList.add(game);
    }

    @Override
    public GameData getGame(int id) {
        for (var curr : gameList){
            if (id == curr.gameID()){
                return curr;
            }
        }
        return null;
    }

    @Override
    public List<GameData> listGames() {
        return gameList;
    }

    @Override
    public void updateGame(GameData game) {
        for (var curr : gameList){
            if (game.gameID() == curr.gameID()){
                gameList.remove(curr);
                gameList.add(game);
                break;
            }
        }
    }
}
