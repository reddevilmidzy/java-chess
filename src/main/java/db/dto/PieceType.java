package db.dto;

import constant.ErrorCode;
import exception.MessageDoesNotExistException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import model.Camp;
import model.piece.Bishop;
import model.piece.BlackPawn;
import model.piece.King;
import model.piece.Knight;
import model.piece.Piece;
import model.piece.Queen;
import model.piece.Rook;
import model.piece.WhitePawn;

public enum PieceType {

    WHITE_KING(new King(Camp.WHITE), "King"),
    WHITE_QUEEN(new Queen(Camp.WHITE), "Queen"),
    WHITE_ROOK(new Rook(Camp.WHITE), "Rook"),
    WHITE_BISHOP(new Bishop(Camp.WHITE), "Bishop"),
    WHITE_KNIGHT(new Knight(Camp.WHITE), "Knight"),
    WHITE_PAWN(new WhitePawn(), "Pawn"),
    BLACK_KING(new King(Camp.BLACK), "King"),
    BLACK_QUEEN(new Queen(Camp.BLACK), "Queen"),
    BLACK_ROOK(new Rook(Camp.BLACK), "Rook"),
    BLACK_BISHOP(new Bishop(Camp.BLACK), "Bishop"),
    BLACK_KNIGHT(new Knight(Camp.BLACK), "Knight"),
    BLACK_PAWN(new BlackPawn(), "Pawn");

    private static final Map<Piece, PieceType> SUIT_PIECE_TYPE = Arrays.stream(values())
            .collect(Collectors.toMap(PieceType::getPiece, Function.identity()));

    private final Piece piece;
    private final String pieceName;

    PieceType(final Piece piece, final String pieceName) {
        this.piece = piece;
        this.pieceName = pieceName;
    }

    public static PieceType findByPiece(final Piece target) {
        if (SUIT_PIECE_TYPE.containsKey(target)) {
            return SUIT_PIECE_TYPE.get(target);
        }
        throw new MessageDoesNotExistException(ErrorCode.NO_MESSAGE);
    }

    public static PieceType findByCampAndType(final Camp camp, final String type) {
        final List<PieceType> pieceTypes = Arrays.stream(values())
                .filter(pieceType -> pieceType.pieceName.equals(type))
                .toList();
        return pieceTypes.stream()
                .filter(pieceType -> pieceType.piece.isSameCamp(camp))
                .findFirst()
                .orElseThrow();
    }

    public String getPieceName() {
        return pieceName;
    }

    public Piece getPiece() {
        return piece;
    }
}
