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
        if(getBoard().getPiece(startPosition) != null) {
            Collection<ChessMove> possibleMoves = getBoard().getPiece(startPosition).pieceMoves(getBoard(), startPosition);
            for (ChessMove move : possibleMoves){
                //logic for each condition;
                return null;
            }
        }
        return null;
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
            this.getBoard().board[move.getStartPosition().getRow()][move.getStartPosition().getColumn()] = null;
            this.getBoard().board[move.getEndPosition().getRow()][move.getEndPosition().getColumn()] = new ChessPiece(startPiece.pieceColor, startPiece.type);
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
                potentialKingKillers.addAll(this.validMoves(currPos));
                if (getBoard().board[currPos.getRow()][currPos.getColumn()].type == ChessPiece.PieceType.KING && teamColor == getBoard().board[currPos.getRow()][currPos.getColumn()].pieceColor) {
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
                if (getBoard().board[currPos.getRow()][currPos.getColumn()].type == ChessPiece.PieceType.KING && teamColor == getBoard().board[currPos.getRow()][currPos.getColumn()].pieceColor) {
                    kingPosition = new ChessPosition(i+1,j+1);
                    break boardLoop;
                }
            }
        }
        // get set of moves for the king
         Collection<ChessMove> validKingMoves = validMoves(kingPosition);
        //make deep copy of current board
        for (ChessMove move : validKingMoves){
            ChessBoard currBoard = getBoard().copyBoard();
        }
        // if all boards with simulated moves are in check then return true for checkmate.

        return false;
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
