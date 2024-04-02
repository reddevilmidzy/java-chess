package db.dto;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import model.Board;
import model.piece.Piece;
import model.position.Position;

public record BoardDto(Map<PositionDto, PieceDto> pieces) {

    public static BoardDto from(final Board board) {
        final Map<PositionDto, PieceDto> result = new HashMap<>();
        final Map<Position, Piece> pieces = board.getPieces();

        for (Entry<Position, Piece> entry : pieces.entrySet()) {
            final PositionDto position = PositionDto.from(entry.getKey());
            final PieceDto piece = PieceDto.from(entry.getValue());
            result.put(position, piece);
        }
        return new BoardDto(result);
    }

    public static BoardDto create() {
        return from(Board.create());
    }

    public Board toBoard() {
        final Map<Position, Piece> result = new HashMap<>();
        for (Entry<PositionDto, PieceDto> entry : pieces.entrySet()) {
            result.put(entry.getKey().toPosition(), entry.getValue().toPiece());
        }
        return new Board(result);
    }
}
