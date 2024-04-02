package model;

public enum Camp {

    BLACK,
    WHITE;

    public static Camp calculateTurn(final int turn) {
        if (turn % 2 == 0) {
            return WHITE;
        }
        return BLACK;
    }

    public Camp toggle() {
        if (this == BLACK) {
            return WHITE;
        }
        return BLACK;
    }
}
