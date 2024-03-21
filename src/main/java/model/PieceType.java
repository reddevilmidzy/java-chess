package model;

import model.piece.Bishop;
import model.piece.King;
import model.piece.Knight;
import model.piece.Pawn;
import model.piece.Piece;
import model.piece.Queen;
import model.piece.Rook;

public enum PieceType {
    BISHOP_BLACK(new Bishop(Camp.BLACK), "B"),
    BISHOP_WHITE(new Bishop(Camp.WHITE), "b"),
    KING_BLACK(new King(Camp.BLACK), "K"),
    KING_WHITE(new King(Camp.WHITE), "k"),
    KNIGHT_BLACK(new Knight(Camp.BLACK), "N"),
    KNIGHT_WHITE(new Knight(Camp.WHITE), "n"),
    PAWN_BLACK(new Pawn(Camp.BLACK), "P"),
    PAWN_WHITE(new Pawn(Camp.WHITE), "p"),
    QUEEN_BLACK(new Queen(Camp.BLACK), "Q"),
    QUEEN_WHITE(new Queen(Camp.WHITE), "q"),
    ROOK_BLACK(new Rook(Camp.BLACK), "R"),
    ROOK_WHITE(new Rook(Camp.WHITE), "r");
    ;

    private final Piece piece;
    private final String value;

    PieceType(Piece piece, String value) {
        this.piece = piece;
        this.value = value;
    }

    public static PieceType from(Piece target) {
        for (PieceType pieceType : values()) {
            if (pieceType.piece.equals(target)) {
                return pieceType;
            }
        }
        throw new IllegalArgumentException("없음");
    }

    public String getValue() {
        return value;
    }
}
