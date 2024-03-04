package dataAccess;

import model.GameData;

import java.util.HashSet;

public class DbGameDAO implements GameDAO {
    @Override
    public Integer createGame(String gameName) throws DataAccessException {
        return null;
    }

    @Override
    public GameData getGame(String gameName) throws DataAccessException {
        return null;
    }

    @Override
    public String getGameName(Integer gameID) throws DataAccessException {
        return null;
    }

    @Override
    public HashSet<GameData> listGames() throws DataAccessException {
        return null;
    }

    @Override
    public void updateGame(GameData game) throws DataAccessException {

    }

    @Override
    public void clear() {
//        updateGame("TRUNCATE games");
    }
}
