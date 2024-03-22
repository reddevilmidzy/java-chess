package model.piece;

import constant.ErrorCode;
import exception.InvalidMovingException;
import java.util.Set;
import model.Camp;
import model.position.Moving;
import model.position.Position;
import model.position.Row;
import view.message.PieceType;

public class Pawn extends Piece {

    public Pawn(final Camp camp) {
        super(camp);
    }

    @Override
    public Set<Position> getMoveRoute(final Moving moving) {
        final Position currentPosition = moving.getCurrentPosition();
        final Position nextPosition = moving.getNextPosition();
        if (!canMovable(moving)) {
            throw new InvalidMovingException(ErrorCode.INVALID_MOVEMENT_RULE);
        }
        if (Math.abs(nextPosition.getRowIndex() - currentPosition.getRowIndex()) == 1) {
            return Set.of();
        }
        return getTwoStraightRoute(currentPosition);
    }

    private Set<Position> getTwoStraightRoute(final Position currentPosition) {
        if (Camp.BLACK == camp) {
            return Set.of(new Position(currentPosition.getColumn(), Row.SIX));
        }
        return Set.of(new Position(currentPosition.getColumn(), Row.THREE));
    }

    @Override
    protected boolean canMovable(final Moving moving) {
        if (moving.isNotMoved()) {
            return false;
        }
        final Position currentPosition = moving.getCurrentPosition();
        final Position nextPosition = moving.getNextPosition();
        final int dRow = currentPosition.getRowIndex() - nextPosition.getRowIndex();
        final int dColumn = currentPosition.getColumnIndex() - nextPosition.getColumnIndex();
        return isStraight(currentPosition, dColumn, dRow);
    }

    private boolean isStraight(final Position currentPosition, final int dColumn, final int dRow) {
        if (dColumn != 0) {
            return false;
        }
        if (Camp.BLACK == camp) {
            return isBlackTwoStraight(currentPosition, dRow);
        }
        if (Row.TWO.getIndex() == currentPosition.getRowIndex() && dRow == 2) {
            return true;
        }
        return dRow == 1;
    }

    private boolean isBlackTwoStraight(final Position currentPosition, final int dRow) {
        if (Row.SEVEN.getIndex() == currentPosition.getRowIndex() && dRow == -2) {
            return true;
        }
        return dRow == -1;
    }

    @Override
    public Set<Position> getAttackRoute(final Moving moving) {
        if (!canAttack(moving)) {
            throw new InvalidMovingException(ErrorCode.INVALID_MOVEMENT_RULE);
        }
        return Set.of();
    }

    private boolean canAttack(final Moving moving) {
        if (moving.isNotMoved()) {
            return false;
        }
        final Position currentPosition = moving.getCurrentPosition();
        final Position nextPosition = moving.getNextPosition();
        final int dRow = currentPosition.getRowIndex() - nextPosition.getRowIndex();
        final int dColumn = currentPosition.getColumnIndex() - nextPosition.getColumnIndex();
        return isDiagonal(dColumn, dRow);
    }

    private boolean isDiagonal(final int dColumn, final int dRow) {
        if (Math.abs(dColumn) != 1) {
            return false;
        }
        if (Camp.BLACK == camp) {
            return dRow == -1;
        }
        return dRow == 1;
    }

    @Override
    public String toString() {
        return PieceType.from(this).getValue();
    }
}
