package chess;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {
    PieceType type;
    ChessGame.TeamColor pieceColor;


    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessPiece that = (ChessPiece) o;
        return type == that.type && pieceColor == that.pieceColor;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, pieceColor);
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return this.pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return this.type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {

//        if (type == PieceType.PAWN){
//            return pieceMovesPawn(board, position);
//        }
//        if(type == PieceType.QUEEN){
//            return pieceMoves();
//        }
//        if(type == PieceType.ROOK){
//            return pieceMoves();
//        }
//        if(type == PieceType.KNIGHT){
//            return pieceMoves(board, myPosition);
//        }
        if(type == PieceType.KING){
            return pieceMovesKing(board, myPosition);
        }
        if (type == PieceType.BISHOP) {
            return pieceMovesBishop(board, myPosition);
        }
        return new HashSet<ChessMove>();
    }

//    public Collection<ChessMove> pieceMovesPawn(ChessBoard board, ChessPosition myPosition){
//        Collection<ChessMove> possibleMoves = new HashSet<ChessMove>();
//        return possibleMoves;
//    }
    public Collection<ChessMove> pieceMovesBishop(ChessBoard board, ChessPosition myPosition){
        Collection<ChessMove> possibleMoves = new HashSet<ChessMove>();
        ChessPosition currentEndPos = new ChessPosition(myPosition.row, myPosition.col);
        int i = -7;
        while(i < 8){
            currentEndPos = new ChessPosition(myPosition.getRow()+i, myPosition.getColumn()+i);
            if((currentEndPos.getRow() <= 8 && currentEndPos.getRow() >= 1) && (currentEndPos.getColumn() <= 8 && currentEndPos.getColumn() >= 1) ) {
                if (board.getPiece(currentEndPos) == null || board.getPiece(currentEndPos).pieceColor != this.pieceColor) {
                    possibleMoves.add(new ChessMove(myPosition, currentEndPos, null));
                }
            }
            currentEndPos = new ChessPosition(myPosition.getRow()-i, myPosition.getColumn()+i);
            if((currentEndPos.getRow() <= 8 && currentEndPos.getRow() >= 1) && (currentEndPos.getColumn() <= 8 && currentEndPos.getColumn() >= 1) ) {
                if (board.getPiece(currentEndPos) == null || board.getPiece(currentEndPos).pieceColor != this.pieceColor) {
                    possibleMoves.add(new ChessMove(myPosition, currentEndPos, null));
                }
            }
            i = i + 1;
        }
        return possibleMoves;
    }
    public Collection<ChessMove> pieceMovesKing(ChessBoard board, ChessPosition myPosition){
        Collection<ChessMove> possibleMoves = new HashSet<ChessMove>();
        ChessPosition currentEndPos = new ChessPosition(myPosition.row+1, myPosition.col);
        if((currentEndPos.getRow() <= 8 && currentEndPos.getRow() >= 1) && (currentEndPos.getColumn() <= 8 && currentEndPos.getColumn() >= 1) ) {
            if (board.getPiece(currentEndPos) == null || board.getPiece(currentEndPos).pieceColor != this.pieceColor) {
                possibleMoves.add(new ChessMove(myPosition, currentEndPos, null));
            }
        }
        currentEndPos = new ChessPosition(myPosition.row, myPosition.col+1);
        if((currentEndPos.getRow() <= 8 && currentEndPos.getRow() >= 1) && (currentEndPos.getColumn() <= 8 && currentEndPos.getColumn() >= 1) ) {
            if (board.getPiece(currentEndPos) == null || board.getPiece(currentEndPos).pieceColor != this.pieceColor) {
                possibleMoves.add(new ChessMove(myPosition, currentEndPos, null));
            }
        }
        currentEndPos = new ChessPosition(myPosition.row-1, myPosition.col);
        if((currentEndPos.getRow() <= 8 && currentEndPos.getRow() >= 1) && (currentEndPos.getColumn() <= 8 && currentEndPos.getColumn() >= 1) ) {
            if (board.getPiece(currentEndPos) == null || board.getPiece(currentEndPos).pieceColor != this.pieceColor) {
                possibleMoves.add(new ChessMove(myPosition, currentEndPos, null));
            }
        }
        currentEndPos = new ChessPosition(myPosition.row, myPosition.col-1);
        if((currentEndPos.getRow() <= 8 && currentEndPos.getRow() >= 1) && (currentEndPos.getColumn() <= 8 && currentEndPos.getColumn() >= 1) ) {
            if (board.getPiece(currentEndPos) == null || board.getPiece(currentEndPos).pieceColor != this.pieceColor) {
                possibleMoves.add(new ChessMove(myPosition, currentEndPos, null));
            }
        }
        currentEndPos = new ChessPosition(myPosition.row-1, myPosition.col-1);
        if((currentEndPos.getRow() <= 8 && currentEndPos.getRow() >= 1) && (currentEndPos.getColumn() <= 8 && currentEndPos.getColumn() >= 1) ) {
            if (board.getPiece(currentEndPos) == null || board.getPiece(currentEndPos).pieceColor != this.pieceColor) {
                possibleMoves.add(new ChessMove(myPosition, currentEndPos, null));
            }
        }
        currentEndPos = new ChessPosition(myPosition.row+1, myPosition.col-1);
        if((currentEndPos.getRow() <= 8 && currentEndPos.getRow() >= 1) && (currentEndPos.getColumn() <= 8 && currentEndPos.getColumn() >= 1) ) {
            if (board.getPiece(currentEndPos) == null || board.getPiece(currentEndPos).pieceColor != this.pieceColor) {
                possibleMoves.add(new ChessMove(myPosition, currentEndPos, null));
            }
        }
        currentEndPos = new ChessPosition(myPosition.row-1, myPosition.col+1);
        if((currentEndPos.getRow() <= 8 && currentEndPos.getRow() >= 1) && (currentEndPos.getColumn() <= 8 && currentEndPos.getColumn() >= 1) ) {
            if (board.getPiece(currentEndPos) == null || board.getPiece(currentEndPos).pieceColor != this.pieceColor) {
                possibleMoves.add(new ChessMove(myPosition, currentEndPos, null));
            }
        }
        currentEndPos = new ChessPosition(myPosition.row+1, myPosition.col+1);
        if((currentEndPos.getRow() <= 8 && currentEndPos.getRow() >= 1) && (currentEndPos.getColumn() <= 8 && currentEndPos.getColumn() >= 1) ) {
            if (board.getPiece(currentEndPos) == null || board.getPiece(currentEndPos).pieceColor != this.pieceColor) {
                possibleMoves.add(new ChessMove(myPosition, currentEndPos, null));
            }
        }
        return possibleMoves;
    }
}
