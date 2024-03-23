package model;

import static model.Fixtures.A1;
import static model.Fixtures.A2;
import static model.Fixtures.A3;
import static model.Fixtures.A6;
import static model.Fixtures.A7;
import static model.Fixtures.A8;
import static model.Fixtures.B1;
import static model.Fixtures.B2;
import static model.Fixtures.B7;
import static model.Fixtures.B8;
import static model.Fixtures.C1;
import static model.Fixtures.C2;
import static model.Fixtures.C7;
import static model.Fixtures.C8;
import static model.Fixtures.D1;
import static model.Fixtures.D2;
import static model.Fixtures.D7;
import static model.Fixtures.D8;
import static model.Fixtures.E1;
import static model.Fixtures.E2;
import static model.Fixtures.E4;
import static model.Fixtures.E5;
import static model.Fixtures.E6;
import static model.Fixtures.E7;
import static model.Fixtures.E8;
import static model.Fixtures.F1;
import static model.Fixtures.F2;
import static model.Fixtures.F3;
import static model.Fixtures.F7;
import static model.Fixtures.F8;
import static model.Fixtures.G1;
import static model.Fixtures.G2;
import static model.Fixtures.G7;
import static model.Fixtures.G8;
import static model.Fixtures.H1;
import static model.Fixtures.H2;
import static model.Fixtures.H7;
import static model.Fixtures.H8;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import exception.InvalidTurnException;
import exception.PieceDoesNotExistException;
import exception.PieceExistInRouteException;
import java.util.HashMap;
import java.util.Map;
import model.piece.Bishop;
import model.piece.King;
import model.piece.Knight;
import model.piece.Pawn;
import model.piece.Piece;
import model.piece.Queen;
import model.piece.Rook;
import model.position.Moving;
import model.position.Position;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ChessBoardTest {

    @Test
    @DisplayName("초기에는 32개의 기물이 생성된다.")
    void initPieces() {
        //given
        final ChessBoard chessBoard = new ChessBoard();

        //when
        chessBoard.setting();

        //then
        final Map<Position, Piece> board = chessBoard.getBoard();
        assertThat(board.keySet()).hasSize(32);
    }

    @Test
    @DisplayName("기물들의 시작 위치를 확인한다.")
    void checkInitialPosition() {
        //given
        final ChessBoard chessBoard = new ChessBoard();

        //when
        chessBoard.setting();
        final Map<Position, Piece> board = chessBoard.getBoard();

        final Map<Position, Piece> expected = new HashMap<>();

        // black
        expected.put(A8, new Rook(Camp.BLACK));
        expected.put(B8, new Knight(Camp.BLACK));
        expected.put(C8, new Bishop(Camp.BLACK));
        expected.put(D8, new Queen(Camp.BLACK));
        expected.put(E8, new King(Camp.BLACK));
        expected.put(F8, new Bishop(Camp.BLACK));
        expected.put(G8, new Knight(Camp.BLACK));
        expected.put(H8, new Rook(Camp.BLACK));
        expected.put(A7, new Pawn(Camp.BLACK));
        expected.put(B7, new Pawn(Camp.BLACK));
        expected.put(C7, new Pawn(Camp.BLACK));
        expected.put(D7, new Pawn(Camp.BLACK));
        expected.put(E7, new Pawn(Camp.BLACK));
        expected.put(F7, new Pawn(Camp.BLACK));
        expected.put(G7, new Pawn(Camp.BLACK));
        expected.put(H7, new Pawn(Camp.BLACK));

        //white
        expected.put(A1, new Rook(Camp.WHITE));
        expected.put(B1, new Knight(Camp.WHITE));
        expected.put(C1, new Bishop(Camp.WHITE));
        expected.put(D1, new Queen(Camp.WHITE));
        expected.put(E1, new King(Camp.WHITE));
        expected.put(F1, new Bishop(Camp.WHITE));
        expected.put(G1, new Knight(Camp.WHITE));
        expected.put(H1, new Rook(Camp.WHITE));
        expected.put(A2, new Pawn(Camp.WHITE));
        expected.put(B2, new Pawn(Camp.WHITE));
        expected.put(C2, new Pawn(Camp.WHITE));
        expected.put(D2, new Pawn(Camp.WHITE));
        expected.put(E2, new Pawn(Camp.WHITE));
        expected.put(F2, new Pawn(Camp.WHITE));
        expected.put(G2, new Pawn(Camp.WHITE));
        expected.put(H2, new Pawn(Camp.WHITE));

        //then
        assertThat(board).isEqualTo(expected);
    }

    @Test
    @DisplayName("기물이 없는 위치가 주어졌을 때 예외가 발생한다.")
    void blankPosition() {
        //given
        final ChessBoard chessBoard = new ChessBoard();
        chessBoard.setting();

        final Moving moving = new Moving(E4, E5);

        //when & then
        assertThatThrownBy(() -> chessBoard.move(moving))
                .isInstanceOf(PieceDoesNotExistException.class);
    }

    @DisplayName("이동 경로에 다른 기물이 있으면 예외를 발생시킨다.")
    @Test
    void routeContainPiece() {
        //given
        final ChessBoard chessBoard = new ChessBoard();
        chessBoard.setting();

        final Map<Position, Piece> board = chessBoard.getBoard();
        board.put(E6, new Queen(Camp.BLACK));
        chessBoard.move(new Moving(A2, A3));

        //when && then
        final Moving moving = new Moving(E7, E5);

        assertThatThrownBy(() -> chessBoard.move(moving))
                .isInstanceOf(PieceExistInRouteException.class);
    }

    //TODO 폰 테스트 필히 추가

    @DisplayName("도착 지점에 같은 진영의 기물이 있으면 예외를 발생시킨다.")
    @Test
    void targetPositionIsEqualCamp() {
        //given
        final ChessBoard chessBoard = new ChessBoard();
        chessBoard.setting();

        final Map<Position, Piece> board = chessBoard.getBoard();
        board.put(F3, new Queen(Camp.WHITE));

        //when && then
        final Moving moving = new Moving(G1, F3);

        assertThatThrownBy(() -> chessBoard.move(moving))
                .isInstanceOf(PieceExistInRouteException.class);
    }

    @Test
    @DisplayName("상대방의 기물을 이동시키려 하면 예외가 발생한다.")
    void invalidTurn() {
        //given
        final ChessBoard chessBoard = new ChessBoard();
        chessBoard.setting();

        //when && then
        final Moving moving = new Moving(A7, A6);

        assertThatThrownBy(() -> chessBoard.move(moving))
                .isInstanceOf(InvalidTurnException.class);
    }
}
