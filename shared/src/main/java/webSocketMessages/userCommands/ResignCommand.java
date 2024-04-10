package webSocketMessages.userCommands;

public class ResignCommand extends UserGameCommand{
    Integer gameID;
    public ResignCommand(String authToken, Integer gameID) {
        super(authToken);
        this.gameID = gameID;
        this.commandType = CommandType.RESIGN;
    }

    public Integer getGameID() {
        return gameID;
    }
}
