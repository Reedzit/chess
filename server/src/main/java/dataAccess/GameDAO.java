package dataAccess;

import model.GameData;

import javax.xml.crypto.Data;
import java.util.List;

public interface GameDAO {
    Integer createGame(String gameName) throws DataAccessException;
    GameData getGame(String gameName) throws DataAccessException;
    List<GameData> listGames() throws DataAccessException;
    void updateGame(GameData game) throws DataAccessException;
    Integer createGameID();
    void clear();
}
