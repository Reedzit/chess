package chess;

import java.util.Collection;
import java.util.HashSet;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

        TeamColor currentTurn;
        ChessBoard currentBoard;
    public ChessGame() {
        setTeamTurn(TeamColor.WHITE);
        setBoard(new ChessBoard());
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return currentTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        this.currentTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        HashSet<ChessMove> movesThatWork = new HashSet<>();
        if(getBoard().getPiece(startPosition) != null) {
            Collection<ChessMove> possibleMoves = getBoard().getPiece(startPosition).pieceMoves(getBoard(), startPosition);
            for (ChessMove move : possibleMoves){
                ChessGame copyGame = this.copyGame();
                copyGame.getBoard().addPiece(move.getEndPosition(), copyGame.getBoard().getPiece(startPosition));
                copyGame.getBoard().addPiece(startPosition, null);
                if (copyGame.isInCheck(this.currentTurn)) continue;
                movesThatWork.add(move);
                //logic for each condition;
            }
        }
        return movesThatWork;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        if (validMoves(move.getStartPosition()).contains(move)){
            ChessPiece startPiece = getBoard().getPiece(move.getStartPosition());
            this.getBoard().addPiece(move.getEndPosition(), startPiece);
            this.getBoard().addPiece(move.getStartPosition(), null);

            if (this.currentTurn == TeamColor.WHITE){
                this.currentTurn = TeamColor.BLACK;
            }else {
                this.currentTurn = TeamColor.WHITE;
            }
        }else {
            throw new InvalidMoveException("Cannot make this move");
        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        Collection<ChessMove> potentialKingKillers = new HashSet<>();
        ChessPosition kingPosition = null;
        for ( int i = 0; i < 8; i++){
            for ( int j = 0; j < 8; j++){
                if (getBoard().board[i][j] == null) continue;
                ChessPosition currPos = new ChessPosition(i+1,j+1);
                potentialKingKillers.addAll(this.getBoard().getPiece(currPos).pieceMoves(this.getBoard(), currPos));
                if (getBoard().getPiece(currPos).type == ChessPiece.PieceType.KING && teamColor == getBoard().getPiece(currPos).pieceColor) {
                    kingPosition = new ChessPosition(i+1,j+1);
                }
            }
        }
        if (kingPosition != null) {
            for (ChessMove move : potentialKingKillers) {
                if (move.getEndPosition().getRow() == kingPosition.getRow() && move.getEndPosition().getColumn() == kingPosition.getColumn()) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        // get king position
        ChessPosition kingPosition = null;
        boardLoop:
        for ( int i = 0; i < 8; i++){
            for ( int j = 0; j < 8; j++){
                if (getBoard().board[i][j] == null) continue;
                ChessPosition currPos = new ChessPosition(i+1,j+1);
                if (getBoard().board[currPos.getRow()-1][currPos.getColumn()-1].type == ChessPiece.PieceType.KING && teamColor == getBoard().board[currPos.getRow()-1][currPos.getColumn()-1].pieceColor) {
                    kingPosition = new ChessPosition(i+1,j+1);
                    break boardLoop;
                }
            }
        }
        // get set of moves for the king
         Collection<ChessMove> validKingMoves = validMoves(kingPosition);
        //make deep copy of current board
        int checkCounter = 0;
        for (ChessMove move : validKingMoves){
            ChessGame currGame = this.copyGame();
            currGame.getBoard().board[move.getStartPosition().getRow()][move.getStartPosition().getColumn()] = null;
            currGame.getBoard().board[move.getEndPosition().getRow()][move.getEndPosition().getColumn()] = new ChessPiece(teamColor, ChessPiece.PieceType.KING);
            if (currGame.isInCheck(this.currentTurn)){
               checkCounter += 1;                                         //this board move puts the king in check
            }
        }
        return validKingMoves.size() == checkCounter;
        // if all boards with simulated moves are in check then return true for checkmate.
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        Collection<ChessMove> potentialMoves = new HashSet<>();
        for ( int i = 0; i < 8; i++){
            for ( int j = 0; j < 8; j++){
                if (getBoard().board[i][j] == null) continue;
                if (getBoard().board[i][i].pieceColor == teamColor) {
                    potentialMoves.addAll(this.validMoves(new ChessPosition(i + 1, j + 1)));
                }
            }
        }
        return potentialMoves.isEmpty();
    }

    public ChessGame copyGame(){
        ChessGame gameCopy = new ChessGame();
        gameCopy.currentTurn = this.currentTurn;
        gameCopy.currentBoard = this.getBoard().copyBoard();
        return gameCopy;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.currentBoard = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return this.currentBoard;
    }
}
