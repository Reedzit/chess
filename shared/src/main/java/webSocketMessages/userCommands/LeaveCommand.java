package webSocketMessages.userCommands;

public class LeaveCommand extends UserGameCommand{
    Integer gameID;
    public LeaveCommand(String authToken, Integer gameID) {
        super(authToken);
        this.gameID = gameID;
    }
}
