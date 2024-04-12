package ui;

import chess.*;

import java.util.Collection;

public class BoardPrinter {
    public static void main(String[] args) {
        ChessGame game = new ChessGame();
        BoardPrinter printer = new BoardPrinter();
        System.out.println(printer.printChessboard(game));
    }

    public StringBuilder printSide(ChessGame game, boolean isWhiteOnTop, Collection<ChessMove> validMoves) {
        var board = game.getBoard();
        StringBuilder boardString = new StringBuilder();
        boardString.append("\n\n");
        boolean isWhite = true;
        boolean isValidMove = false;
        ChessPosition startPosition = null;
        if (validMoves != null) {
            for (var move : validMoves){
                startPosition = move.getStartPosition();
                break;
            }
        }

        // Determine row range based on side
        int startRow = isWhiteOnTop ? 1 : 8;
        int endRow = isWhiteOnTop ? 9 : 0;
        int rowIncrement = isWhiteOnTop ? 1 : -1;

        // Header
        boardString.append(EscapeSequences.RESET_BG_COLOR);
        if (isWhiteOnTop) {
            boardString.append(EscapeSequences.SET_BG_COLOR_BLACK).append(String.format("%3s", " "))
                    .append("   a  b   c   d  e   f   g   h");
        } else {
            boardString.append(EscapeSequences.SET_BG_COLOR_BLACK).append(String.format("%3s", " "))
                    .append("   a  b   c   d  e   f   g   h");
        }
        boardString.append(EscapeSequences.RESET_BG_COLOR).append("\n");

        // Board
        for (int i = startRow; i != endRow; i += rowIncrement) {
            boardString.append(EscapeSequences.RESET_BG_COLOR).append("\n");
            boardString.append(i).append(EscapeSequences.EMPTY);

            for (int j = 1; j < 9; j++) {
                if (validMoves != null && startPosition != null ) {
                    for (var move : validMoves) {
                        if ((move.getEndPosition().getRow() == i && move.getEndPosition().getColumn() == j)) {
                            isValidMove = true;
                            break;
                        }
                    }

                }
                if (isWhite) {
                    if (isValidMove){
                        isValidMove = false;
                        boardString.append(EscapeSequences.SET_BG_COLOR_RED);
                    }else {
                        boardString.append(EscapeSequences.SET_BG_COLOR_LIGHT_GREY);
                    }
                    isWhite = false;
                } else {
                    if (isValidMove){
                        isValidMove = false;
                        boardString.append(EscapeSequences.SET_BG_COLOR_RED);
                    }else {
                        boardString.append(EscapeSequences.SET_BG_COLOR_DARK_GREY);
                    }
                    isWhite = true;
                }

                ChessPiece currPiece = board.getPiece(new ChessPosition(i, j));
                if (currPiece != null) {
                    if (currPiece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                        switch (currPiece.getPieceType()) {
                            case PAWN -> boardString.append(EscapeSequences.BLACK_PAWN);
                            case BISHOP -> boardString.append(EscapeSequences.BLACK_BISHOP);
                            case KNIGHT -> boardString.append(EscapeSequences.BLACK_KNIGHT);
                            case ROOK -> boardString.append(EscapeSequences.BLACK_ROOK);
                            case QUEEN -> boardString.append(EscapeSequences.BLACK_QUEEN);
                            default -> boardString.append(EscapeSequences.BLACK_KING);
                        }
                    } else {
                        switch (currPiece.getPieceType()) {
                            case PAWN -> boardString.append(EscapeSequences.WHITE_PAWN);
                            case BISHOP -> boardString.append(EscapeSequences.WHITE_BISHOP);
                            case KNIGHT -> boardString.append(EscapeSequences.WHITE_KNIGHT);
                            case ROOK -> boardString.append(EscapeSequences.WHITE_ROOK);
                            case QUEEN -> boardString.append(EscapeSequences.WHITE_QUEEN);
                            default -> boardString.append(EscapeSequences.WHITE_KING);
                        }
                    }
                } else {
                    boardString.append(EscapeSequences.EMPTY);
                }
            }

            if (isWhite) {
                boardString.append(EscapeSequences.SET_BG_COLOR_WHITE);
                isWhite = false;
            } else {
                boardString.append(EscapeSequences.SET_BG_COLOR_BLACK);
                isWhite = true;
            }
        }

        boardString.append("\n\n");
        return boardString;
    }

    public String printChessboard(ChessGame game) {
        StringBuilder printString = new StringBuilder();
        printString.append(printSide(game, true, null));
        printString.append(printSide(game, false, null));
        return printString.toString();
    }
}

