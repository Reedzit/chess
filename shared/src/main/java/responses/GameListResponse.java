package responses;

import model.GameData;

import java.util.HashSet;
import java.util.List;

public record GameListResponse(HashSet<GameData> games, String message) {
}
