package dataAccess;

import model.GameData;

import javax.xml.crypto.Data;
import java.util.List;

public interface GameDAO {
    void createGame(GameData game) throws DataAccessException;
    GameData getGame(int id) throws DataAccessException;
    List<GameData> listGames() throws DataAccessException;
    void updateGame(GameData game) throws DataAccessException;
    void clear();
}
