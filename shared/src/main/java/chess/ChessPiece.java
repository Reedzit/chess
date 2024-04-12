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

    public Collection<ChessMove> pieceMovesPawn(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> possibleMoves = new HashSet<>();

        int direction = (this.pieceColor == ChessGame.TeamColor.WHITE) ? 1 : -1;
        int startRow = (this.pieceColor == ChessGame.TeamColor.WHITE) ? 2 : 7;
        int promoteRow = (this.pieceColor == ChessGame.TeamColor.WHITE) ? 8 : 1;

        // Move forward
        ChessPosition forwardOne = new ChessPosition(myPosition.getRow() + direction, myPosition.getColumn());
        if (board.getPiece(forwardOne) == null) {
            addPawnMove(board, myPosition, forwardOne, promoteRow, possibleMoves);

            // Move two steps if at starting position
            if (myPosition.getRow() == startRow) {
                ChessPosition forwardTwo = new ChessPosition(myPosition.getRow() + 2 * direction, myPosition.getColumn());
                if (board.getPiece(forwardTwo) == null && board.getPiece(forwardOne) == null) {
                    possibleMoves.add(new ChessMove(myPosition, forwardTwo, null));
                }
            }
        }

        // Attacks
        checkPawnAttack(board, myPosition, direction, 1, promoteRow, possibleMoves);
        checkPawnAttack(board, myPosition, direction, -1, promoteRow, possibleMoves);

        return possibleMoves;
    }

    private void addPawnMove(ChessBoard board, ChessPosition myPosition, ChessPosition endPosition, int promoteRow, Collection<ChessMove> moves) {
        if (endPosition.getRow() == promoteRow) {
            for (var piece : this.promotionTypes) {
                moves.add(new ChessMove(myPosition, endPosition, piece));
            }
        } else {
            moves.add(new ChessMove(myPosition, endPosition, null));
        }
    }

    private void checkPawnAttack(ChessBoard board, ChessPosition myPosition, int direction, int columnOffset, int promoteRow, Collection<ChessMove> moves) {
        int targetRow = myPosition.getRow() + direction;
        int targetColumn = myPosition.getColumn() + columnOffset;

        if (isValidPosition(targetRow, targetColumn)) {
            ChessPosition attackPosition = new ChessPosition(targetRow, targetColumn);
            ChessPiece targetPiece = board.getPiece(attackPosition);

            if (targetPiece != null && targetPiece.pieceColor != this.pieceColor) {
                addPawnMove(board, myPosition, attackPosition, promoteRow, moves);
            }
        }
    }

    public Collection<ChessMove> pieceMovesRook(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> possibleMoves = new HashSet<>();

        // Define directions for rook movement: up, down, left, right
        int[][] directions = { {-1, 0}, {1, 0}, {0, -1}, {0, 1} };

        for (int[] dir : directions) {
            int dRow = dir[0];
            int dCol = dir[1];
            int row = myPosition.getRow() + dRow;
            int col = myPosition.getColumn() + dCol;

            while (isValidPosition(row, col)) {
                ChessPosition currEndPosition = new ChessPosition(row, col);
                ChessPiece pieceAtPosition = board.getPiece(currEndPosition);

                if (pieceAtPosition != null) {
                    if (pieceAtPosition.pieceColor != this.pieceColor) {
                        possibleMoves.add(new ChessMove(myPosition, currEndPosition, null));
                    }
                    break;
                } else {
                    possibleMoves.add(new ChessMove(myPosition, currEndPosition, null));
                }

                row += dRow;
                col += dCol;
            }
        }
        return possibleMoves;
    }

    // Helper method to check if a position is within the board bounds (1-8 for both row and column)
    private boolean isValidPosition(int row, int col) {
        return row >= 1 && row <= 8 && col >= 1 && col <= 8;
    }

    public Collection<ChessMove> pieceMovesKnight(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> possibleMoves = new HashSet<>();

        // Define all possible knight moves relative to the current position
        int[][] knightMoves = {
                {1, 2}, {1, -2}, {-1, 2}, {-1, -2},
                {2, 1}, {2, -1}, {-2, 1}, {-2, -1}
        };

        for (int[] move : knightMoves) {
            int newRow = myPosition.getRow() + move[0];
            int newCol = myPosition.getColumn() + move[1];

            // Check if the new position is within the board bounds
            if (isValidPosition(newRow, newCol)) {
                ChessPosition newPos = new ChessPosition(newRow, newCol);
                ChessPiece pieceAtNewPos = board.getPiece(newPos);

                // Check if the position is empty or contains an opponent's piece
                if (pieceAtNewPos == null || pieceAtNewPos.pieceColor != this.pieceColor) {
                    possibleMoves.add(new ChessMove(myPosition, newPos, null));
                }
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
