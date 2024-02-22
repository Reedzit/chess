package dataAccess;

import model.GameData;

import java.util.List;

public interface GameDAO {
    void createGame(GameData game);
    GameData getGame(GameData game);
    List<GameData> listGames();
    void updateGame(GameData game);
}
