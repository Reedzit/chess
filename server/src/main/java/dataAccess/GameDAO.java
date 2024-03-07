package dataAccess;

import model.GameData;
import java.util.HashSet;

public interface GameDAO {
    Integer createGame(String gameName) throws DataAccessException;
    GameData getGame(String gameName) throws DataAccessException;
    String getGameName(Integer gameID) throws DataAccessException;
    HashSet<GameData> listGames() throws DataAccessException;
    void updateGame(GameData game) throws DataAccessException;
    void clear() throws DataAccessException;
}
