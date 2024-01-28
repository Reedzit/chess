package chess;

import java.util.*;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {
    PieceType type;
    ChessGame.TeamColor pieceColor;
    Collection<PieceType> promotionTypes = List.of(PieceType.ROOK, PieceType.KNIGHT, PieceType.BISHOP, PieceType.QUEEN);

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

        if (type == PieceType.PAWN){
            return pieceMovesPawn(board, myPosition);
        }
        if(type == PieceType.QUEEN){
            Collection<ChessMove> possibleMoves;
            possibleMoves = pieceMovesBishop(board, myPosition);
            possibleMoves.addAll(pieceMovesRook(board,myPosition));
            return possibleMoves;
        }
        if(type == PieceType.ROOK){
            return pieceMovesRook(board, myPosition);
        }
        if(type == PieceType.KNIGHT){
            return pieceMovesKnight(board, myPosition);
        }
        if(type == PieceType.KING){
            return pieceMovesKing(board, myPosition);
        }
        if (type == PieceType.BISHOP) {
            return pieceMovesBishop(board, myPosition);
        }
        return new HashSet<>();
    }

    public Collection<ChessMove> pieceMovesPawn(ChessBoard board, ChessPosition myPosition){
        Collection<ChessMove> possibleMoves = new HashSet<>();
        //logic for white pawns
        if (this.pieceColor == ChessGame.TeamColor.WHITE){
            if(myPosition.getRow() == 2) {
                ChessPosition currEndPosition = new ChessPosition(myPosition.getRow() + 2, myPosition.getColumn());
                if(board.getPiece(currEndPosition) == null && board.getPiece(new ChessPosition(myPosition.getRow() +1,myPosition.getColumn())) == null) {
                    possibleMoves.add(new ChessMove(myPosition, currEndPosition, null));
                }
            }
            ChessPosition currEndPosition = new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn());
            if(currEndPosition.getRow() == 8){
                for(var piece : this.promotionTypes){
                    possibleMoves.add(new ChessMove(myPosition, currEndPosition, piece));
                }
            }else if(board.getPiece(currEndPosition) == null){
                possibleMoves.add(new ChessMove(myPosition, currEndPosition, null));
            }
            // attack on right
            currEndPosition = new ChessPosition(myPosition.getRow()+1, myPosition.getColumn()+1);
            if(board.getPiece(currEndPosition) != null && board.getPiece(currEndPosition).pieceColor == ChessGame.TeamColor.BLACK){
                if(currEndPosition.getRow() == 8) {
                    for (var piece : this.promotionTypes) {
                        possibleMoves.add(new ChessMove(myPosition, currEndPosition, piece));
                    }
                }else {
                    possibleMoves.add(new ChessMove(myPosition, currEndPosition, null));
                }
            }
            //attack on left
            currEndPosition = new ChessPosition(myPosition.getRow()+1, myPosition.getColumn()-1);
            if(board.getPiece(currEndPosition) != null && board.getPiece(currEndPosition).pieceColor == ChessGame.TeamColor.BLACK){
                if(currEndPosition.getRow() == 8) {
                    for (var piece : this.promotionTypes) {
                        possibleMoves.add(new ChessMove(myPosition, currEndPosition, piece));
                    }
                }else {
                    possibleMoves.add(new ChessMove(myPosition, currEndPosition, null));
                }
            }
        }
        //logic for black pawns.
        else{
            if(myPosition.getRow() == 7) {
                ChessPosition currEndPosition = new ChessPosition(myPosition.getRow() - 2, myPosition.getColumn());
                if(board.getPiece(currEndPosition) == null && board.getPiece(new ChessPosition(myPosition.getRow() -1,myPosition.getColumn())) == null) {
                    possibleMoves.add(new ChessMove(myPosition, currEndPosition, null));
                }
            }
            ChessPosition currEndPosition = new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn());
            if(currEndPosition.getRow() == 1){
                for(var piece : this.promotionTypes){
                    possibleMoves.add(new ChessMove(myPosition, currEndPosition, piece));
                }
            }else if(board.getPiece(currEndPosition) == null){
                possibleMoves.add(new ChessMove(myPosition, currEndPosition, null));
            }
            // attack on right
            currEndPosition = new ChessPosition(myPosition.getRow()-1, myPosition.getColumn()+1);
            if(board.getPiece(currEndPosition) != null && board.getPiece(currEndPosition).pieceColor == ChessGame.TeamColor.WHITE){
                if(currEndPosition.getRow() == 1) {
                    for (var piece : this.promotionTypes) {
                        possibleMoves.add(new ChessMove(myPosition, currEndPosition, piece));
                    }
                }else {
                    possibleMoves.add(new ChessMove(myPosition, currEndPosition, null));
                }
            }
            //attack on left
            currEndPosition = new ChessPosition(myPosition.getRow()-1, myPosition.getColumn()-1);
            if(board.getPiece(currEndPosition) != null && board.getPiece(currEndPosition).pieceColor == ChessGame.TeamColor.WHITE){
                if(currEndPosition.getRow() == 1) {
                    for (var piece : this.promotionTypes) {
                        possibleMoves.add(new ChessMove(myPosition, currEndPosition, piece));
                    }
                }else {
                    possibleMoves.add(new ChessMove(myPosition, currEndPosition, null));
                }
            }
        }
        return possibleMoves;
    }
    public Collection<ChessMove> pieceMovesRook(ChessBoard board, ChessPosition myPosition){
        Collection<ChessMove> possibleMoves = new HashSet<>();
        //this is going up
        for(int i = myPosition.getRow()-1; i>0; i--){
            ChessPosition currEndPosition = new ChessPosition(i, myPosition.getColumn());
            if(board.getPiece(currEndPosition) != null){
                if(board.getPiece(currEndPosition).pieceColor != this.pieceColor) {
                    possibleMoves.add(new ChessMove(myPosition, currEndPosition, null));
                }
                break;
            }else{
                possibleMoves.add(new ChessMove(myPosition, currEndPosition, null));
            }
        }
        //this is going down
        for(int i = myPosition.getRow()+1; i<=8; i++){
            ChessPosition currEndPosition = new ChessPosition(i, myPosition.getColumn());
            if(board.getPiece(currEndPosition) != null){
                if(board.getPiece(currEndPosition).pieceColor != this.pieceColor) {
                    possibleMoves.add(new ChessMove(myPosition, currEndPosition, null));
                }
                break;
            }else{
                possibleMoves.add(new ChessMove(myPosition, currEndPosition, null));
            }
        }
        // this goes left
        for(int i = myPosition.getColumn()-1; i>0; i--){
            ChessPosition currEndPosition = new ChessPosition(myPosition.getRow(), i);
            if(board.getPiece(currEndPosition) != null){
                if(board.getPiece(currEndPosition).pieceColor != this.pieceColor) {
                    possibleMoves.add(new ChessMove(myPosition, currEndPosition, null));
                }
                break;
            }else{
                possibleMoves.add(new ChessMove(myPosition, currEndPosition, null));
            }
        }
        // this goes right
        for(int i = myPosition.getColumn()+1; i<=8; i++){
            ChessPosition currEndPosition = new ChessPosition(myPosition.getRow(), i);
            if(board.getPiece(currEndPosition) != null){
                if(board.getPiece(currEndPosition).pieceColor != this.pieceColor) {
                    possibleMoves.add(new ChessMove(myPosition, currEndPosition, null));
                }
                break;
            }else{
                possibleMoves.add(new ChessMove(myPosition, currEndPosition, null));
            }
        }
        return possibleMoves;
    }
    public Collection<ChessMove> pieceMovesKnight(ChessBoard board, ChessPosition myPosition){
        Collection<ChessMove> possibleMoves = new HashSet<>();
        ChessPosition currentEndPos = new ChessPosition(myPosition.row+1, myPosition.col+2);
        if((currentEndPos.getRow() <= 8 && currentEndPos.getRow() >= 1) && (currentEndPos.getColumn() <= 8 && currentEndPos.getColumn() >= 1) ) {
            if (board.getPiece(currentEndPos) == null || board.getPiece(currentEndPos).pieceColor != this.pieceColor) {
                possibleMoves.add(new ChessMove(myPosition, currentEndPos, null));
            }
        }
        currentEndPos = new ChessPosition(myPosition.row-1, myPosition.col+2);
        if((currentEndPos.getRow() <= 8 && currentEndPos.getRow() >= 1) && (currentEndPos.getColumn() <= 8 && currentEndPos.getColumn() >= 1) ) {
            if (board.getPiece(currentEndPos) == null || board.getPiece(currentEndPos).pieceColor != this.pieceColor) {
                possibleMoves.add(new ChessMove(myPosition, currentEndPos, null));
            }
        }
        currentEndPos = new ChessPosition(myPosition.row-1, myPosition.col-2);
        if((currentEndPos.getRow() <= 8 && currentEndPos.getRow() >= 1) && (currentEndPos.getColumn() <= 8 && currentEndPos.getColumn() >= 1) ) {
            if (board.getPiece(currentEndPos) == null || board.getPiece(currentEndPos).pieceColor != this.pieceColor) {
                possibleMoves.add(new ChessMove(myPosition, currentEndPos, null));
            }
        }
        currentEndPos = new ChessPosition(myPosition.row+1, myPosition.col-2);
        if((currentEndPos.getRow() <= 8 && currentEndPos.getRow() >= 1) && (currentEndPos.getColumn() <= 8 && currentEndPos.getColumn() >= 1) ) {
            if (board.getPiece(currentEndPos) == null || board.getPiece(currentEndPos).pieceColor != this.pieceColor) {
                possibleMoves.add(new ChessMove(myPosition, currentEndPos, null));
            }
        }

        currentEndPos = new ChessPosition(myPosition.row+2, myPosition.col+1);
        if((currentEndPos.getRow() <= 8 && currentEndPos.getRow() >= 1) && (currentEndPos.getColumn() <= 8 && currentEndPos.getColumn() >= 1) ) {
            if (board.getPiece(currentEndPos) == null || board.getPiece(currentEndPos).pieceColor != this.pieceColor) {
                possibleMoves.add(new ChessMove(myPosition, currentEndPos, null));
            }
        }
        currentEndPos = new ChessPosition(myPosition.row+2, myPosition.col-1);
        if((currentEndPos.getRow() <= 8 && currentEndPos.getRow() >= 1) && (currentEndPos.getColumn() <= 8 && currentEndPos.getColumn() >= 1) ) {
            if (board.getPiece(currentEndPos) == null || board.getPiece(currentEndPos).pieceColor != this.pieceColor) {
                possibleMoves.add(new ChessMove(myPosition, currentEndPos, null));
            }
        }
        currentEndPos = new ChessPosition(myPosition.row-2, myPosition.col+1);
        if((currentEndPos.getRow() <= 8 && currentEndPos.getRow() >= 1) && (currentEndPos.getColumn() <= 8 && currentEndPos.getColumn() >= 1) ) {
            if (board.getPiece(currentEndPos) == null || board.getPiece(currentEndPos).pieceColor != this.pieceColor) {
                possibleMoves.add(new ChessMove(myPosition, currentEndPos, null));
            }
        }
        currentEndPos = new ChessPosition(myPosition.row-2, myPosition.col-1);
        if((currentEndPos.getRow() <= 8 && currentEndPos.getRow() >= 1) && (currentEndPos.getColumn() <= 8 && currentEndPos.getColumn() >= 1) ) {
            if (board.getPiece(currentEndPos) == null || board.getPiece(currentEndPos).pieceColor != this.pieceColor) {
                possibleMoves.add(new ChessMove(myPosition, currentEndPos, null));
            }
        }

        return possibleMoves;
    }
    public Collection<ChessMove> pieceMovesBishop(ChessBoard board, ChessPosition myPosition){
        Collection<ChessMove> possibleMoves = new HashSet<>();
        ChessPosition currentEndPos;
        int i = 1;
        int pieceFlag1 = 0;
        int pieceFlag2 = 0;
        int pieceFlag3 = 0;
        int pieceFlag4 = 0;
        while(i < 8){
            currentEndPos = new ChessPosition(myPosition.getRow()+i, myPosition.getColumn()+i);
            if((currentEndPos.getRow() <= 8 && currentEndPos.getRow() >= 1) && (currentEndPos.getColumn() <= 8 && currentEndPos.getColumn() >= 1 && pieceFlag1 == 0) ) {
                if (board.getPiece(currentEndPos) == null) {
                    possibleMoves.add(new ChessMove(myPosition, currentEndPos, null));
                }
                if(board.getPiece(currentEndPos) != null){
                    pieceFlag1 = 1;
                    if(board.getPiece(currentEndPos).pieceColor != this.pieceColor) {
                        possibleMoves.add(new ChessMove(myPosition, currentEndPos, null));
                    }
                }
            }
            currentEndPos = new ChessPosition(myPosition.getRow()-i, myPosition.getColumn()+i);
            if((currentEndPos.getRow() <= 8 && currentEndPos.getRow() >= 1) && (currentEndPos.getColumn() <= 8 && currentEndPos.getColumn() >= 1 && pieceFlag2 == 0) ) {
                if (board.getPiece(currentEndPos) == null) {
                    possibleMoves.add(new ChessMove(myPosition, currentEndPos, null));
                }
                if(board.getPiece(currentEndPos) != null){
                    pieceFlag2 = 1;
                    if(board.getPiece(currentEndPos).pieceColor != this.pieceColor) {
                        possibleMoves.add(new ChessMove(myPosition, currentEndPos, null));
                    }
                }
            }
            currentEndPos = new ChessPosition(myPosition.getRow()+i, myPosition.getColumn()-i);
            if((currentEndPos.getRow() <= 8 && currentEndPos.getRow() >= 1) && (currentEndPos.getColumn() <= 8 && currentEndPos.getColumn() >= 1 && pieceFlag3 == 0) ) {
                if (board.getPiece(currentEndPos) == null) {
                    possibleMoves.add(new ChessMove(myPosition, currentEndPos, null));
                }
                if(board.getPiece(currentEndPos) != null){
                    pieceFlag3 = 1;
                    if(board.getPiece(currentEndPos).pieceColor != this.pieceColor) {
                        possibleMoves.add(new ChessMove(myPosition, currentEndPos, null));
                    }
                }
            }
            currentEndPos = new ChessPosition(myPosition.getRow()-i, myPosition.getColumn()-i);
            if((currentEndPos.getRow() <= 8 && currentEndPos.getRow() >= 1) && (currentEndPos.getColumn() <= 8 && currentEndPos.getColumn() >= 1 && pieceFlag4 == 0) ) {
                if (board.getPiece(currentEndPos) == null) {
                    possibleMoves.add(new ChessMove(myPosition, currentEndPos, null));
                }
                if(board.getPiece(currentEndPos) != null){
                    pieceFlag4 = 1;
                    if(board.getPiece(currentEndPos).pieceColor != this.pieceColor) {
                        possibleMoves.add(new ChessMove(myPosition, currentEndPos, null));
                    }
                }
            }
            i = i + 1;
        }
        return possibleMoves;
    }
    public Collection<ChessMove> pieceMovesKing(ChessBoard board, ChessPosition myPosition){
        HashSet<ChessMove> possibleMoves = new HashSet<>();
        ChessPosition currEndPosition = new ChessPosition(myPosition.getRow()+1, myPosition.getColumn());
        if ((currEndPosition.getRow()>=1 && currEndPosition.getRow()<=8) && (currEndPosition.getColumn()>=1 && currEndPosition.getColumn()<=8)){
            if (board.getPiece(currEndPosition) == null || board.getPiece(currEndPosition).pieceColor != this.pieceColor){
                possibleMoves.add(new ChessMove(myPosition, currEndPosition, null));
            }
        }
        currEndPosition = new ChessPosition(myPosition.getRow()-1, myPosition.getColumn());
        if ((currEndPosition.getRow()>=1 && currEndPosition.getRow()<=8) && (currEndPosition.getColumn()>=1 && currEndPosition.getColumn()<=8)){
            if (board.getPiece(currEndPosition) == null || board.getPiece(currEndPosition).pieceColor != this.pieceColor){
                possibleMoves.add(new ChessMove(myPosition, currEndPosition, null));
            }
        }
        currEndPosition = new ChessPosition(myPosition.getRow(), myPosition.getColumn()+1);
        if ((currEndPosition.getRow()>=1 && currEndPosition.getRow()<=8) && (currEndPosition.getColumn()>=1 && currEndPosition.getColumn()<=8)){
            if (board.getPiece(currEndPosition) == null || board.getPiece(currEndPosition).pieceColor != this.pieceColor){
                possibleMoves.add(new ChessMove(myPosition, currEndPosition, null));
            }
        }
        currEndPosition = new ChessPosition(myPosition.getRow(), myPosition.getColumn()-1);
        if ((currEndPosition.getRow()>=1 && currEndPosition.getRow()<=8) && (currEndPosition.getColumn()>=1 && currEndPosition.getColumn()<=8)){
            if (board.getPiece(currEndPosition) == null || board.getPiece(currEndPosition).pieceColor != this.pieceColor){
                possibleMoves.add(new ChessMove(myPosition, currEndPosition, null));
            }
        }
        currEndPosition = new ChessPosition(myPosition.getRow()-1, myPosition.getColumn()+1);
        if ((currEndPosition.getRow()>=1 && currEndPosition.getRow()<=8) && (currEndPosition.getColumn()>=1 && currEndPosition.getColumn()<=8)){
            if (board.getPiece(currEndPosition) == null || board.getPiece(currEndPosition).pieceColor != this.pieceColor){
                possibleMoves.add(new ChessMove(myPosition, currEndPosition, null));
            }
        }
        currEndPosition = new ChessPosition(myPosition.getRow()+1, myPosition.getColumn()+1);
        if ((currEndPosition.getRow()>=1 && currEndPosition.getRow()<=8) && (currEndPosition.getColumn()>=1 && currEndPosition.getColumn()<=8)){
            if (board.getPiece(currEndPosition) == null || board.getPiece(currEndPosition).pieceColor != this.pieceColor){
                possibleMoves.add(new ChessMove(myPosition, currEndPosition, null));
            }
        }
        currEndPosition = new ChessPosition(myPosition.getRow()+1, myPosition.getColumn()-1);
        if ((currEndPosition.getRow()>=1 && currEndPosition.getRow()<=8) && (currEndPosition.getColumn()>=1 && currEndPosition.getColumn()<=8)){
            if (board.getPiece(currEndPosition) == null || board.getPiece(currEndPosition).pieceColor != this.pieceColor){
                possibleMoves.add(new ChessMove(myPosition, currEndPosition, null));
            }
        }
        currEndPosition = new ChessPosition(myPosition.getRow()-1, myPosition.getColumn()-1);
        if ((currEndPosition.getRow()>=1 && currEndPosition.getRow()<=8) && (currEndPosition.getColumn()>=1 && currEndPosition.getColumn()<=8)){
            if (board.getPiece(currEndPosition) == null || board.getPiece(currEndPosition).pieceColor != this.pieceColor){
                possibleMoves.add(new ChessMove(myPosition, currEndPosition, null));
            }
        }
        return possibleMoves;
    }
}
