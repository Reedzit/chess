package chess;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
        public boolean gameOver;
        TeamColor currentTurn;
        ChessBoard currentBoard;
    public ChessGame() {
        setTeamTurn(TeamColor.WHITE);
        ChessBoard board = new ChessBoard();
        board.resetBoard();
        setBoard(board);
        gameOver = false;
    }
    public void gameOver(){
        gameOver = true;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessGame chessGame = (ChessGame) o;
        return currentTurn == chessGame.currentTurn && Objects.equals(currentBoard, chessGame.currentBoard);
    }

    @Override
    public int hashCode() {
        return Objects.hash(currentTurn, currentBoard);
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
                copyGame.getBoard().addPiece(move.getEndPosition(), new ChessPiece(copyGame.getBoard().getPiece(move.getStartPosition()).getTeamColor(),copyGame.getBoard().getPiece(startPosition).getPieceType()));
                copyGame.getBoard().addPiece(startPosition, null);
                if (!copyGame.isInCheck(getBoard().getPiece(startPosition).getTeamColor())) {
                    movesThatWork.add(move);
                }
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
    public void makeMove(ChessMove move) throws InvalidMoveException{
//        if (gameOver){
//            throw new GameOverException("Error: this game has ended");
//        }
        if (getBoard().getPiece(move.getStartPosition()).getTeamColor()== this.getTeamTurn() && validMoves(move.getStartPosition()).contains(move)){
            ChessPiece startPiece = getBoard().getPiece(move.getStartPosition());
            if (this.getBoard().getPiece(move.getStartPosition()).getPieceType() == ChessPiece.PieceType.PAWN && move.getPromotionPiece() != null){     //logic for promotion piece
                if (this.getBoard().getPiece(move.getStartPosition()).getTeamColor() == TeamColor.WHITE && move.getEndPosition().getRow() == 8){
                    this.getBoard().addPiece(move.getEndPosition(), new ChessPiece(startPiece.getTeamColor(), move.getPromotionPiece()));
                    this.getBoard().addPiece(move.getStartPosition(), null);
                }else if (this.getBoard().getPiece(move.getStartPosition()).getTeamColor() == TeamColor.BLACK && move.getEndPosition().getRow() == 1){
                    this.getBoard().addPiece(move.getEndPosition(), new ChessPiece(startPiece.getTeamColor(), move.getPromotionPiece()));
                    this.getBoard().addPiece(move.getStartPosition(), null);
                }
            }else {
                this.getBoard().addPiece(move.getEndPosition(), startPiece);
                this.getBoard().addPiece(move.getStartPosition(), null);
            }
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
                if (getBoard().getPiece(currPos).getTeamColor() != teamColor) {
                    potentialKingKillers.addAll(this.getBoard().getPiece(currPos).pieceMoves(this.getBoard(), currPos));
                }
                if (getBoard().getPiece(currPos).getPieceType() == ChessPiece.PieceType.KING && teamColor == getBoard().getPiece(currPos).getTeamColor()) {
                    kingPosition = currPos;
                }
            }
        }
        if (kingPosition != null) {
            for (ChessMove move : potentialKingKillers) {
                if (getBoard().getPiece(move.getStartPosition()).getTeamColor() != teamColor && move.getEndPosition().getRow() == kingPosition.getRow() && move.getEndPosition().getColumn() == kingPosition.getColumn()) {
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
        Collection<ChessMove> allMoves = new HashSet<>();
        for ( int i = 0; i < 8; i++){
            for ( int j = 0; j < 8; j++){
                if (getBoard().board[i][j] == null) continue;
                ChessPosition currPos = new ChessPosition(i+1,j+1);
                if (teamColor == getBoard().getPiece(currPos).getTeamColor()) {
                    allMoves.addAll(getBoard().getPiece(currPos).pieceMoves(getBoard(), currPos));
                }
            }
        }
        int checkCounter = 0;
        for (ChessMove move : allMoves){
            ChessGame currGame = this.copyGame();
            currGame.getBoard().addPiece(move.getEndPosition(), currGame.getBoard().getPiece(move.getStartPosition()));
            currGame.getBoard().addPiece(move.getStartPosition(), null);
            if (currGame.isInCheck(teamColor)){
               checkCounter += 1;
            }
        }
        return allMoves.size() == checkCounter;
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
                ChessPosition currPosition = new ChessPosition(i + 1, j + 1);
                if (getBoard().getPiece(currPosition) != null) {
                    if (getBoard().getPiece(currPosition).getTeamColor() == teamColor) {
                        potentialMoves.addAll(validMoves(currPosition));
                    }
                }
            }
        }
        return potentialMoves.isEmpty() && getTeamTurn() == teamColor;
    }

    public ChessGame copyGame(){
        ChessGame gameCopy = new ChessGame();
        gameCopy.setTeamTurn(currentTurn);
        gameCopy.setBoard(this.getBoard().copyBoard());
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
