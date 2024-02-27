package dataAccess;

import chess.ChessGame;
import model.GameData;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class MemoryGameDAO implements GameDAO{
    static final private HashSet<GameData> gamesSet = new HashSet<>();
    @Override
    public Integer createGame(String gameName) {
        GameData game = new GameData(gamesSet.size()+1,null, null, gameName, new ChessGame());
        gamesSet.add(game);
        return game.gameID();
    }

    @Override
    public GameData getGame(String gameName) {
        for (var curr: gamesSet){
            if (gameName.equals(curr.gameName())){
                return curr;
            }
        }
        return null;
    }

    @Override
    public String getGameName(Integer gameID)  {
        for (var curr : gamesSet){
            if (gameID.equals(curr.gameID())){
                return curr.gameName();
            }
        }
        return null;
    }

    @Override
    public HashSet<GameData> listGames() {
        return gamesSet;
    }

    @Override
    public void updateGame(GameData game) {
        for (var curr : gamesSet){
            if (game.gameID() == curr.gameID()){
                gamesSet.remove(curr);
                gamesSet.add(game);
                break;
            }
        }
    }
    @Override
    public void clear() {
        gamesSet.clear();
    }
}
