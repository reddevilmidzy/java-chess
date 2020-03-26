package chess.domain.piece;

import java.util.List;

public class Knight extends Piece {
    public Knight(Position position, Color color) {
        super(position, color);
    }

    @Override
    protected List<Direction> movableDirections(Piece piece, Direction direction) {
        return Direction.knightDirection();
    }

    @Override
    protected Direction findDirection(int x, int y) {
        return Direction.of(x, y);
    }
}
