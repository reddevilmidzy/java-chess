package model;

import static model.Fixtures.A2;
import static model.Fixtures.A4;
import static model.Fixtures.B4;
import static model.Fixtures.B5;
import static model.Fixtures.B7;
import static model.Fixtures.B8;
import static model.Fixtures.C2;
import static model.Fixtures.C3;
import static model.Fixtures.C6;
import static model.Fixtures.E2;
import static model.Fixtures.E8;
import static model.Fixtures.F4;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import exception.KingDeadException;
import java.util.Map;
import model.piece.King;
import model.piece.Piece;
import model.piece.Queen;
import model.piece.WhitePawn;
import model.position.Moving;
import model.position.Position;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class BoardTest {

    @DisplayName("초기 상태의 기물 점수를 계산한다.")
    @Test
    void calculateInitBoard() {
        //given
        final ChessGame chessGame = ChessGame.setupStartingPosition();
        final Score expected = new Score(38.0F);

        //when then
        assertAll(
                () -> assertThat(chessGame.calculateScore(Camp.WHITE)).isEqualTo(expected),
                () -> assertThat(chessGame.calculateScore(Camp.BLACK)).isEqualTo(expected)
        );
    }

    @Test
    @DisplayName("같은 세로줄에 폰이 있으면 0.5점이다.")
    void calculateBoardWhenPawnSameFile() {
        //given
        final ChessGame chessGame = ChessGame.setupStartingPosition();
        final Score blackExpected = new Score(37.0F);
        final Score whiteExpected = new Score(37.0F);

        //when
        chessGame.move(new Moving(A2, A4));
        chessGame.move(new Moving(B7, B5));
        chessGame.move(new Moving(A4, B5));

        //when then
        assertAll(
                () -> assertThat(chessGame.calculateScore(Camp.WHITE)).isEqualTo(whiteExpected),
                () -> assertThat(chessGame.calculateScore(Camp.BLACK)).isEqualTo(blackExpected)
        );
    }

    @Test
    @DisplayName("세로 줄에 3개의 폰이 있는 경우")
    void threePawn() {
        //given
        final ChessGame chessGame = ChessGame.setupStartingPosition();
        final Score blackExpected = new Score(34.5F);
        final Score whiteExpected = new Score(36.5F);

        //when
        chessGame.move(new Moving(A2, A4));
        chessGame.move(new Moving(B7, B5));
        chessGame.move(new Moving(A4, B5));
        chessGame.move(new Moving(B8, C6));
        chessGame.move(new Moving(C2, C3));
        chessGame.move(new Moving(C6, B4));
        chessGame.move(new Moving(C3, B4));

        /*
        R.BQKBNR  8
        P.PPPPPP  7
        ........  6
        .p......  5
        .p......  4
        ........  3
        .p.ppppp  2
        rnbqkbnr  1

        abcdefgh
         */

        //then
        assertAll(
                () -> assertThat(chessGame.calculateScore(Camp.WHITE)).isEqualTo(whiteExpected),
                () -> assertThat(chessGame.calculateScore(Camp.BLACK)).isEqualTo(blackExpected)
        );
    }

    @Test
    @DisplayName("getter로 가져온 값을 수정하려고 하면 예외가 발생한다.")
    void doNotUpdate() {
        //given
        final Board board = Board.create();
        final Piece piece = new WhitePawn();

        //when
        final Map<Position, Piece> pieces = board.getPieces();

        //then
        assertThatThrownBy(() -> pieces.put(F4, piece))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @DisplayName("킹을 잡으려 하면 예외가 발생한다.")
    @Test
    void failToCatchKing() {
        //given
        final Map<Position, Piece> pieces = Map.of(
                E8, new King(Camp.BLACK),
                E2, new Queen(Camp.WHITE));

        final Board board = new Board(pieces);

        final Moving moving = new Moving(E2, E8);
        /*
        ....K...  8
        ........  7
        ........  6
        ........  5
        ........  4
        ........  3
        ....q...  2
        ........  1

        abcdefgh
         */

        //when && then
        assertThatThrownBy(() -> board.validate(moving, Camp.WHITE))
                .isInstanceOf(KingDeadException.class);
    }
}
