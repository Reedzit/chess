package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

public class BoardPrinter {
    public static void main(String[] args) {
        ChessBoard board = new ChessBoard();
        board.resetBoard();
//        System.out.println(board);
        printChessboard(board);
    }

    public static void printChessboard(ChessBoard board) {
        StringBuilder boardString = new StringBuilder();
        boolean isWhite = true;
        for (int i = 1; i < 9; i++) {
            boardString.append(EscapeSequences.RESET_BG_COLOR);
            boardString.append("\n");
            for (int j = 1; j < 9; j++) {
                if (isWhite){
                    //change bg color
                    boardString.append(EscapeSequences.SET_BG_COLOR_LIGHT_GREY);
                    isWhite = false;
                }else{
                    boardString.append(EscapeSequences.SET_BG_COLOR_DARK_GREY);
                    isWhite = true;
                }
                ChessPiece currPiece = board.getPiece(new ChessPosition(i,j));
                if (currPiece != null){
                    //switch statement
                    if (currPiece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                        switch (currPiece.getPieceType()) {
                            case ChessPiece.PieceType.PAWN -> boardString.append(EscapeSequences.WHITE_PAWN);
                            case ChessPiece.PieceType.BISHOP -> boardString.append(EscapeSequences.WHITE_BISHOP);
                            case ChessPiece.PieceType.KNIGHT -> boardString.append(EscapeSequences.WHITE_KNIGHT);
                            case ChessPiece.PieceType.ROOK -> boardString.append(EscapeSequences.WHITE_ROOK);
                            case ChessPiece.PieceType.QUEEN -> boardString.append(EscapeSequences.WHITE_QUEEN);
                            default -> boardString.append(EscapeSequences.WHITE_KING);
                        }
                    }else {
                        switch (currPiece.getPieceType()) {
                            case ChessPiece.PieceType.PAWN -> boardString.append(EscapeSequences.BLACK_PAWN);
                            case ChessPiece.PieceType.BISHOP -> boardString.append(EscapeSequences.BLACK_BISHOP);
                            case ChessPiece.PieceType.KNIGHT -> boardString.append(EscapeSequences.BLACK_KNIGHT);
                            case ChessPiece.PieceType.ROOK -> boardString.append(EscapeSequences.BLACK_ROOK);
                            case ChessPiece.PieceType.QUEEN -> boardString.append(EscapeSequences.BLACK_QUEEN);
                            default -> boardString.append(EscapeSequences.BLACK_KING);
                        }
                    }
                }else{
                    //print empty
                    boardString.append(EscapeSequences.EMPTY);
                }

            }
            if (isWhite){
                //change bg color
                boardString.append(EscapeSequences.SET_BG_COLOR_WHITE);
                isWhite = false;
            }else{
                boardString.append(EscapeSequences.SET_BG_COLOR_BLACK);
                isWhite = true;
            }
        }
        boardString.append("\n\n");
        for (int i = 8; i > 0; i--) {
            boardString.append(EscapeSequences.RESET_BG_COLOR);
            boardString.append("\n");
            for (int j = 8; j > 0; j--) {
                if (isWhite){
                    //change bg color
                    boardString.append(EscapeSequences.SET_BG_COLOR_LIGHT_GREY);
                    isWhite = false;
                }else{
                    boardString.append(EscapeSequences.SET_BG_COLOR_DARK_GREY);
                    isWhite = true;
                }
                ChessPiece currPiece = board.getPiece(new ChessPosition(i,j));
                if (currPiece != null){
                    //switch statement
                    if (currPiece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                        switch (currPiece.getPieceType()) {
                            case ChessPiece.PieceType.PAWN -> boardString.append(EscapeSequences.WHITE_PAWN);
                            case ChessPiece.PieceType.BISHOP -> boardString.append(EscapeSequences.WHITE_BISHOP);
                            case ChessPiece.PieceType.KNIGHT -> boardString.append(EscapeSequences.WHITE_KNIGHT);
                            case ChessPiece.PieceType.ROOK -> boardString.append(EscapeSequences.WHITE_ROOK);
                            case ChessPiece.PieceType.QUEEN -> boardString.append(EscapeSequences.WHITE_QUEEN);
                            default -> boardString.append(EscapeSequences.WHITE_KING);
                        }
                    }else {
                        switch (currPiece.getPieceType()) {
                            case ChessPiece.PieceType.PAWN -> boardString.append(EscapeSequences.BLACK_PAWN);
                            case ChessPiece.PieceType.BISHOP -> boardString.append(EscapeSequences.BLACK_BISHOP);
                            case ChessPiece.PieceType.KNIGHT -> boardString.append(EscapeSequences.BLACK_KNIGHT);
                            case ChessPiece.PieceType.ROOK -> boardString.append(EscapeSequences.BLACK_ROOK);
                            case ChessPiece.PieceType.QUEEN -> boardString.append(EscapeSequences.BLACK_QUEEN);
                            default -> boardString.append(EscapeSequences.BLACK_KING);
                        }
                    }
                }else{
                    //print empty
                    boardString.append(EscapeSequences.EMPTY);
                }

            }
            if (isWhite){
                //change bg color
                boardString.append(EscapeSequences.SET_BG_COLOR_WHITE);
                isWhite = false;
            }else{
                boardString.append(EscapeSequences.SET_BG_COLOR_BLACK);
                isWhite = true;
            }
        }
        System.out.println(boardString);
    }
}

