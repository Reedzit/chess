package dataAccess;

import model.GameData;

import javax.xml.crypto.Data;
import java.util.HashSet;
import java.util.List;

public interface GameDAO {
    Integer createGame(String gameName) throws DataAccessException;
    GameData getGame(String gameName) throws DataAccessException;
    String getGameName(Integer gameID) throws DataAccessException;
    HashSet<GameData> listGames() throws DataAccessException;
    void updateGame(GameData game) throws DataAccessException;
    void clear();
}
