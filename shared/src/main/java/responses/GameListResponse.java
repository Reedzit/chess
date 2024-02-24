package responses;

import model.GameData;

import java.util.List;

public record GameListResponse(List<GameData> games, String message) {
}
